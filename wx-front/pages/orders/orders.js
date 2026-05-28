const api = require('../../utils/api')

Page({
  data: {
    user: {},
    allOrders: [],
    visibleOrders: [],
    activeSide: 'sent',
    activeStatus: 'all',
    orderSides: [],
    filters: [],
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
        api.handleNeedLogin(err, '登录后才能查看点单记录')
      })
  },

  apply(data) {
    const orderSides = data.orderSides || []
    const filters = data.orderStatusFilters || []
    this.setData({
      user: data.user || {},
      allOrders: data.orders || [],
      orderSides,
      filters,
      pendingTodoCount: Number((data.stats || {}).pendingTodos || 0),
      pendingOrderCount: Number((data.stats || {}).pendingOrders || 0),
      activeSide: orderSides.find((item) => item.key === this.data.activeSide) ? this.data.activeSide : ((orderSides[0] || {}).key || 'sent'),
      activeStatus: filters.find((item) => item.key === this.data.activeStatus) ? this.data.activeStatus : ((filters[0] || {}).key || 'all')
    })
    this.refreshOrders()
  },

  refreshOrders() {
    const userId = this.data.user.id
    const visibleOrders = this.data.allOrders.filter((item) => {
      const matchSide = this.data.activeSide === 'received'
        ? String(item.assigneeId) === String(userId)
        : String(item.requesterId) === String(userId)
      const matchStatus = this.data.activeStatus === 'all' || !this.data.activeStatus || item.status === this.data.activeStatus
      return matchSide && matchStatus
    })
    this.setData({ visibleOrders })
  },

  switchSide(event) {
    this.setData({ activeSide: event.currentTarget.dataset.key })
    this.refreshOrders()
  },

  switchStatus(event) {
    this.setData({ activeStatus: event.currentTarget.dataset.key })
    this.refreshOrders()
  },

  changeOrder(event) {
    const id = event.currentTarget.dataset.id
    const action = event.currentTarget.dataset.action
    api.post(`/service-orders/${id}/${action}`)
      .then(() => {
        wx.showToast({ title: '状态已更新', icon: 'success' })
        this.load()
      })
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能更新点单状态')
      })
  },

  goMenu() {
    wx.redirectTo({ url: '/pages/index/index' })
  },

  goTodos() {
    wx.redirectTo({ url: '/pages/todos/todos' })
  },

  goMine() {
    wx.redirectTo({ url: '/pages/mine/mine' })
  }
})
