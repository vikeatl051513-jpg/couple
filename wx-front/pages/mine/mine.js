const api = require('../../utils/api')

Page({
  data: {
    user: {},
    space: {},
    stats: {},
    members: [],
    wishes: [],
    anniversaries: [],
    messages: [],
    activeDress: {},
    menuGroups: [],
    roleText: '',
    avatarText: '我',
    companionDays: 0,
    unlockedDressCount: 0,
    checkinToday: false,
    needLogin: false,
    profileAvatarUrl: '',
    inviteInfo: {},
    inviteLoading: false,
    showInviteSheet: false,
    inviteCodeInput: '',
    pendingTodoCount: 0,
    pendingOrderCount: 0
  },

  onShow() {
    this.load()
  },

  load() {
    api.get('/bootstrap')
      .then((data) => this.apply(data))
      .catch((err) => {
        const app = getApp()
        api.handleNeedLogin(err, '登录后才能查看我的空间')
        this.setData({ needLogin: !!err.needLogin && !(app.hasSession && app.hasSession()) })
      })
  },

  apply(data) {
    const groups = data.mineMenuGroups || []
    const profileStats = ((groups[0] || {}).stats) || {}
    const user = {
      ...(data.user || {}),
      avatarUrl: api.assetUrl((data.user || {}).avatarUrl || '')
    }
    const nickname = user.nickname === '微信用户' ? '' : (user.nickname || '')
    this.setData({
      user,
      needLogin: false,
      avatarText: this.firstText(nickname || '我'),
      profileForm: {
        nickname,
        avatarUrl: user.avatarUrl || ''
      },
      profileAvatarUrl: user.avatarUrl || '',
      space: data.space || {},
      stats: data.stats || {},
      pendingTodoCount: Number((data.stats || {}).pendingTodos || 0),
      pendingOrderCount: Number((data.stats || {}).pendingOrders || 0),
      members: data.memberUsers || [],
      wishes: data.wishes || [],
      anniversaries: data.anniversaries || [],
      messages: data.messages || [],
      activeDress: data.activeDress || {},
      roleText: data.currentRoleText || user.title || '空间成员',
      menuGroups: groups,
      unlockedDressCount: profileStats.unlockedDressCount || (data.ownedDressIds || []).length,
      companionDays: profileStats.companionDays || 0,
      checkinToday: !!data.checkinToday
    })
    this.ensureInvite()
  },

  firstText(value) {
    const text = `${value || ''}`.trim()
    return text ? Array.from(text)[0] : ''
  },

  checkin() {
    if (!api.ensureLogin('登录后才能签到')) return
    if (this.data.checkinToday) {
      wx.showToast({ title: '今天已签到', icon: 'none' })
      return
    }
    api.post('/checkin')
      .then((record) => {
        wx.showToast({ title: `签到 +${record.rewardPoint}`, icon: 'success' })
        this.setData({ checkinToday: true })
        this.load()
      })
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能签到')
      })
  },

  syncProfile() {
    wx.navigateTo({ url: '/pages/account/account' })
  },

  goAccount() {
    wx.navigateTo({ url: '/pages/account/account' })
  },

  handleMenuAction(event) {
    const action = event.currentTarget.dataset.action
    if (action === 'invite') {
      this.openInvitePanel()
      return
    }
    if (action === 'dress') {
      this.goDress()
      return
    }
    if (action === 'manage') {
      this.goManage()
      return
    }
    if (action === 'points') {
      wx.navigateTo({ url: '/pages/dress/dress' })
      return
    }
    if (action === 'anniversary') {
      wx.navigateTo({ url: '/pages/anniversaries/anniversaries' })
      return
    }
    if (action === 'account') {
      wx.navigateTo({ url: '/pages/account/account' })
      return
    }
    wx.showToast({ title: '功能建设中', icon: 'none' })
  },

  ensureInvite() {
    if (this.data.needLogin) return Promise.resolve(null)
    if (this.data.inviteInfo && this.data.inviteInfo.inviteCode) {
      return Promise.resolve(this.data.inviteInfo)
    }
    if (this.data.inviteLoading && this.invitePromise) return this.invitePromise
    this.setData({ inviteLoading: true })
    this.invitePromise = api.post('/spaces/invites')
      .then((inviteInfo) => {
        this.setData({ inviteInfo: inviteInfo || {} })
        return inviteInfo || {}
      })
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能邀请对象')
        return null
      })
      .then((inviteInfo) => {
        this.setData({ inviteLoading: false })
        return inviteInfo
      })
    return this.invitePromise
  },

  openInvitePanel() {
    this.ensureInvite()
      .then((inviteInfo) => {
        const info = inviteInfo || this.data.inviteInfo || {}
        if (!info.inviteCode) {
          wx.showToast({ title: '邀请码生成失败', icon: 'none' })
          return
        }
        this.setData({ showInviteSheet: true, inviteInfo: info })
      })
  },

  closeInvitePanel() {
    this.setData({ showInviteSheet: false })
  },

  copyInviteCode() {
    const code = (this.data.inviteInfo || {}).inviteCode || ''
    if (!code) {
      wx.showToast({ title: '暂无邀请码', icon: 'none' })
      return
    }
    wx.setClipboardData({
      data: code,
      success: () => wx.showToast({ title: '邀请码已复制', icon: 'success' })
    })
  },

  inputInviteCode(event) {
    this.setData({ inviteCodeInput: this.normalizeInviteCode(event.detail.value) })
  },

  bindInviteCode() {
    const inviteCode = this.normalizeInviteCode(this.data.inviteCodeInput)
    if (!inviteCode) {
      wx.showToast({ title: '请输入邀请码', icon: 'none' })
      return
    }
    const app = getApp()
    if (app.setPendingInvite) {
      app.setPendingInvite(inviteCode)
    }
    app.acceptPendingInvite()
      .then((result) => {
        this.setData({ inviteCodeInput: '', showInviteSheet: false })
        wx.showToast({ title: result && result.joined ? '绑定成功' : '已在空间中', icon: 'success' })
        this.load()
      })
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能绑定邀请码')
      })
  },

  normalizeInviteCode(value) {
    return `${value || ''}`.trim().replace(/\s/g, '')
  },

  noop() {},

  onShareAppMessage(event) {
    if (event.from === 'button' && event.target.dataset.shareType === 'invite') {
      const inviteInfo = this.data.inviteInfo || {}
      return {
        title: inviteInfo.title || `${this.data.user.nickname || '我'}邀请你加入专属服务空间`,
        path: inviteInfo.path || '/pages/index/index'
      }
    }
    return {
      title: this.data.space.name || '专属服务空间',
      path: '/pages/index/index'
    }
  },

  goDress() {
    wx.navigateTo({ url: '/pages/dress/dress' })
  },

  goManage() {
    wx.navigateTo({ url: '/pages/manage/manage' })
  },

  goMenu() {
    wx.redirectTo({ url: '/pages/index/index' })
  },

  goTodos() {
    wx.redirectTo({ url: '/pages/todos/todos' })
  },

  goOrders() {
    wx.redirectTo({ url: '/pages/orders/orders' })
  }
})
