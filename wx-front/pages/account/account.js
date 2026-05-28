const api = require('../../utils/api')

Page({
  data: {
    user: {},
    isLoggedIn: false,
    loginLoading: false,
    profileSaving: false,
    avatarText: '微',
    profileAvatarUrl: '',
    displayNickname: '微信用户',
    profileForm: {
      nickname: '',
      avatarUrl: ''
    },
    inviteCode: ''
  },

  onShow() {
    this.load()
  },

  load() {
    const app = getApp()
    this.setData({
      isLoggedIn: app.hasSession && app.hasSession(),
      inviteCode: this.data.inviteCode || (app.getPendingInvite && app.getPendingInvite()) || ''
    })
    if (!(app.hasSession && app.hasSession())) {
      this.setDefaultProfile()
      return
    }
    api.get('/bootstrap')
      .then((data) => this.applyUser(data.user || {}))
      .catch(() => {})
  },

  setDefaultProfile() {
    this.setData({
      user: {},
      isLoggedIn: false,
      avatarText: '微',
      profileAvatarUrl: '',
      displayNickname: '微信用户',
      profileForm: {
        nickname: '',
        avatarUrl: ''
      }
    })
  },

  applyUser(user) {
    user = {
      ...(user || {}),
      avatarUrl: api.assetUrl((user || {}).avatarUrl || '')
    }
    const isDefaultNickname = !user.nickname || user.nickname === '微信用户'
    const nickname = isDefaultNickname ? '' : user.nickname
    const displayNickname = user.nickname || '微信用户'
    this.setData({
      user,
      isLoggedIn: true,
      avatarText: this.firstText(displayNickname || '微'),
      displayNickname,
      profileAvatarUrl: user.avatarUrl || this.data.profileAvatarUrl,
      profileForm: {
        nickname: nickname || this.data.profileForm.nickname,
        avatarUrl: user.avatarUrl || this.data.profileForm.avatarUrl
      }
    })
  },

  profileNickname() {
    return (this.data.profileForm.nickname || '').trim() || (this.data.user.nickname || '微信用户')
  },

  firstText(value) {
    const text = `${value || ''}`.trim()
    return text ? Array.from(text)[0] : '微'
  },

  onNicknameInput(event) {
    this.setData({ 'profileForm.nickname': event.detail.value })
  },

  onInviteCodeInput(event) {
    this.setData({ inviteCode: this.normalizeInviteCode(event.detail.value) })
  },

  onChooseAvatar(event) {
    const avatarUrl = event.detail.avatarUrl || ''
    if (!avatarUrl) return
    this.setData({
      profileAvatarUrl: avatarUrl,
      'profileForm.avatarUrl': avatarUrl
    })
  },

  onPhoneLogin(event) {
    if (this.data.loginLoading) return
    const detail = event.detail || {}
    const phoneCode = detail.errMsg && detail.errMsg.indexOf(':ok') === -1 ? '' : (detail.code || '')
    this.login(phoneCode)
  },

  login(phoneCode) {
    if (this.data.loginLoading) return
    this.setData({ loginLoading: true })
    getApp().loginByWechat({ phoneCode })
      .then((loginData) => {
        if (!loginData || !loginData.user) {
          throw new Error('login failed')
        }
        this.applyUser(loginData.user)
        const inviteCode = this.normalizedInviteCode()
        if (inviteCode && getApp().setPendingInvite) {
          getApp().setPendingInvite(inviteCode)
        }
        return getApp().acceptPendingInvite().catch(() => null)
      })
      .then((inviteResult) => {
        wx.showToast({ title: inviteResult && inviteResult.joined ? '已加入空间' : '登录成功', icon: 'success' })
        setTimeout(() => wx.navigateBack(), 500)
      })
      .catch(() => {
        wx.showToast({ title: '登录失败', icon: 'none' })
      })
      .then(() => {
        this.setData({ loginLoading: false })
      })
  },

  saveProfile() {
    if (this.data.profileSaving) return
    const nickname = this.profileNickname()
    const avatarPath = this.data.profileForm.avatarUrl || ''
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: '请先完成登录', icon: 'none' })
      return
    }
    this.setData({ profileSaving: true })
    this.uploadAvatarIfNeeded(avatarPath)
      .then((avatarUrl) => api.put('/users/profile', { nickname, avatarUrl }))
      .then((user) => {
        this.applyUser(user)
        wx.showToast({ title: '资料已保存', icon: 'success' })
      })
      .catch(() => {
        wx.showToast({ title: '保存失败', icon: 'none' })
      })
      .then(() => {
        this.setData({ profileSaving: false })
      })
  },

  bindInviteCode() {
    const inviteCode = this.normalizedInviteCode()
    if (!inviteCode) {
      wx.showToast({ title: '请输入邀请码', icon: 'none' })
      return
    }
    const app = getApp()
    if (app.setPendingInvite) {
      app.setPendingInvite(inviteCode)
    }
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: '请先完成登录', icon: 'none' })
      return
    }
    app.acceptPendingInvite()
      .then((result) => {
        this.setData({ inviteCode: '' })
        wx.showToast({ title: result && result.joined ? '绑定成功' : '已在空间中', icon: 'success' })
      })
      .catch(() => {})
  },

  normalizedInviteCode() {
    return this.normalizeInviteCode(this.data.inviteCode)
  },

  normalizeInviteCode(value) {
    return `${value || ''}`.trim().replace(/\s/g, '')
  },

  uploadAvatarIfNeeded(avatarPath) {
    if (!avatarPath) {
      return Promise.resolve('')
    }
    if (this.isRemoteAvatar(avatarPath)) {
      return Promise.resolve(avatarPath)
    }
    return api.upload('/files/avatar', avatarPath, 'file')
      .then((result) => result.url || '')
      .then((url) => {
        this.setData({
          profileAvatarUrl: url,
          'profileForm.avatarUrl': url
        })
        return url
      })
  },

  isRemoteAvatar(avatarPath) {
    const path = `${avatarPath || ''}`
    const isHttp = path.indexOf('http://') === 0 || path.indexOf('https://') === 0
    const isDevtoolTemp = path.indexOf('http://tmp/') === 0 || path.indexOf('https://tmp/') === 0
    return isHttp && !isDevtoolTemp
  },

  skipLogin() {
    wx.navigateBack()
  },

  logout() {
    getApp().logout()
    this.setDefaultProfile()
    wx.showToast({ title: '已退出登录', icon: 'success' })
    setTimeout(() => wx.navigateBack(), 500)
  }
})
