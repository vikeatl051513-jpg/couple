const api = require('../../utils/api')

Page({
  data: {
    todos: [],
    visibleTodos: [],
    members: [],
    activeTodoTab: 'pending',
    todoTabs: [],
    showTodoSheet: false,
    assigneeIndex: 0,
    priorityOptions: [
      { label: '普通', value: 1 },
      { label: '重要', value: 2 },
      { label: '紧急', value: 3 }
    ],
    priorityIndex: 0,
    selectedAssigneeName: '我',
    selectedPriorityLabel: '普通',
    todoForm: {
      title: '',
      description: '',
      dueDate: '',
      dueTime: ''
    },
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
        api.handleNeedLogin(err, '登录后才能查看待办事项')
      })
  },

  apply(data) {
    const todos = data.todos || []
    const members = data.memberUsers || []
    const tabs = (data.todoTabs || []).map((tab) => {
      const count = todos.filter((item) => tab.key === 'done' ? item.done : !item.done).length
      return { ...tab, count }
    })
    this.setData({
      members,
      todos,
      todoTabs: tabs,
      pendingTodoCount: Number((data.stats || {}).pendingTodos || 0),
      pendingOrderCount: Number((data.stats || {}).pendingOrders || 0),
      assigneeIndex: Math.min(this.data.assigneeIndex, Math.max(members.length - 1, 0)),
      selectedAssigneeName: ((members[Math.min(this.data.assigneeIndex, Math.max(members.length - 1, 0))] || {}).nickname) || '我',
      selectedPriorityLabel: (this.data.priorityOptions[this.data.priorityIndex] || this.data.priorityOptions[0]).label
    })
    if (!tabs.find((tab) => tab.key === this.data.activeTodoTab) && tabs.length) {
      this.setData({ activeTodoTab: tabs[0].key })
    }
    this.refreshVisibleTodos()
  },

  refreshVisibleTodos() {
    const visibleTodos = this.data.todos.filter((item) => this.data.activeTodoTab === 'done' ? item.done : !item.done)
    this.setData({ visibleTodos })
  },

  switchTodoTab(event) {
    this.setData({ activeTodoTab: event.currentTarget.dataset.key })
    this.refreshVisibleTodos()
  },

  toggleTodo(event) {
    const id = event.currentTarget.dataset.id
    const status = event.currentTarget.dataset.status
    api.post(`/todos/${id}/${status}`)
      .then(() => {
        wx.showToast({ title: '已更新', icon: 'success' })
        this.load()
      })
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能更新待办事项')
      })
  },

  showAddTodo() {
    if (!api.ensureLogin('登录后才能创建待办事项')) return
    this.setData({ showTodoSheet: true })
  },

  closeTodoSheet() {
    this.setData({ showTodoSheet: false })
  },

  noop() {},

  inputTodo(event) {
    const key = event.currentTarget.dataset.key
    this.setData({ [`todoForm.${key}`]: event.detail.value })
  },

  changeAssignee(event) {
    const assigneeIndex = Number(event.detail.value || 0)
    this.setData({
      assigneeIndex,
      selectedAssigneeName: ((this.data.members[assigneeIndex] || {}).nickname) || '我'
    })
  },

  changePriority(event) {
    const priorityIndex = Number(event.detail.value || 0)
    this.setData({
      priorityIndex,
      selectedPriorityLabel: (this.data.priorityOptions[priorityIndex] || this.data.priorityOptions[0]).label
    })
  },

  changeDueDate(event) {
    this.setData({ 'todoForm.dueDate': event.detail.value })
  },

  changeDueTime(event) {
    this.setData({ 'todoForm.dueTime': event.detail.value })
  },

  submitTodo() {
    const title = (this.data.todoForm.title || '').trim()
    if (!title) {
      wx.showToast({ title: '待办标题不能为空', icon: 'none' })
      return
    }
    if (!api.ensureLogin('登录后才能创建待办事项')) return
    const assignee = this.data.members[this.data.assigneeIndex] || {}
    const priority = this.data.priorityOptions[this.data.priorityIndex] || this.data.priorityOptions[0]
    const dueTime = this.data.todoForm.dueDate && this.data.todoForm.dueTime
      ? `${this.data.todoForm.dueDate}T${this.data.todoForm.dueTime}:00`
      : ''
    api.post('/todos', {
      title,
      description: this.data.todoForm.description || '',
      assigneeId: assignee.id,
      priority: priority.value,
      dueTime
    }).then(() => {
      wx.showToast({ title: '待办已创建', icon: 'success' })
      this.setData({
        showTodoSheet: false,
        todoForm: {
          title: '',
          description: '',
          dueDate: '',
          dueTime: ''
        },
        priorityIndex: 0,
        selectedPriorityLabel: '普通'
      })
      this.load()
    }).catch((err) => {
      api.handleNeedLogin(err, '登录后才能创建待办事项')
    })
  },

  goMenu() {
    wx.redirectTo({ url: '/pages/index/index' })
  },

  goOrders() {
    wx.redirectTo({ url: '/pages/orders/orders' })
  },

  goMine() {
    wx.redirectTo({ url: '/pages/mine/mine' })
  }
})
