const api = require('../../utils/api')

const DEFAULT_SERVICE_IMAGES = [
  { path: '/assets/service-icons/kitty-tea-1.png', label: '古茗' },
  { path: '/assets/service-icons/kitty-tea-2.png', label: '霸王茶姬' },
  { path: '/assets/service-icons/kitty-tea-3.png', label: '沪上阿姨' },
  { path: '/assets/service-icons/kitty-tea-4.png', label: '喜茶' }
]

const defaultServiceImage = () => DEFAULT_SERVICE_IMAGES[0].path

const emptyCategoryForm = () => ({
  id: '',
  name: '',
  description: '',
  visible: true
})

const emptyServiceForm = (category = {}) => ({
  id: '',
  categoryId: category.id || '',
  categoryName: category.name || '',
  name: '',
  description: '',
  imageUrl: defaultServiceImage(),
  tag: '茶',
  pointCost: 0,
  dailyLimit: 0,
  cooldownMinutes: 0,
  requireRemark: true,
  requireAppointTime: false,
  status: 'active'
})

Page({
  data: {
    statusBarHeight: 0,
    activeSheet: '',
    activeManageTab: 'space',
    categories: [],
    services: [],
    filteredServices: [],
    templates: [],
    templateImporting: false,
    space: {},
    spaceForm: {
      name: '',
      announcement: ''
    },
    spaceSaving: false,
    activeDress: {},
    roleText: '',
    noAccess: false,
    canTransferManager: false,
    transferLoading: false,
    currentManagerName: '',
    managementTargets: [],
    imageUploading: false,
    presetImages: DEFAULT_SERVICE_IMAGES,
    activeCategoryId: '',
    expandedCategoryId: '',
    serviceCategoryIndex: 0,
    categoryForm: emptyCategoryForm(),
    serviceForm: emptyServiceForm()
  },

  onLoad() {
    this.setData({ statusBarHeight: this.getStatusBarHeight() })
    this.load()
  },

  getStatusBarHeight() {
    try {
      return wx.getSystemInfoSync().statusBarHeight || 0
    } catch (error) {
      return 0
    }
  },

  load() {
    api.get('/bootstrap')
      .then((data) => this.apply(data))
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能管理空间')
      })
  },

  apply(data) {
    const space = data.space || {}
    const app = getApp()
    const currentUserId = wx.getStorageSync('userId') || (app.globalData || {}).userId || ''
    const memberUsers = (data.memberUsers || []).map((member) => ({
      ...member,
      avatarUrl: api.assetUrl(member.avatarUrl || '')
    }))
    const ownerUserId = space.ownerUserId || ''
    const ownerUser = memberUsers.find((member) => String(member.id) === String(ownerUserId)) || data.user || {}
    const rawCategories = data.categories || []
    const rawServices = (data.serviceItems || []).map((service) => ({
      ...service,
      imageUrl: api.assetUrl(service.imageUrl || '')
    }))
    const services = this.attachCategoryName(rawServices, rawCategories)
    const categories = this.decorateCategories(rawCategories, services)
    const first = categories[0] || {}
    const activeStillExists = categories.some((item) => String(item.id) === String(this.data.activeCategoryId))
    const activeCategoryId = activeStillExists ? this.data.activeCategoryId : first.id || ''
    const activeCategory = categories.find((item) => String(item.id) === String(activeCategoryId)) || first
    const serviceCategory = categories.find((item) => String(item.id) === String(this.data.serviceForm.categoryId)) || activeCategory
    const serviceForm = this.data.serviceForm.categoryId
      ? { ...this.data.serviceForm, categoryName: serviceCategory.name || this.data.serviceForm.categoryName }
      : emptyServiceForm(serviceCategory)

    this.setData({
      categories,
      services,
      space,
      spaceForm: {
        name: space.name || '',
        announcement: space.announcement || ''
      },
      filteredServices: this.filterServices(services, activeCategoryId),
      templates: data.templates || [],
      activeDress: data.activeDress || {},
      roleText: data.currentRoleText || '',
      noAccess: !data.isSpaceAdmin,
      canTransferManager: data.isSpaceAdmin && String(ownerUserId) === String(currentUserId),
      currentManagerName: ownerUser.nickname || ownerUser.displayName || '空间管理员',
      managementTargets: this.decorateManagementMembers(memberUsers, data.members || [], ownerUserId),
      activeCategoryId,
      expandedCategoryId: categories.some((item) => item.expanded) ? String((categories.find((item) => item.expanded) || {}).id || '') : '',
      serviceCategoryIndex: this.findCategoryIndex(serviceForm.categoryId, categories),
      serviceForm
    })
  },

  switchManageTab(event) {
    const tab = event.currentTarget.dataset.tab || 'space'
    if (tab === this.data.activeManageTab) return
    this.setData({
      activeManageTab: tab,
      activeSheet: ''
    })
  },

  decorateManagementMembers(memberUsers, spaceMembers, ownerUserId) {
    return memberUsers
      .filter((member) => String(member.id) !== String(ownerUserId))
      .map((member) => {
        const relation = (spaceMembers || []).find((item) => String(item.userId) === String(member.id)) || {}
        const name = member.nickname || relation.displayName || '空间成员'
        return {
          ...member,
          name,
          role: relation.role || 'partner',
          roleText: relation.role === 'owner' ? '当前管理员' : '可交接对象',
          avatarText: this.firstText(name || 'TA')
        }
      })
  },

  firstText(value) {
    const text = `${value || ''}`.trim()
    return text ? Array.from(text)[0] : ''
  },

  inputSpace(event) {
    const key = event.currentTarget.dataset.key
    this.setData({ [`spaceForm.${key}`]: event.detail.value })
  },

  saveSpace() {
    if (this.data.spaceSaving) return
    const form = this.data.spaceForm || {}
    const name = `${form.name || ''}`.trim()
    if (!name) {
      wx.showToast({ title: '空间名称不能为空', icon: 'none' })
      return
    }
    this.setData({ spaceSaving: true })
    api.put('/spaces/current', {
      name,
      announcement: form.announcement || ''
    }).then((space) => {
      this.setData({
        space: space || {},
        spaceForm: {
          name: (space || {}).name || name,
          announcement: (space || {}).announcement || form.announcement || ''
        }
      })
      wx.showToast({ title: '空间资料已保存', icon: 'success' })
      this.load()
    }).catch((err) => {
      api.handleNeedLogin(err, '登录后才能保存空间资料')
    }).then(() => {
      this.setData({ spaceSaving: false })
    })
  },

  transferManager(event) {
    if (this.data.transferLoading || !this.data.canTransferManager) return
    const targetUserId = event.currentTarget.dataset.id
    const target = this.data.managementTargets.find((item) => String(item.id) === String(targetUserId)) || {}
    const targetName = target.name || '对象'
    wx.showModal({
      title: '交接管理权限',
      content: `确定把空间管理和默认接单交给${targetName}吗？交接后你会变成被服务的人。`,
      confirmText: '交给TA',
      success: (res) => {
        if (!res.confirm) return
        this.setData({ transferLoading: true })
        api.post('/spaces/current/manager', { targetUserId })
          .then(() => {
            wx.showToast({ title: '已交接管理权', icon: 'success' })
            this.load()
          })
          .catch((err) => {
            api.handleNeedLogin(err, '登录后才能交接管理权限')
          })
          .then(() => {
            this.setData({ transferLoading: false })
          })
      }
    })
  },

  decorateCategories(categories, services) {
    const currentExpandedId = this.data.expandedCategoryId || this.data.activeCategoryId || ((categories[0] || {}).id || '')
    return categories.map((category) => ({
      ...category,
      expanded: String(category.id) === String(currentExpandedId),
      serviceList: services.filter((service) => String(service.categoryId) === String(category.id)),
      serviceCount: services.filter((service) => String(service.categoryId) === String(category.id)).length
    }))
  },

  attachCategoryName(services, categories) {
    return services.map((service) => {
      const category = categories.find((item) => String(item.id) === String(service.categoryId)) || {}
      return {
        ...service,
        categoryName: category.name || '未分类'
      }
    })
  },

  filterServices(services, categoryId) {
    if (!categoryId) return services
    return services.filter((service) => String(service.categoryId) === String(categoryId))
  },

  findCategoryIndex(categoryId, categories = this.data.categories) {
    const index = categories.findIndex((item) => String(item.id) === String(categoryId))
    return index > -1 ? index : 0
  },

  goBack() {
    if (getCurrentPages().length > 1) {
      wx.navigateBack()
      return
    }
    wx.redirectTo({ url: '/pages/index/index' })
  },

  openCategorySheet() {
    this.setData({
      categoryForm: emptyCategoryForm(),
      activeSheet: 'category'
    })
  },

  editCategory(event) {
    const category = this.data.categories.find((item) => String(item.id) === String(event.currentTarget.dataset.id))
    if (!category) return
    this.setData({
      categoryForm: {
        id: category.id,
        name: category.name,
        description: category.description || '',
        visible: category.visible !== false
      },
      activeSheet: 'category'
    })
  },

  toggleCategory(event) {
    const id = String(event.currentTarget.dataset.id || '')
    const willCollapse = String(this.data.expandedCategoryId || '') === id
    const expandedCategoryId = willCollapse ? '' : id
    this.setData({
      expandedCategoryId,
      categories: this.data.categories.map((category) => ({
        ...category,
        expanded: String(category.id) === expandedCategoryId
      }))
    })
  },

  inputCategory(event) {
    const key = event.currentTarget.dataset.key
    this.setData({ [`categoryForm.${key}`]: event.detail.value })
  },

  toggleCategoryVisible(event) {
    this.setData({ 'categoryForm.visible': event.detail.value })
  },

  resetCategoryForm() {
    this.setData({ categoryForm: emptyCategoryForm() })
  },

  saveCategory() {
    const form = this.data.categoryForm
    const name = (form.name || '').trim()
    if (!name) {
      wx.showToast({ title: '分类名称不能为空', icon: 'none' })
      return
    }
    const payload = {
      name,
      description: form.description || '',
      iconUrl: '',
      visible: form.visible !== false
    }
    const request = form.id
      ? api.put(`/service-categories/${form.id}`, payload)
      : api.post('/service-categories', payload)
    request.then(() => {
      wx.showToast({ title: form.id ? '分类已更新' : '分类已保存', icon: 'success' })
      this.closeAllSheets()
      this.resetCategoryForm()
      this.load()
    }).catch((err) => {
      api.handleNeedLogin(err, '登录后才能管理服务分类')
    })
  },

  removeCategory(event) {
    const id = event.currentTarget.dataset.id
    wx.showModal({
      title: '删除分类',
      content: '会同时删除分类里的服务，确定继续吗？',
      success: (res) => {
        if (!res.confirm) return
        api.del(`/service-categories/${id}`)
          .then(() => {
            wx.showToast({ title: '已删除', icon: 'success' })
            this.load()
          })
          .catch((err) => {
            api.handleNeedLogin(err, '登录后才能管理服务分类')
          })
      }
    })
  },

  openServiceSheet() {
    if (!this.data.categories.length) {
      wx.showToast({ title: '请先新建分类', icon: 'none' })
      return
    }
    const category = this.data.categories.find((item) => String(item.id) === String(this.data.activeCategoryId)) || this.data.categories[0]
    this.setData({
      serviceForm: emptyServiceForm(category),
      serviceCategoryIndex: this.findCategoryIndex(category.id),
      activeSheet: 'service'
    })
  },

  changeServiceCategory(event) {
    const index = Number(event.detail.value)
    const category = this.data.categories[index]
    if (!category) return
    this.setData({
      activeCategoryId: category.id,
      filteredServices: this.filterServices(this.data.services, category.id),
      serviceCategoryIndex: index,
      'serviceForm.categoryId': category.id,
      'serviceForm.categoryName': category.name
    })
  },

  inputService(event) {
    const key = event.currentTarget.dataset.key
    this.setData({ [`serviceForm.${key}`]: event.detail.value })
  },

  toggleService(event) {
    const key = event.currentTarget.dataset.key
    this.setData({ [`serviceForm.${key}`]: event.detail.value })
  },

  editService(event) {
    const service = this.data.services.find((item) => String(item.id) === String(event.currentTarget.dataset.id))
    if (!service) return
    this.setData({
      activeCategoryId: service.categoryId,
      filteredServices: this.filterServices(this.data.services, service.categoryId),
      serviceCategoryIndex: this.findCategoryIndex(service.categoryId),
      activeSheet: 'service',
      serviceForm: {
        id: service.id,
        categoryId: service.categoryId,
        categoryName: service.categoryName,
        name: service.name,
        description: service.description || '',
        imageUrl: service.imageUrl || defaultServiceImage(),
        tag: service.tag || service.visualText || '茶',
        pointCost: service.pointCost || 0,
        dailyLimit: service.dailyLimit || 0,
        cooldownMinutes: service.cooldownMinutes || 0,
        requireRemark: !!service.requireRemark,
        requireAppointTime: !!service.requireAppointTime,
        status: service.status || 'active'
      }
    })
  },

  resetServiceForm() {
    const category = this.data.categories.find((item) => String(item.id) === String(this.data.activeCategoryId)) || this.data.categories[0] || {}
    this.setData({
      serviceForm: emptyServiceForm(category),
      serviceCategoryIndex: this.findCategoryIndex(category.id)
    })
  },

  saveService() {
    const form = this.data.serviceForm
    const name = (form.name || '').trim()
    if (!form.categoryId) {
      wx.showToast({ title: '请先选择分类', icon: 'none' })
      return
    }
    if (!name) {
      wx.showToast({ title: '服务名称不能为空', icon: 'none' })
      return
    }
    const payload = {
      categoryId: form.categoryId,
      name,
      description: form.description || '',
      imageUrl: form.imageUrl || defaultServiceImage(),
      tag: form.tag || '茶',
      pointCost: Number(form.pointCost || 0),
      dailyLimit: Number(form.dailyLimit || 0),
      cooldownMinutes: Number(form.cooldownMinutes || 0),
      requireRemark: !!form.requireRemark,
      requireAppointTime: !!form.requireAppointTime,
      status: form.status || 'active'
    }
    const request = form.id
      ? api.put(`/service-items/${form.id}`, payload)
      : api.post('/service-items', payload)
    request.then(() => {
      wx.showToast({ title: form.id ? '服务已更新' : '服务已上架', icon: 'success' })
      this.closeAllSheets()
      this.resetServiceForm()
      this.load()
    }).catch((err) => {
      api.handleNeedLogin(err, '登录后才能管理服务')
    })
  },

  removeService(event) {
    const id = event.currentTarget.dataset.id
    wx.showModal({
      title: '删除服务',
      content: '删除后点单页不再显示这个服务。',
      success: (res) => {
        if (!res.confirm) return
        api.del(`/service-items/${id}`)
          .then(() => {
            wx.showToast({ title: '已删除', icon: 'success' })
            this.load()
          })
          .catch((err) => {
            api.handleNeedLogin(err, '登录后才能管理服务')
          })
      }
    })
  },

  chooseServiceImage() {
    if (this.data.imageUploading) return
    const pickImage = new Promise((resolve, reject) => {
      if (wx.chooseMedia) {
        wx.chooseMedia({
          count: 1,
          mediaType: ['image'],
          sourceType: ['album', 'camera'],
          success: (res) => resolve(((res.tempFiles || [])[0] || {}).tempFilePath || ''),
          fail: reject
        })
        return
      }
      wx.chooseImage({
        count: 1,
        sourceType: ['album', 'camera'],
        success: (res) => resolve((res.tempFilePaths || [])[0] || ''),
        fail: reject
      })
    })
    pickImage
      .then((filePath) => {
        if (!filePath) return null
        this.setData({ imageUploading: true })
        return api.upload('/files/service-image', filePath, 'file')
      })
      .then((result) => {
        if (!result) return
        this.setData({ 'serviceForm.imageUrl': api.assetUrl(result.url || '') })
        wx.showToast({ title: '图片已上传', icon: 'success' })
      })
      .catch((err) => {
        api.handleNeedLogin(err, '登录后才能上传服务图片')
      })
      .then(() => {
        this.setData({ imageUploading: false })
      })
  },

  selectPresetImage(event) {
    this.setData({
      'serviceForm.imageUrl': event.currentTarget.dataset.path || defaultServiceImage(),
      'serviceForm.tag': event.currentTarget.dataset.label || '茶'
    })
  },

  clearServiceImage() {
    this.setData({ 'serviceForm.imageUrl': defaultServiceImage() })
  },

  closeAllSheets() {
    this.setData({ activeSheet: '' })
  },

  noop() {},

  importTemplate(event) {
    if (this.data.templateImporting) return
    const templateId = event.currentTarget.dataset.id
    const template = this.data.templates.find((item) => String(item.id) === String(templateId)) || {}
    wx.showModal({
      title: '导入模板',
      content: `将导入「${template.name || '服务模板'}」里的分类和服务，已有内容不会被删除。`,
      confirmText: '导入',
      success: (res) => {
        if (!res.confirm) return
        this.setData({ templateImporting: true })
        api.post(`/templates/${templateId}/import`)
          .then((result) => {
            wx.showToast({ title: `导入 ${result.imported} 项`, icon: 'success' })
            this.load()
          })
          .catch((err) => {
            api.handleNeedLogin(err, '登录后才能导入服务模板')
          })
          .then(() => {
            this.setData({ templateImporting: false })
          })
      }
    })
  }
})
