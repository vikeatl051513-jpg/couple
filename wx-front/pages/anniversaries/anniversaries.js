const api = require('../../utils/api')

const todayText = () => {
  const now = new Date()
  const month = `${now.getMonth() + 1}`.padStart(2, '0')
  const day = `${now.getDate()}`.padStart(2, '0')
  return `${now.getFullYear()}-${month}-${day}`
}

const emptyForm = () => ({
  id: '',
  name: '',
  date: todayText(),
  important: true
})

Page({
  data: {
    loading: true,
    space: {},
    anniversaries: [],
    hero: null,
    showSheet: false,
    saving: false,
    form: emptyForm()
  },

  onShow() {
    this.load()
  },

  load() {
    this.setData({ loading: true })
    api.get('/bootstrap')
      .then((data) => this.apply(data))
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能管理纪念日')
        this.setData({ loading: false })
      })
  },

  apply(data) {
    const anniversaries = (data.anniversaries || [])
      .map((item) => this.decorateAnniversary(item))
      .sort((left, right) => {
        if (left.important !== right.important) return left.important ? -1 : 1
        return left.daysUntil - right.daysUntil
      })
    this.setData({
      loading: false,
      space: data.space || {},
      anniversaries,
      hero: anniversaries[0] || null
    })
  },

  decorateAnniversary(item) {
    const date = `${item.date || todayText()}`.slice(0, 10)
    const fromStart = this.daysBetween(date, todayText())
    const days = Math.max(0, fromStart)
    const nextDate = this.nextOccurrence(date)
    const daysUntil = this.daysBetween(todayText(), nextDate)
    return {
      ...item,
      date,
      days,
      nextDate,
      daysUntil,
      dateText: date.replace(/-/g, '.'),
      nextText: daysUntil === 0 ? '就是今天' : `${daysUntil} 天后`,
      badgeText: item.important ? '重要' : '普通'
    }
  },

  daysBetween(startText, endText) {
    const start = new Date(`${startText}T00:00:00`)
    const end = new Date(`${endText}T00:00:00`)
    return Math.round((end.getTime() - start.getTime()) / 86400000)
  },

  nextOccurrence(dateText) {
    const today = new Date(`${todayText()}T00:00:00`)
    const source = new Date(`${dateText}T00:00:00`)
    const month = source.getMonth()
    const day = source.getDate()
    let next = new Date(today.getFullYear(), month, day)
    if (next.getTime() < today.getTime()) {
      next = new Date(today.getFullYear() + 1, month, day)
    }
    const nextMonth = `${next.getMonth() + 1}`.padStart(2, '0')
    const nextDay = `${next.getDate()}`.padStart(2, '0')
    return `${next.getFullYear()}-${nextMonth}-${nextDay}`
  },

  openAddSheet() {
    this.setData({
      form: emptyForm(),
      showSheet: true
    })
  },

  editAnniversary(event) {
    const id = event.currentTarget.dataset.id
    const item = this.data.anniversaries.find((candidate) => String(candidate.id) === String(id))
    if (!item) return
    this.setData({
      form: {
        id: item.id,
        name: item.name,
        date: item.date,
        important: !!item.important
      },
      showSheet: true
    })
  },

  closeSheet() {
    if (this.data.saving) return
    this.setData({ showSheet: false })
  },

  noop() {},

  inputName(event) {
    this.setData({ 'form.name': event.detail.value })
  },

  changeDate(event) {
    this.setData({ 'form.date': event.detail.value })
  },

  toggleImportant(event) {
    this.setData({ 'form.important': event.detail.value })
  },

  saveAnniversary() {
    if (this.data.saving) return
    const form = this.data.form || {}
    const name = `${form.name || ''}`.trim()
    if (!name) {
      wx.showToast({ title: '请填写纪念日名称', icon: 'none' })
      return
    }
    this.setData({ saving: true })
    const payload = {
      name,
      date: form.date || todayText(),
      important: !!form.important
    }
    const request = form.id
      ? api.put(`/anniversaries/${form.id}`, payload)
      : api.post('/anniversaries', payload)
    request
      .then(() => {
        wx.showToast({ title: form.id ? '已更新' : '已添加', icon: 'success' })
        this.setData({ showSheet: false })
        this.load()
      })
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能保存纪念日')
      })
      .then(() => {
        this.setData({ saving: false })
      })
  },

  removeAnniversary() {
    const id = this.data.form.id
    if (!id || this.data.saving) return
    wx.showModal({
      title: '删除纪念日',
      content: '删除后不会影响其他空间数据，确定继续吗？',
      confirmText: '删除',
      success: (res) => {
        if (!res.confirm) return
        this.setData({ saving: true })
        api.del(`/anniversaries/${id}`)
          .then(() => {
            wx.showToast({ title: '已删除', icon: 'success' })
            this.setData({ showSheet: false })
            this.load()
          })
          .catch((err) => {
            api.handleNeedLogin(err, '登录后才能删除纪念日')
          })
          .then(() => {
            this.setData({ saving: false })
          })
      }
    })
  }
})
