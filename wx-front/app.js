App({
  onLaunch(options) {
    options = options || {}
    this.captureInvite(options)
    if (wx.getStorageSync('manualLogout')) {
      this.loginReady = Promise.resolve(null)
      return
    }
    this.loginReady = Promise.resolve(null)
  },

  onShow(options) {
    options = options || {}
    this.captureInvite(options)
  },

  loginWithWechat(profile) {
    profile = profile || {}
    const loginSeq = (this.loginSeq || 0) + 1
    this.loginSeq = loginSeq
    const loginTask = new Promise((resolve) => {
      wx.login({
        success: (loginRes) => {
          if (!loginRes.code) {
            this.clearSession(loginSeq)
            resolve(null)
            return
          }
          this.requestLogin(loginRes.code, profile, loginSeq)
            .then((data) => {
              if (this.loginSeq === loginSeq) {
                resolve(data)
                return
              }
              resolve(null)
            })
            .catch(() => {
              this.clearSession(loginSeq)
              resolve(null)
            })
        },
        fail: () => {
          this.clearSession(loginSeq)
          resolve(null)
        }
      })
    })
    this.loginReady = loginTask
    return loginTask
  },

  loginByWechat(profile) {
    profile = profile || {}
    wx.removeStorageSync('manualLogout')
    return this.loginWithWechat(profile)
  },

  requestLogin(code, profile, loginSeq) {
    profile = profile || {}
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${this.globalData.apiBase}/auth/login`,
        method: 'POST',
        timeout: 10000,
        data: {
          code,
          nickname: profile.nickName || profile.nickname || '',
          avatarUrl: profile.avatarUrl || '',
          phoneCode: profile.phoneCode || ''
        },
        header: {
          'content-type': 'application/json'
        },
        success: (res) => {
          const payload = res.data || {}
          if (payload.code !== 0 || !payload.data || !payload.data.user) {
            reject(payload)
            return
          }
          if (loginSeq && this.loginSeq !== loginSeq) {
            resolve(null)
            return
          }
          const user = payload.data.user
          this.globalData.userId = user.id
          this.globalData.token = payload.data.token || ''
          wx.setStorageSync('userId', user.id)
          wx.setStorageSync('token', this.globalData.token)
          resolve(payload.data)
        },
        fail: reject
      })
    })
  },

  clearSession(loginSeq) {
    if (loginSeq && this.loginSeq !== loginSeq) {
      return
    }
    this.globalData.userId = null
    this.globalData.token = ''
    wx.removeStorageSync('userId')
    wx.removeStorageSync('token')
  },

  logout() {
    wx.setStorageSync('manualLogout', true)
    this.loginSeq = (this.loginSeq || 0) + 1
    this.loginReady = Promise.resolve(null)
    this.clearSession()
  },

  hasSession() {
    return !!(wx.getStorageSync('userId') || this.globalData.userId)
  },

  captureInvite(options) {
    options = options || {}
    const query = options.query || options || {}
    const inviteCode = query.inviteCode || this.inviteCodeFromScene(query.scene)
    if (inviteCode) {
      this.setPendingInvite(inviteCode)
    }
  },

  inviteCodeFromScene(scene) {
    if (!scene) return ''
    let decoded = ''
    try {
      decoded = decodeURIComponent(scene)
    } catch (error) {
      decoded = String(scene || '')
    }
    if (decoded.indexOf('inviteCode=') > -1) {
      return decoded.split('inviteCode=')[1].split('&')[0]
    }
    return decoded
  },

  setPendingInvite(inviteCode) {
    wx.setStorageSync('pendingInviteCode', inviteCode)
    this.globalData.pendingInviteCode = inviteCode
  },

  getPendingInvite() {
    return wx.getStorageSync('pendingInviteCode') || this.globalData.pendingInviteCode || ''
  },

  clearPendingInvite() {
    this.globalData.pendingInviteCode = ''
    wx.removeStorageSync('pendingInviteCode')
  },

  acceptPendingInvite(options) {
    options = options || {}
    const silent = !!options.silent
    const inviteCode = this.getPendingInvite()
    const userId = wx.getStorageSync('userId') || this.globalData.userId
    if (!inviteCode || !userId) {
      return Promise.resolve(null)
    }
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${this.globalData.apiBase}/spaces/invites/accept`,
        method: 'POST',
        data: { inviteCode },
        header: {
          'content-type': 'application/json',
          'X-User-Id': userId
        },
        success: (res) => {
          const payload = res.data || {}
          if (payload.code === 0) {
            this.clearPendingInvite()
            resolve(payload.data)
            return
          }
          this.clearPendingInvite()
          if (!silent) {
            wx.showToast({ title: payload.message || '邀请绑定失败', icon: 'none' })
          }
          reject(payload)
        },
        fail: reject
      })
    })
  },

  globalData: {
    apiBase: 'https://couple.vikeatl.xyz/api/wx',
    assetBase: 'https://couple.vikeatl.xyz',
    userId: wx.getStorageSync('userId') || null,
    token: wx.getStorageSync('token') || '',
    pendingInviteCode: wx.getStorageSync('pendingInviteCode') || ''
  },

  loginSeq: 0
})
