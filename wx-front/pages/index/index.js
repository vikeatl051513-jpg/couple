const api = require('../../utils/api')

Page({
  data: {
    loading: true,
    space: {},
    user: {},
    members: [],
    avatarMembers: [],
    categories: [],
    serviceItems: [],
    activeCategoryId: '',
    activeCategory: {},
    visibleItems: [],
    moodOptions: [],
    quickSignals: [],
    currentMood: {},
    activeDress: {},
    searchKeyword: '',
    selectedService: null,
    remark: '',
    needLogin: false,
    guestMode: false,
    pendingTodoCount: 0,
    pendingOrderCount: 0
  },

  onLoad(options = {}) {
    this.captureInvite(options)
    this.load()
  },

  onShow() {
    if (!this.data.loading) {
      this.load()
    }
  },

  load() {
    this.setData({ loading: true })
    api.get('/bootstrap')
      .then((data) => this.applyBootstrap(data))
      .catch((err) => this.handleLoadError(err))
  },

  handleLoadError(err = {}) {
    this.setData({
      loading: false,
      needLogin: false,
      guestMode: !!err.needLogin,
      pendingTodoCount: 0,
      pendingOrderCount: 0
    })
  },

  applyBootstrap(data) {
    const categories = data.categories || []
    const user = {
      ...(data.user || {}),
      avatarUrl: api.assetUrl((data.user || {}).avatarUrl || '')
    }
    const members = (data.memberUsers || []).map((member) => ({
      ...member,
      avatarUrl: api.assetUrl(member.avatarUrl || '')
    }))
    const serviceItems = (data.serviceItems || []).map((item) => ({
      ...item,
      imageUrl: api.assetUrl(item.imageUrl || '')
    }))
    const activeCategoryId = this.data.activeCategoryId || (categories[0] && categories[0].id) || ''
    this.setData({
      loading: false,
      needLogin: false,
      guestMode: false,
      space: data.space || {},
      user,
      members,
      avatarMembers: this.buildAvatarMembers(members, user),
      categories,
      serviceItems,
      moodOptions: data.moodOptions || [],
      quickSignals: data.quickSignals || [],
      currentMood: data.currentMood || {},
      activeDress: data.activeDress || {},
      pendingTodoCount: Number((data.stats || {}).pendingTodos || 0),
      pendingOrderCount: Number((data.stats || {}).pendingOrders || 0),
      activeCategoryId
    })
    this.refreshVisibleItems()
    this.acceptInviteIfNeeded()
  },

  captureInvite(options = {}) {
    const app = getApp()
    if (app.captureInvite) {
      app.captureInvite(options)
    }
  },

  acceptInviteIfNeeded() {
    const app = getApp()
    if (!app.getPendingInvite || !app.getPendingInvite() || !(app.hasSession && app.hasSession())) {
      return
    }
    app.acceptPendingInvite({ silent: true })
      .then((result) => {
        if (!result) return
        wx.showToast({ title: result.joined ? '已加入空间' : '已打开空间', icon: 'success' })
        this.load()
      })
      .catch(() => {})
  },

  buildAvatarMembers(members, user) {
    const list = members.length ? members.slice(0, 2) : [user]
    return list.map((item, index) => ({
      ...item,
      avatarClass: index === 0 ? 'girl' : 'boy',
      avatarText: this.firstText(item.nickname || item.title || '我')
    }))
  },

  refreshVisibleItems() {
    const keyword = this.data.searchKeyword.trim()
    const activeCategoryId = this.data.activeCategoryId
    const activeCategory = this.data.categories.find((item) => String(item.id) === String(activeCategoryId)) || {}
    const visibleItems = this.data.serviceItems.filter((item) => {
      const matchCategory = !activeCategoryId || String(item.categoryId) === String(activeCategoryId)
      const matchKeyword = !keyword || (item.name || '').indexOf(keyword) > -1 || (item.description || '').indexOf(keyword) > -1
      return matchCategory && matchKeyword && item.status !== 'disabled'
    }).map((item) => ({
      ...item,
      visualText: item.visualText || this.firstText(item.tag || item.name || '服'),
      needTimeText: item.needTimeText || (item.requireAppointTime ? '需预约' : '立即'),
      needRemarkText: item.needRemarkText || (item.requireRemark ? '填备注' : ''),
      orderButtonText: item.orderButtonText || (item.cooldownMinutes ? '冷却中' : '立即召唤'),
      cooldownText: item.cooldownText || (item.cooldownMinutes ? `${item.cooldownMinutes} 分钟` : '')
    }))
    this.setData({ visibleItems, activeCategory })
  },

  firstText(value) {
    const text = `${value || ''}`.trim()
    return text ? Array.from(text)[0] : ''
  },

  selectCategory(event) {
    this.setData({ activeCategoryId: event.currentTarget.dataset.id })
    this.refreshVisibleItems()
  },

  onSearch(event) {
    this.setData({ searchKeyword: event.detail.value || '' })
    this.refreshVisibleItems()
  },

  clearSearch() {
    this.setData({ searchKeyword: '' })
    this.refreshVisibleItems()
  },

  openOrder(event) {
    const id = event.currentTarget.dataset.id
    const selectedService = this.data.serviceItems.find((item) => String(item.id) === String(id))
    this.setData({
      selectedService,
      remark: ''
    })
  },

  closeOrder() {
    this.setData({ selectedService: null })
  },

  noop() {},

  onRemark(event) {
    this.setData({ remark: event.detail.value })
  },

  submitOrder() {
    if (!api.ensureLogin('登录后才能发送点单给对方')) return
    const item = this.data.selectedService
    if (!item) return
    api.post('/service-orders', {
      serviceItemId: item.id,
      remark: this.data.remark
    }).then(() => {
      wx.showToast({ title: '已发送点单', icon: 'success' })
      this.closeOrder()
      this.load()
    }).catch((err) => {
      api.handleNeedLogin(err, '登录后才能发送点单给对方')
    })
  },

  setMood(event) {
    const moodKey = event.currentTarget.dataset.key
    const option = this.data.moodOptions.find((item) => item.key === moodKey)
    this.updateMood(option)
  },

  cycleMood() {
    const options = this.data.moodOptions || []
    if (!options.length) return
    const currentIndex = options.findIndex((item) => item.key === this.data.currentMood.moodKey)
    const option = options[(currentIndex + 1) % options.length]
    this.updateMood(option)
  },

  updateMood(option) {
    if (!api.ensureLogin('登录后才能更新你的心情状态')) return
    if (!option) return
    api.post('/moods', {
      moodKey: option.key,
      note: option.hint || ''
    }).then((mood) => {
      this.setData({ currentMood: mood })
      wx.showToast({ title: '心情已更新', icon: 'success' })
    }).catch((err) => {
      api.handleNeedLogin(err, '登录后才能更新你的心情状态')
    })
  },

  sendSignal(event) {
    if (!api.ensureLogin('登录后才能发送暗号')) return
    const signal = this.data.quickSignals[Number(event.currentTarget.dataset.index)]
    if (!signal) return
    api.post('/signals', {
      key: signal.key,
      title: signal.title || signal.label,
      content: signal.content || signal.description
    }).then(() => {
      wx.showToast({ title: '暗号已发送', icon: 'success' })
    }).catch((err) => {
      api.handleNeedLogin(err, '登录后才能发送暗号')
    })
  },

  syncProfile() {
    if (!api.ensureLogin('登录后才能同步头像和昵称')) return
    this.goAccount()
  },

  login() {
    this.goAccount()
  },

  randomPick() {
    const items = this.data.serviceItems
    if (!items.length) return
    const selectedService = items[Math.floor(Math.random() * items.length)]
    this.setData({ selectedService })
  },

  goManage() {
    if (!api.ensureLogin('登录后才能管理空间')) return
    wx.navigateTo({ url: '/pages/manage/manage' })
  },

  goDress() {
    if (!api.ensureLogin('登录后才能查看空间装扮')) return
    wx.navigateTo({ url: '/pages/dress/dress' })
  },

  goWish() {
    if (!api.ensureLogin('登录后才能查看愿望清单')) return
    wx.redirectTo({ url: '/pages/mine/mine' })
  },

  goTodos() {
    if (!api.ensureLogin('登录后才能查看待办事项')) return
    wx.redirectTo({ url: '/pages/todos/todos' })
  },

  goOrders() {
    if (!api.ensureLogin('登录后才能查看点单记录')) return
    wx.redirectTo({ url: '/pages/orders/orders' })
  },

  goMine() {
    if (!api.ensureLogin('登录后才能查看我的空间')) return
    wx.redirectTo({ url: '/pages/mine/mine' })
  },

  goAccount() {
    wx.navigateTo({ url: '/pages/account/account' })
  }
})
