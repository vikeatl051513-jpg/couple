const api = require('../../utils/api')

Page({
  data: {
    user: {},
    activeDress: {},
    dressItems: [],
    ownedDressIds: [],
    activities: [],
    activityDesc: '',
    checkinToday: false
  },

  onShow() {
    this.load()
  },

  load() {
    api.get('/bootstrap')
      .then((data) => this.apply(data))
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能查看空间装扮')
      })
  },

  apply(data) {
    const owned = data.ownedDressIds || []
    const products = data.products || []
    this.setData({
      user: data.user || {},
      activeDress: data.activeDress || {},
      dressItems: (data.dressItems || []).map((item) => {
        const product = products.find((candidate) => candidate.type === 'dress' && String(candidate.targetId) === String(item.id))
        return {
          ...item,
          owned: owned.indexOf(item.id) > -1,
          active: data.activeDress && String(data.activeDress.id) === String(item.id),
          productId: product ? product.id : null,
          priceText: this.priceText(item)
        }
      }),
      ownedDressIds: owned,
      activities: data.activities || [],
      activityDesc: ((data.activities || [])[0] || {}).description || '',
      checkinToday: data.checkinToday
    })
  },

  priceText(item) {
    if (item.obtainType === 'paid') {
      return `${Math.round((item.priceCent || 0) / 100)} 元`
    }
    if (item.obtainType === 'point') {
      return `${item.pointPrice || 0} 积分`
    }
    return '免费'
  },

  checkin() {
    if (!api.ensureLogin('登录后才能签到')) return
    api.post('/checkin')
      .then((record) => {
        wx.showToast({ title: `签到 +${record.rewardPoint}`, icon: 'success' })
        this.load()
      })
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能签到')
      })
  },

  applyDress(event) {
    if (!api.ensureLogin('登录后才能应用装扮')) return
    const id = event.currentTarget.dataset.id
    api.post(`/dress/items/${id}/apply`)
      .then(() => {
        wx.showToast({ title: '装扮已应用', icon: 'success' })
        this.load()
      })
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能应用装扮')
      })
  },

  buyDress(event) {
    if (!api.ensureLogin('登录后才能购买装扮')) return
    const productId = event.currentTarget.dataset.productId
    if (!productId) {
      wx.showToast({ title: '后端未配置商品', icon: 'none' })
      return
    }
    api.post('/payments', { productId })
      .then(() => {
        wx.showToast({ title: '购买成功', icon: 'success' })
        this.load()
      })
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能购买装扮')
      })
  }
})
