const app = getApp()

const fallbackToast = (title) => {
  wx.showToast({
    title,
    icon: 'none'
  })
}

let loginPrompting = false

const promptLogin = (content = '登录后才能继续使用这个功能') => {
  if (loginPrompting) return Promise.resolve(false)
  loginPrompting = true
  return new Promise((resolve) => {
    wx.showModal({
      title: '需要登录',
      content,
      confirmText: '去登录',
      cancelText: '暂不',
      success: (res) => {
        if (res.confirm) {
          wx.navigateTo({ url: '/pages/account/account' })
        }
        resolve(!!res.confirm)
      },
      fail: () => resolve(false),
      complete: () => {
        loginPrompting = false
      }
    })
  })
}

const ensureLogin = (content) => {
  if (app.hasSession && app.hasSession()) {
    return true
  }
  promptLogin(content)
  return false
}

const handleNeedLogin = (error, content) => {
  if (error && error.needLogin) {
    promptLogin(content)
    return true
  }
  return false
}

const publicAssetBase = () => {
  const globalData = app.globalData || {}
  const configured = globalData.assetBase || (globalData.apiBase || '').replace(/\/api\/wx\/?$/, '')
  return (configured || 'https://couple.vikeatl.xyz').replace(/\/$/, '')
}

const assetUrl = (url) => {
  const value = `${url || ''}`.trim()
  if (!value) return ''
  if (value.indexOf('/assets/') === 0) return value
  if (value.indexOf('wxfile://') === 0) return value
  if (value.indexOf('http://tmp/') === 0 || value.indexOf('https://tmp/') === 0) return value
  const base = publicAssetBase()
  if (value.indexOf('/uploads/') === 0) return `${base}${value}`
  return value
    .replace(/^http:\/\/localhost:8081/i, base)
    .replace(/^http:\/\/127\.0\.0\.1:8081/i, base)
    .replace(/^https:\/\/84661ijbo277\.vicp\.fun/i, base)
    .replace(/^http:\/\/84661ijbo277\.vicp\.fun/i, base)
}

const isLoginError = (payload = {}) => {
  const message = payload.message || ''
  return payload.code === 401 || message.indexOf('用户不存在') > -1 || message.indexOf('登录') > -1
}

const rejectLogin = (reject, payload = {}, requestUserId) => {
  const currentUserId = wx.getStorageSync('userId') || app.globalData.userId
  if (currentUserId && String(currentUserId) !== String(requestUserId || '')) {
    reject({
      ...payload,
      staleLogin: true
    })
    return
  }
  app.clearSession && app.clearSession()
  reject({
    ...payload,
    needLogin: true
  })
}

const request = ({ url, method = 'GET', data = {} }) => {
  const waitLogin = app.loginReady && typeof app.loginReady.then === 'function'
    ? app.loginReady.catch(() => null)
    : Promise.resolve(null)
  return waitLogin.then(() => new Promise((resolve, reject) => {
    const userId = wx.getStorageSync('userId') || app.globalData.userId
    const header = {
      'content-type': 'application/json'
    }
    if (userId) {
      header['X-User-Id'] = userId
    }
    wx.request({
      url: `${app.globalData.apiBase}${url}`,
      method,
      data,
      header,
      success: (res) => {
        const payload = res.data || {}
        if (payload.code === 0) {
          resolve(payload.data)
          return
        }
        if (isLoginError(payload)) {
          rejectLogin(reject, payload, userId)
          return
        }
        fallbackToast(payload.message || '请求失败')
        reject(payload)
      },
      fail: (err) => {
        fallbackToast('后端连接失败，请先启动服务')
        reject(err)
      }
    })
  }))
}

const upload = ({ url, filePath, name = 'file', formData = {} }) => {
  const waitLogin = app.loginReady && typeof app.loginReady.then === 'function'
    ? app.loginReady.catch(() => null)
    : Promise.resolve(null)
  return waitLogin.then(() => new Promise((resolve, reject) => {
    const userId = wx.getStorageSync('userId') || app.globalData.userId
    const header = {}
    if (userId) {
      header['X-User-Id'] = userId
    }
    wx.uploadFile({
      url: `${app.globalData.apiBase}${url}`,
      filePath,
      name,
      formData,
      header,
      success: (res) => {
        let payload = {}
        try {
          payload = JSON.parse(res.data || '{}')
        } catch (error) {
          reject(error)
          return
        }
        if (payload.code === 0) {
          resolve(payload.data)
          return
        }
        if (isLoginError(payload)) {
          rejectLogin(reject, payload, userId)
          return
        }
        fallbackToast(payload.message || '上传失败')
        reject(payload)
      },
      fail: (err) => {
        fallbackToast('上传失败，请检查后端服务')
        reject(err)
      }
    })
  }))
}

module.exports = {
  get: (url, data) => request({ url, data }),
  post: (url, data) => request({ url, method: 'POST', data }),
  put: (url, data) => request({ url, method: 'PUT', data }),
  del: (url, data) => request({ url, method: 'DELETE', data }),
  upload: (url, filePath, name, formData) => upload({ url, filePath, name, formData }),
  assetUrl,
  ensureLogin,
  handleNeedLogin,
  promptLogin
}
