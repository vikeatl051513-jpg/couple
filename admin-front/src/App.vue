<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'

const apiBase = 'https://couple.vikeatl.xyz/api/admin'

const fallback = {
  stats: {
    users: 3,
    spaces: 1,
    serviceItems: 13,
    orders: 2,
    todos: 3,
    dressItems: 3
  },
  users: [
    { id: 1, nickname: '铁铁', title: '御膳房掌柜', points: 126, contribution: 18, status: 'active' },
    { id: 2, nickname: '小满', title: '头号点单官', points: 98, contribution: 12, status: 'active' }
  ],
  spaces: [
    {
      id: 1,
      name: '铁铁的独家御膳房',
      type: 'couple',
      ownerUserId: 1,
      ownerName: '铁铁',
      level: 1,
      status: 'active',
      memberCount: 2,
      announcement: '此店只为小满一人服务',
      stats: {
        memberCount: 2,
        pendingOrders: 1,
        completedOrders: 1,
        pendingTodos: 2,
        openWishes: 1
      },
      members: [
        {
          id: 11,
          userId: 1,
          role: 'owner',
          roleText: '空间管理员',
          displayName: '铁铁',
          nickname: '铁铁',
          title: '御膳房掌柜',
          points: 126,
          contribution: 18,
          memberStatus: 'active',
          userStatus: 'active'
        },
        {
          id: 12,
          userId: 2,
          role: 'admin',
          roleText: '管理员',
          displayName: '小满',
          nickname: '小满',
          title: '头号点单官',
          points: 98,
          contribution: 12,
          memberStatus: 'active',
          userStatus: 'active'
        }
      ]
    }
  ],
  orders: [
    { id: 801, orderNo: 'SO2026052701', title: '买一杯奶茶', requester: { nickname: '小满' }, assignee: { nickname: '铁铁' }, statusLabel: '待接单', createdAt: '2026-05-27 10:15' }
  ],
  todos: [
    { id: 1, title: '今晚拿快递', assigneeId: 1, assigneeName: '铁铁', priorityLabel: '普通', dueText: '今天 21:00', status: 'pending' },
    { id: 2, title: '周末买水果', assigneeId: 2, assigneeName: '小满', priorityLabel: '中优先级', dueText: '周末', status: 'pending' }
  ],
  wishes: [
    { id: 1, title: '周末去看电影', type: 'date', status: 'open' }
  ],
  dressItems: [
    { id: 1, name: '白桃奶油', obtainType: 'free', rarity: 'R', pointPrice: 0, priceCent: 0, status: 'active' },
    { id: 2, name: '薄荷汽水', obtainType: 'point', rarity: 'SR', pointPrice: 25, priceCent: 0, status: 'active' },
    { id: 3, name: '夜间电影', obtainType: 'paid', rarity: 'SSR', pointPrice: 0, priceCent: 600, status: 'active' }
  ],
  activities: [
    { id: 1, name: '七日签到拿装扮', type: 'checkin', rewardPoint: 5, status: 'active' }
  ],
  payments: [],
  audits: [
    { id: 1, targetType: 'service_item', status: 'pass', remark: '系统示例内容已通过' }
  ],
  templates: []
}

const navs = [
  { key: 'overview', label: '仪表盘统计', icon: 'chart' },
  { key: 'spaces', label: '用户与空间管理', icon: 'users' },
  { key: 'dressItems', label: '装扮素材中心', icon: 'palette' }
]

const metricLabels = {
  users: '注册用户',
  spaces: '空间数',
  serviceItems: '服务项',
  orders: '服务订单',
  todos: '待办事项',
  dressItems: '装扮素材'
}

const metricCards = computed(() => [
  { key: 'users', label: '总注册用户数', value: dashboard.stats?.users ?? 0, trend: '较昨日 +1.2%', tone: 'up' },
  { key: 'spaces', label: '已创建空间数', value: dashboard.stats?.spaces ?? 0, trend: '较昨日 +0.8%', tone: 'up' },
  { key: 'orders', label: '今日服务订单', value: dashboard.stats?.orders ?? 0, trend: '较昨日 -2.1%', tone: 'down' },
  { key: 'audits', label: '待审核内容', value: dashboard.stats?.audits ?? dashboard.audits?.length ?? 0, trend: '需及时处理', tone: 'danger' }
])

const tableSchemas = {
  orders: [
    { key: 'orderNo', label: '订单号' },
    { key: 'title', label: '服务' },
    { key: 'requester.nickname', label: '发起人' },
    { key: 'assignee.nickname', label: '接单人' },
    { key: 'statusLabel', label: '状态' },
    { key: 'createdAt', label: '创建时间' }
  ],
  todos: [
    { key: 'title', label: '事项' },
    { key: 'assigneeName', label: '负责人' },
    { key: 'priorityLabel', label: '优先级' },
    { key: 'dueText', label: '截止时间' },
    { key: 'status', label: '状态' }
  ],
  dressItems: [
    { key: 'name', label: '装扮名称' },
    { key: 'obtainType', label: '获取方式' },
    { key: 'rarity', label: '稀有度' },
    { key: 'pointPrice', label: '积分价' },
    { key: 'priceCent', label: '价格分' },
    { key: 'status', label: '状态' }
  ],
  activities: [
    { key: 'name', label: '活动名称' },
    { key: 'type', label: '类型' },
    { key: 'rewardPoint', label: '奖励积分' },
    { key: 'startDate', label: '开始日期' },
    { key: 'endDate', label: '结束日期' },
    { key: 'status', label: '状态' }
  ],
  wishes: [
    { key: 'title', label: '愿望' },
    { key: 'type', label: '类型' },
    { key: 'creatorId', label: '创建人ID' },
    { key: 'claimedBy', label: '认领人ID' },
    { key: 'status', label: '状态' }
  ],
  payments: [
    { key: 'payNo', label: '支付单号' },
    { key: 'userId', label: '用户ID' },
    { key: 'spaceId', label: '空间ID' },
    { key: 'productType', label: '商品类型' },
    { key: 'amountCent', label: '金额分' },
    { key: 'status', label: '状态' },
    { key: 'paidAt', label: '支付时间' }
  ],
  audits: [
    { key: 'targetType', label: '内容类型' },
    { key: 'targetId', label: '目标ID' },
    { key: 'status', label: '状态' },
    { key: 'remark', label: '备注' },
    { key: 'createdAt', label: '创建时间' }
  ]
}

const spaceStatLabels = {
  pendingOrders: '待处理订单',
  completedOrders: '已完成订单',
  pendingTodos: '待办',
  openWishes: '开放愿望'
}

const spaceTypeLabels = {
  couple: '情侣空间',
  family: '家庭空间'
}

const statusLabels = {
  active: '正常',
  disabled: '停用',
  pending: '待处理',
  completed: '已完成',
  done: '已完成',
  pass: '已通过',
  open: '开放',
  claimed: '已认领',
  unknown: '未知'
}

const active = ref('overview')
const loading = ref(false)
const offline = ref(false)
const selectedSpaceId = ref(null)
const spaceQuery = ref('')
const spaceStatus = ref('all')
const dashboard = reactive({ ...fallback })

const dressForm = reactive({
  name: '',
  type: 'theme',
  obtainType: 'free',
  rarity: 'R',
  priceCent: 0,
  pointPrice: 0,
  status: 'active',
  background: 'linear-gradient(180deg,#fff7fb,#fff)',
  primaryColor: '#756f9d',
  cardColor: '#ffffff',
  textColor: '#202020'
})

const activityForm = reactive({
  name: '',
  type: 'checkin',
  description: '',
  rewardPoint: 5,
  startDate: '',
  endDate: '',
  status: 'active'
})

const activeTitle = computed(() => navs.find((item) => item.key === active.value)?.label || '管理台')
const spaces = computed(() => dashboard.spaces || [])
const totalMembers = computed(() => spaces.value.reduce((sum, space) => sum + memberCount(space), 0))
const averageMembers = computed(() => {
  if (!spaces.value.length) return 0
  return (totalMembers.value / spaces.value.length).toFixed(1)
})

const filteredSpaces = computed(() => {
  const keyword = spaceQuery.value.trim().toLowerCase()
  return spaces.value.filter((space) => {
    const matchesStatus = spaceStatus.value === 'all' || space.status === spaceStatus.value
    if (!keyword) return matchesStatus
    const haystack = [
      space.id,
      space.name,
      space.ownerName,
      space.announcement,
      space.type,
      ...(space.members || []).flatMap((member) => [member.userId, member.nickname, member.displayName, member.roleText])
    ]
      .filter((value) => value !== null && value !== undefined)
      .join(' ')
      .toLowerCase()
    return matchesStatus && haystack.includes(keyword)
  })
})

const selectedSpace = computed(() => {
  const scopedSpaces = spaceQuery.value.trim() || spaceStatus.value !== 'all' ? filteredSpaces.value : spaces.value
  return scopedSpaces.find((space) => space.id === selectedSpaceId.value) || scopedSpaces[0] || null
})

const selectedMembers = computed(() => selectedSpace.value?.members || [])
const selectedSpaceStats = computed(() => selectedSpace.value?.stats || {})
const dressRows = computed(() => dashboard.dressItems || [])
const dressTypeFilter = ref('all')
const dressObtainFilter = ref('all')
const dressEditorOpen = ref(false)
const dressEditingId = ref(null)
const dressSaving = ref(false)
const dressMessage = ref('')

const filteredDressRows = computed(() => {
  return dressRows.value.filter((item) => {
    const matchesType = dressTypeFilter.value === 'all' || (item.type || 'theme') === dressTypeFilter.value
    const matchesObtain = dressObtainFilter.value === 'all' || item.obtainType === dressObtainFilter.value
    return matchesType && matchesObtain
  })
})

const dressEditorTitle = computed(() => (dressEditingId.value ? '配置装扮JSON' : '上传新装扮'))

const activeRows = computed(() => {
  if (active.value === 'overview' || active.value === 'spaces') return []
  return dashboard[active.value] || []
})

const tableColumns = computed(() => {
  if (tableSchemas[active.value]) return tableSchemas[active.value]
  const first = activeRows.value[0] || {}
  return Object.keys(first)
    .slice(0, 7)
    .map((key) => ({ key, label: key }))
})

watch(
  filteredSpaces,
  (items) => {
    if (!items.length) {
      selectedSpaceId.value = null
      return
    }
    if (!items.some((space) => space.id === selectedSpaceId.value)) {
      selectedSpaceId.value = items[0].id
    }
  },
  { immediate: true }
)

async function request(path, options = {}) {
  const response = await fetch(`${apiBase}${path}`, {
    headers: { 'content-type': 'application/json' },
    ...options
  })
  const payload = await response.json()
  if (payload.code !== 0) {
    throw new Error(payload.message || '请求失败')
  }
  return payload.data
}

async function load() {
  loading.value = true
  try {
    const data = await request('/dashboard')
    Object.assign(dashboard, data)
    offline.value = false
  } catch (error) {
    Object.assign(dashboard, fallback)
    offline.value = true
  } finally {
    loading.value = false
  }
}

async function saveDress() {
  dressSaving.value = true
  dressMessage.value = ''
  const payload = { ...dressForm }
  try {
    await request(dressEditingId.value ? `/dress/items/${dressEditingId.value}` : '/dress/items', {
      method: dressEditingId.value ? 'PUT' : 'POST',
      body: JSON.stringify(payload)
    })
    await load()
    closeDressEditor()
    active.value = 'dressItems'
    dressMessage.value = '保存成功'
  } catch (error) {
    dressMessage.value = error.message || '保存失败'
  } finally {
    dressSaving.value = false
  }
}

async function saveActivity() {
  await request('/activities', {
    method: 'POST',
    body: JSON.stringify(activityForm)
  })
  Object.assign(activityForm, {
    name: '',
    description: '',
    rewardPoint: 5
  })
  await load()
  active.value = 'activities'
}

function selectSpace(spaceId) {
  selectedSpaceId.value = spaceId
}

function openCreateDress() {
  dressEditingId.value = null
  Object.assign(dressForm, {
    name: '',
    type: 'theme',
    obtainType: 'free',
    rarity: 'R',
    priceCent: 0,
    pointPrice: 0,
    status: 'active',
    background: 'linear-gradient(180deg,#fff,#f7f3ff)',
    primaryColor: '#9b88ed',
    cardColor: '#ffffff',
    textColor: '#333333'
  })
  dressMessage.value = ''
  dressEditorOpen.value = true
}

function openDressConfig(item) {
  dressEditingId.value = item.id
  Object.assign(dressForm, {
    name: item.name || '',
    type: item.type || 'theme',
    obtainType: item.obtainType || 'free',
    rarity: item.rarity || 'R',
    priceCent: item.priceCent || 0,
    pointPrice: item.pointPrice || 0,
    status: item.status || 'active',
    background: item.background || 'linear-gradient(180deg,#fff,#f7f3ff)',
    primaryColor: item.primaryColor || '#9b88ed',
    cardColor: item.cardColor || '#ffffff',
    textColor: item.textColor || '#333333'
  })
  dressMessage.value = ''
  dressEditorOpen.value = true
}

function closeDressEditor() {
  dressEditorOpen.value = false
  dressEditingId.value = null
}

async function toggleDressStatus(item) {
  dressMessage.value = ''
  const nextStatus = item.status === 'active' ? 'disabled' : 'active'
  try {
    await request(`/dress/items/${item.id}`, {
      method: 'PUT',
      body: JSON.stringify({ ...item, status: nextStatus })
    })
    await load()
    dressMessage.value = nextStatus === 'active' ? '已上架' : '已下架'
  } catch (error) {
    dressMessage.value = error.message || '操作失败'
  }
}

function memberCount(space) {
  return space.memberCount ?? space.members?.length ?? 0
}

function statusText(status) {
  return statusLabels[status] || status || '-'
}

function typeText(type) {
  return spaceTypeLabels[type] || type || '-'
}

function roleText(member) {
  return member.roleText || statusLabels[member.role] || member.role || '-'
}

function displayName(member) {
  return member.displayName || member.nickname || `用户 ${member.userId}`
}

function initials(value) {
  const text = value || '用'
  return Array.from(text)[0]
}

function valueByPath(row, path) {
  return path.split('.').reduce((value, key) => (value == null ? undefined : value[key]), row)
}

function formatValue(value) {
  if (value === null || value === undefined || value === '') return '-'
  if (typeof value === 'boolean') return value ? '是' : '否'
  if (typeof value === 'object') {
    if (value.nickname) return value.nickname
    if (value.name) return value.name
    return JSON.stringify(value)
  }
  return statusLabels[value] || value
}

function obtainTypeText(type) {
  const labels = {
    free: '免费初始',
    point: '积分兑换',
    paid: '付费购买',
    activity: '活动获取'
  }
  return labels[type] || type || '-'
}

function dressTypeText(type) {
  const labels = {
    theme: '全局主题',
    card: '卡片样式',
    background: '背景图'
  }
  return labels[type] || type || '全局主题'
}

function dressPriceText(item) {
  if (item.obtainType === 'point') return `${item.pointPrice || 0} 积分`
  if (item.obtainType === 'paid') return `${((item.priceCent || 0) / 100).toFixed(2)} 元`
  return '0'
}

function dressSwatchStyle(item, index) {
  const fallbackColors = ['#9b88ed', '#ffc9dc', '#7dd3c7', '#f3b35f']
  const fallback = fallbackColors[index % fallbackColors.length]
  const config = typeof item.configJson === 'string' ? tryParseJson(item.configJson) : item.configJson
  return {
    background: item.primaryColor || config?.primaryColor || fallback
  }
}

function tryParseJson(value) {
  try {
    return JSON.parse(value)
  } catch (error) {
    return null
  }
}

onMounted(load)
</script>

<template>
  <div class="admin-shell">
    <aside class="sidebar">
      <div class="brand">
        <strong>后台管理系统</strong>
      </div>

      <nav class="nav" aria-label="管理菜单">
        <button
          v-for="item in navs"
          :key="item.key"
          class="nav-item"
          :class="{ active: active === item.key }"
          type="button"
          @click="active = item.key"
        >
          <span class="nav-icon" :class="`icon-${item.icon}`" aria-hidden="true"></span>
          {{ item.label }}
        </button>
      </nav>
    </aside>

    <main class="workspace">
      <header class="topbar">
        <div class="breadcrumb">首页 <span>/</span> <strong>{{ activeTitle }}</strong></div>
        <div class="top-actions">
          <span v-if="offline" class="offline">后端未连接，正在展示预览数据</span>
          <button class="refresh" type="button" @click="load">{{ loading ? '同步中' : '刷新' }}</button>
          <span class="admin-user"><span class="admin-avatar" aria-hidden="true"></span>管理员_Zheng</span>
        </div>
      </header>

      <section v-if="active === 'overview'" class="overview">
        <div class="metric-grid">
          <article v-for="item in metricCards" :key="item.key" class="metric-card">
            <span>{{ item.label }}</span>
            <strong :class="{ danger: item.tone === 'danger' }">{{ item.value }}</strong>
            <small :class="item.tone">{{ item.trend }}</small>
          </article>
        </div>

        <div class="chart-placeholder">[ ECharts 走势图表预留区 - 订单与活跃用户量统计 ]</div>
      </section>

      <section v-else-if="active === 'spaces'" class="space-page">
        <section class="page-card">
          <div class="toolbar">
            <div class="filter-group">
              <input v-model="spaceQuery" placeholder="搜索空间ID/名称/成员" />
              <select v-model="spaceStatus">
                <option value="all">全部状态</option>
                <option value="active">正常</option>
                <option value="disabled">已停用</option>
              </select>
              <button class="outline-button" type="button">查询</button>
            </div>
            <div class="summary-inline">空间 {{ spaces.length }} 个 / 成员 {{ totalMembers }} 人 / 平均 {{ averageMembers }} 人</div>
          </div>

          <div v-if="!filteredSpaces.length" class="empty">暂无空间</div>
          <div v-else class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>空间ID</th>
                  <th>空间名称</th>
                  <th>类型</th>
                  <th>主理人</th>
                  <th>成员数</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="space in filteredSpaces"
                  :key="space.id"
                  :class="{ selected: selectedSpace?.id === space.id }"
                  @click="selectSpace(space.id)"
                >
                  <td>{{ space.id }}</td>
                  <td>
                    <strong>{{ space.name }}</strong>
                    <span class="muted">{{ space.announcement || '暂无公告' }}</span>
                  </td>
                  <td>{{ typeText(space.type) }}</td>
                  <td>{{ space.ownerName || '-' }}</td>
                  <td>{{ memberCount(space) }}</td>
                  <td>
                    <span class="status-pill" :class="`status-${space.status || 'unknown'}`">
                      {{ statusText(space.status) }}
                    </span>
                  </td>
                  <td>
                    <button class="outline-button" type="button" @click.stop="selectSpace(space.id)">详情</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-if="selectedSpace" class="page-card detail-card">
          <div class="card-header">
            <strong>{{ selectedSpace.name }}</strong>
            <span>ID {{ selectedSpace.id }} / {{ typeText(selectedSpace.type) }} / {{ selectedMembers.length }} 名成员</span>
          </div>

          <div class="detail-stats">
            <span>等级 {{ selectedSpace.level || 1 }}</span>
            <span>待处理订单 {{ selectedSpaceStats.pendingOrders ?? 0 }}</span>
            <span>已完成订单 {{ selectedSpaceStats.completedOrders ?? 0 }}</span>
            <span>待办 {{ selectedSpaceStats.pendingTodos ?? 0 }}</span>
            <span>开放愿望 {{ selectedSpaceStats.openWishes ?? 0 }}</span>
          </div>

          <div v-if="!selectedMembers.length" class="empty compact">暂无成员</div>
          <div v-else class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>用户ID</th>
                  <th>昵称/空间名</th>
                  <th>空间角色</th>
                  <th>当前积分</th>
                  <th>贡献值</th>
                  <th>账号状态</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="member in selectedMembers" :key="member.id || `${selectedSpace.id}-${member.userId}`">
                  <td>{{ member.userId }}</td>
                  <td>
                    <div class="member-cell">
                      <div class="avatar" aria-hidden="true">{{ initials(member.nickname || member.displayName) }}</div>
                      <div>
                        <strong>{{ member.nickname || '-' }}</strong>
                        <span class="muted">{{ displayName(member) }}</span>
                      </div>
                    </div>
                  </td>
                  <td>
                    <span class="status-pill role-tag">{{ roleText(member) }}</span>
                  </td>
                  <td>{{ member.points ?? 0 }}</td>
                  <td>{{ member.contribution ?? 0 }}</td>
                  <td>
                    <span class="status-pill" :class="`status-${member.userStatus || member.memberStatus || 'unknown'}`">
                      {{ statusText(member.userStatus || member.memberStatus) }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </section>

      <section v-else-if="active === 'dressItems'" class="dress-page">
        <section class="page-card">
          <div class="toolbar">
            <div class="filter-group">
              <select v-model="dressTypeFilter">
                <option value="all">全部类型 (背景/主题/卡片)</option>
                <option value="theme">全局主题</option>
                <option value="card">卡片样式</option>
                <option value="background">背景图</option>
              </select>
              <select v-model="dressObtainFilter">
                <option value="all">获取方式 (免费/付费/积分)</option>
                <option value="free">免费</option>
                <option value="point">积分</option>
                <option value="paid">付费</option>
                <option value="activity">活动</option>
              </select>
              <button class="outline-button" type="button">筛选</button>
            </div>
            <button class="primary" type="button" @click="openCreateDress">上传新装扮</button>
          </div>

          <p v-if="dressMessage" class="inline-message">{{ dressMessage }}</p>

          <div v-if="!filteredDressRows.length" class="empty">暂无装扮素材</div>
          <div v-else class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>预览</th>
                  <th>装扮名称</th>
                  <th>类型</th>
                  <th>获取方式</th>
                  <th>价格/积分</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in filteredDressRows" :key="item.id || item.name">
                  <td><span class="dress-swatch" :style="dressSwatchStyle(item, index)"></span></td>
                  <td>{{ item.name }}</td>
                  <td>{{ dressTypeText(item.type) }}</td>
                  <td>
                    <span class="status-pill" :class="item.obtainType === 'point' ? 'status-pending' : 'status-active'">
                      {{ obtainTypeText(item.obtainType) }}
                    </span>
                  </td>
                  <td>{{ dressPriceText(item) }}</td>
                  <td>
                    <span class="status-pill" :class="item.status === 'active' ? 'status-active' : 'status-disabled'">
                      {{ item.status === 'active' ? '已上架' : statusText(item.status) }}
                    </span>
                  </td>
                  <td>
                    <button class="outline-button" type="button" @click="openDressConfig(item)">配置JSON</button>
                    <button class="danger-button" type="button" @click="toggleDressStatus(item)">
                      {{ item.status === 'active' ? '下架' : '上架' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section v-if="dressEditorOpen" class="page-card detail-card">
          <div class="card-header">
            <strong>{{ dressEditorTitle }}</strong>
            <button class="outline-button" type="button" @click="closeDressEditor">关闭</button>
          </div>

          <div class="form-grid dress-form">
            <label>
              装扮名称
              <input v-model="dressForm.name" placeholder="例如：经典淡雅紫" />
            </label>
            <label>
              类型
              <select v-model="dressForm.type">
                <option value="theme">全局主题</option>
                <option value="card">卡片样式</option>
                <option value="background">背景图</option>
              </select>
            </label>
            <label>
              获取方式
              <select v-model="dressForm.obtainType">
                <option value="free">免费</option>
                <option value="point">积分</option>
                <option value="paid">付费</option>
                <option value="activity">活动</option>
              </select>
            </label>
            <label>
              稀有度
              <input v-model="dressForm.rarity" placeholder="R / SR / SSR" />
            </label>
            <label>
              价格分
              <input v-model.number="dressForm.priceCent" type="number" />
            </label>
            <label>
              积分价
              <input v-model.number="dressForm.pointPrice" type="number" />
            </label>
            <label>
              背景
              <input v-model="dressForm.background" placeholder="linear-gradient(...)" />
            </label>
            <label>
              主色
              <input v-model="dressForm.primaryColor" placeholder="#9b88ed" />
            </label>
            <label>
              卡片色
              <input v-model="dressForm.cardColor" placeholder="#ffffff" />
            </label>
            <label>
              文字色
              <input v-model="dressForm.textColor" placeholder="#333333" />
            </label>
            <label>
              状态
              <select v-model="dressForm.status">
                <option value="active">已上架</option>
                <option value="disabled">已下架</option>
              </select>
            </label>
          </div>

          <div class="editor-actions">
            <button class="primary" type="button" :disabled="dressSaving" @click="saveDress">
              {{ dressSaving ? '保存中' : '保存' }}
            </button>
            <button class="outline-button" type="button" @click="closeDressEditor">取消</button>
          </div>
        </section>
      </section>

      <section v-else class="panel table-panel">
        <div class="panel-head">
          <h2>{{ activeTitle }}</h2>
          <span>{{ activeRows.length }} 条记录</span>
        </div>
        <div v-if="!activeRows.length" class="empty">暂无数据</div>
        <div v-else class="table-wrap">
          <table>
            <thead>
              <tr>
                <th v-for="column in tableColumns" :key="column.key">{{ column.label }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in activeRows" :key="row.id || JSON.stringify(row)">
                <td v-for="column in tableColumns" :key="column.key">{{ formatValue(valueByPath(row, column.key)) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <footer class="footer">Powered by 智岑世码工作室 © 2026 | 基于 Vue3 + SpringBoot 构建</footer>
    </main>
  </div>
</template>

<style scoped>
.admin-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 240px minmax(0, 1fr);
  background: #eef1f5;
  color: #1f2933;
}

.sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  padding: 22px 14px;
  background: #0b1726;
  color: #f8fafc;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 8px 24px;
}

.brand-mark {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  display: grid;
  place-items: center;
  color: #ffffff;
  background: #6d5dfc;
  font-weight: 900;
}

.brand strong,
.brand span {
  display: block;
}

.brand strong {
  font-size: 15px;
}

.brand span,
.muted {
  color: #7b8794;
  font-size: 12px;
}

.nav {
  display: grid;
  gap: 4px;
}

.nav-item {
  min-height: 44px;
  padding: 0 14px;
  border: 0;
  border-radius: 8px;
  color: #bac4d0;
  background: transparent;
  text-align: left;
  font: inherit;
  cursor: pointer;
}

.nav-item.active,
.nav-item:hover,
.nav-item:focus-visible {
  color: #ffffff;
  background: #25364d;
  outline: 0;
}

.nav-item.active {
  background: #6d5dfc;
}

.workspace {
  min-width: 0;
  padding: 24px;
}

.topbar,
.space-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 18px;
}

.eyebrow {
  margin: 0 0 4px;
  color: #667085;
  font-size: 12px;
  font-weight: 800;
}

h1,
h2,
p {
  margin: 0;
}

h1 {
  font-size: 28px;
  line-height: 1.2;
  font-weight: 900;
}

h2 {
  font-size: 18px;
  line-height: 1.3;
  font-weight: 900;
}

.top-actions,
.filters,
.space-kpis {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.offline {
  color: #9a5b13;
  font-size: 13px;
}

.refresh,
.primary,
.link-button {
  min-height: 44px;
  border-radius: 8px;
  font-weight: 800;
  cursor: pointer;
}

.refresh,
.primary {
  border: 0;
  color: #ffffff;
  background: #5b5fc7;
}

.refresh {
  padding: 0 16px;
}

.primary {
  padding: 0 18px;
}

.link-button {
  border: 1px solid #c8d2e2;
  padding: 0 12px;
  color: #244c7a;
  background: #ffffff;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(120px, 1fr));
  gap: 14px;
}

.metric-card,
.panel {
  border: 1px solid #dbe2ea;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.06);
}

.metric-card {
  min-height: 106px;
  padding: 16px;
}

.metric-card span,
.summary-grid span,
.announcement span,
.space-kpis span {
  display: block;
  color: #667085;
  font-size: 12px;
  font-weight: 800;
}

.metric-card strong {
  display: block;
  margin-top: 16px;
  color: #111827;
  font-size: 32px;
  line-height: 1;
  font-weight: 900;
}

.split-grid,
.space-layout {
  display: grid;
  gap: 16px;
}

.split-grid {
  grid-template-columns: 1fr 1fr;
  margin-top: 18px;
}

.space-layout {
  grid-template-columns: minmax(420px, 0.9fr) minmax(360px, 1.1fr);
}

.panel {
  padding: 18px;
}

.panel-head,
.detail-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.panel-head span {
  color: #667085;
  font-size: 13px;
}

.member-head {
  margin-top: 18px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 14px;
}

label {
  display: grid;
  gap: 6px;
  color: #344054;
  font-size: 13px;
  font-weight: 800;
}

label.wide {
  grid-column: 1 / -1;
}

input,
select {
  width: 100%;
  min-height: 44px;
  border: 1px solid #cfd7e3;
  border-radius: 8px;
  padding: 0 12px;
  color: #1f2933;
  background: #ffffff;
  font: inherit;
}

input:focus,
select:focus,
button:focus-visible {
  border-color: #6d5dfc;
  outline: 3px solid rgba(109, 93, 252, 0.18);
}

.filters input {
  min-width: 260px;
}

.space-kpis {
  padding: 10px 12px;
  border: 1px solid #dbe2ea;
  border-radius: 8px;
  background: #ffffff;
}

.space-kpis div {
  min-width: 92px;
}

.space-kpis strong,
.summary-grid strong {
  display: block;
  margin-top: 4px;
  color: #111827;
  font-size: 22px;
  line-height: 1;
}

.table-panel {
  min-height: 460px;
}

.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

th,
td {
  padding: 12px 10px;
  border-bottom: 1px solid #e5eaf1;
  text-align: left;
  vertical-align: middle;
}

th {
  color: #52606d;
  background: #f5f7fa;
  font-weight: 900;
}

td {
  max-width: 280px;
  color: #243b53;
  word-break: break-word;
}

td strong {
  display: block;
  color: #1f2933;
}

tr.selected td {
  background: #f3f1ff;
}

.space-index tbody tr {
  cursor: pointer;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(110px, 1fr));
  border-top: 1px solid #e5eaf1;
  border-bottom: 1px solid #e5eaf1;
}

.summary-grid div {
  min-height: 76px;
  padding: 14px 12px;
  border-right: 1px solid #e5eaf1;
}

.summary-grid div:nth-child(3n) {
  border-right: 0;
}

.announcement {
  display: grid;
  gap: 6px;
  margin-top: 16px;
  padding: 14px 0 0;
}

.announcement p {
  color: #344054;
  line-height: 1.6;
}

.member-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: grid;
  flex: 0 0 auto;
  place-items: center;
  color: #ffffff;
  background: #1f7a8c;
  font-weight: 900;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 9px;
  border: 1px solid #dbe2ea;
  border-radius: 8px;
  color: #52606d;
  background: #f5f7fa;
  font-size: 12px;
  font-weight: 900;
}

.status-active,
.status-pass,
.status-completed,
.status-done {
  border-color: #b7e4c7;
  color: #1b7f4b;
  background: #edf9f1;
}

.status-pending,
.status-open,
.status-claimed {
  border-color: #f4d19b;
  color: #955f0b;
  background: #fff8eb;
}

.status-disabled {
  border-color: #f0b6b6;
  color: #aa2e25;
  background: #fff1f1;
}

.empty {
  padding: 72px 0;
  color: #667085;
  text-align: center;
}

.empty.compact {
  padding: 32px 0;
}

@media (max-width: 1120px) {
  .metric-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .space-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 860px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
    height: auto;
  }

  .nav {
    grid-template-columns: repeat(3, 1fr);
  }

  .workspace {
    padding: 16px;
  }

  .topbar,
  .space-toolbar {
    align-items: stretch;
    flex-direction: column;
  }

  .metric-grid,
  .split-grid,
  .summary-grid {
    grid-template-columns: 1fr 1fr;
  }

  .summary-grid div:nth-child(3n) {
    border-right: 1px solid #e5eaf1;
  }

  .summary-grid div:nth-child(2n) {
    border-right: 0;
  }
}

@media (max-width: 560px) {
  .metric-grid,
  .split-grid,
  .form-grid,
  .summary-grid,
  .nav {
    grid-template-columns: 1fr;
  }

  .filters input {
    min-width: 0;
  }

  .summary-grid div,
  .summary-grid div:nth-child(2n),
  .summary-grid div:nth-child(3n) {
    border-right: 0;
  }
}

/* Compact admin skin matching the supplied prototype. */
.admin-shell {
  grid-template-columns: 220px minmax(0, 1fr);
  min-height: 100vh;
  background: #f0f2f5;
  color: #303133;
  font-size: 14px;
}

.sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  padding: 0;
  background: #001529;
  color: #a6adb4;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.15);
}

.brand {
  height: 60px;
  justify-content: center;
  padding: 0;
  border-bottom: 1px solid #1a293b;
  color: #ffffff;
}

.brand strong {
  font-size: 18px;
  font-weight: 700;
}

.nav {
  gap: 0;
  padding: 10px 0;
}

.nav-item {
  min-height: 49px;
  padding: 0 22px;
  border-radius: 0;
  color: #cbd6e2;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 9px;
}

.nav-item.active,
.nav-item:hover,
.nav-item:focus-visible {
  color: #ffffff;
  background: #182b40;
}

.nav-item.active {
  background: #9b88ed;
}

.nav-icon {
  position: relative;
  width: 13px;
  height: 13px;
  flex: 0 0 auto;
  display: inline-block;
}

.icon-chart {
  border-left: 3px solid #67c23a;
  border-bottom: 3px solid #67c23a;
}

.icon-chart::before,
.icon-chart::after {
  content: "";
  position: absolute;
  bottom: 0;
  width: 3px;
  border-radius: 2px 2px 0 0;
}

.icon-chart::before {
  left: 5px;
  height: 9px;
  background: #9b88ed;
}

.icon-chart::after {
  left: 10px;
  height: 12px;
  background: #f6c343;
}

.icon-users::before,
.icon-users::after {
  content: "";
  position: absolute;
  border-radius: 50%;
  background: #8d64d8;
}

.icon-users::before {
  width: 7px;
  height: 7px;
  left: 1px;
  top: 1px;
  box-shadow: 0 7px 0 1px rgba(141, 100, 216, 0.58);
}

.icon-users::after {
  width: 6px;
  height: 6px;
  right: 0;
  top: 3px;
  box-shadow: 0 6px 0 1px rgba(141, 100, 216, 0.4);
}

.icon-palette {
  border-radius: 50%;
  background: conic-gradient(#f56c6c 0 25%, #f6c343 0 50%, #67c23a 0 75%, #9b88ed 0);
}

.icon-palette::after {
  content: "";
  position: absolute;
  width: 4px;
  height: 4px;
  right: 2px;
  top: 5px;
  border-radius: 50%;
  background: #001529;
}

.workspace {
  min-width: 0;
  height: 100vh;
  padding: 0;
  display: flex;
  flex-direction: column;
  overflow: auto;
  background: #f0f2f5;
}

.workspace > section {
  padding: 20px;
}

.topbar {
  height: 60px;
  flex: 0 0 auto;
  padding: 0 20px;
  margin: 0;
  border-bottom: 1px solid #ebeef5;
  background: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.breadcrumb {
  color: #606266;
  font-size: 14px;
}

.breadcrumb span {
  margin: 0 8px;
  color: #909399;
}

.breadcrumb strong {
  color: #303133;
}

.top-actions {
  gap: 12px;
}

.admin-user {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.admin-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: inline-block;
  background:
    linear-gradient(#ffd21f, #ffd21f) 50% 3px / 6px 22px no-repeat,
    linear-gradient(#ffd21f, #ffd21f) 3px 50% / 22px 6px no-repeat;
}

.offline {
  color: #e6a23c;
  font-size: 12px;
}

.refresh,
.primary,
.outline-button,
.link-button,
.danger-button {
  min-height: 36px;
  height: 36px;
  padding: 0 15px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
}

.refresh,
.primary {
  border: 1px solid #9b88ed;
  color: #ffffff;
  background: #9b88ed;
}

.outline-button,
.link-button {
  border: 1px solid #dcdfe6;
  color: #606266;
  background: #ffffff;
}

.outline-button:hover,
.link-button:hover {
  border-color: #c6bcf6;
  color: #9b88ed;
  background: #f3f0fc;
}

.danger-button {
  margin-left: 4px;
  border: 1px solid #fbc4c4;
  color: #f56c6c;
  background: #fef0f0;
}

.danger-button:hover {
  border-color: #f56c6c;
  color: #ffffff;
  background: #f56c6c;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.inline-message {
  margin: -8px 0 12px;
  color: #67c23a;
  font-size: 13px;
}

.metric-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.metric-card,
.panel,
.page-card {
  border: 0;
  border-radius: 4px;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.metric-card {
  min-height: 126px;
  padding: 20px;
}

.metric-card span {
  color: #909399;
  font-size: 14px;
  font-weight: 400;
}

.metric-card strong {
  margin-top: 8px;
  color: #303133;
  font-size: 30px;
  font-weight: 700;
}

.metric-card strong.danger {
  color: #f56c6c;
}

.metric-card small {
  display: block;
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}

.metric-card small.up {
  color: #67c23a;
}

.metric-card small.down {
  color: #e6a23c;
}

.chart-placeholder {
  height: 400px;
  margin-top: 40px;
  border: 1px dashed #c9cdd4;
  border-radius: 4px;
  display: grid;
  place-items: center;
  color: #909399;
  background: #fafafa;
  font-size: 16px;
}

.page-card,
.panel {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.filter-group,
.detail-stats {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

input,
select {
  min-height: 34px;
  height: 34px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 0 12px;
  color: #303133;
  background: #ffffff;
  font-size: 14px;
}

input:focus,
select:focus {
  border-color: #9b88ed;
  outline: 0;
}

button:focus-visible {
  outline: 2px solid rgba(155, 136, 237, 0.32);
  outline-offset: 1px;
}

.summary-inline {
  color: #909399;
  font-size: 13px;
}

table {
  font-size: 13px;
}

th,
td {
  padding: 12px 15px;
  border-bottom: 1px solid #ebeef5;
}

th {
  color: #909399;
  background: #f5f7fa;
  font-weight: 700;
}

td {
  max-width: 320px;
  color: #606266;
}

td strong {
  color: #303133;
  font-weight: 500;
}

tbody tr:hover td,
tr.selected td {
  background: #f5f7fa;
}

.muted {
  display: block;
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
}

.status-pill {
  min-height: 23px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 400;
}

.status-active,
.status-pass,
.status-completed,
.status-done {
  border-color: #e1f3d8;
  color: #67c23a;
  background: #f0f9eb;
}

.status-pending,
.status-open,
.status-claimed {
  border-color: #faecd8;
  color: #e6a23c;
  background: #fdf6ec;
}

.status-disabled {
  border-color: #fbc4c4;
  color: #f56c6c;
  background: #fef0f0;
}

.role-tag {
  border-color: #e9e9eb;
  color: #909399;
  background: #f4f4f5;
}

.detail-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 15px;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  font-size: 16px;
}

.card-header span,
.detail-stats {
  color: #909399;
  font-size: 13px;
}

.detail-stats {
  margin-bottom: 15px;
}

.detail-stats span {
  padding-right: 14px;
  border-right: 1px solid #ebeef5;
}

.detail-stats span:last-child {
  border-right: 0;
}

.dress-swatch {
  display: inline-block;
  width: 40px;
  height: 20px;
  border-radius: 4px;
  vertical-align: middle;
}

.dress-form {
  grid-template-columns: repeat(2, minmax(220px, 1fr));
  margin-bottom: 16px;
}

.editor-actions {
  display: flex;
  gap: 10px;
}

.member-cell {
  gap: 10px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  background: #e9eff7;
  color: #606266;
  font-size: 13px;
}

.empty {
  padding: 52px 0;
  color: #909399;
}

.empty.compact {
  padding: 24px 0;
}

.table-panel {
  min-height: 0;
  margin: 20px;
}

.footer {
  margin-top: auto;
  padding: 20px;
  color: #909399;
  text-align: center;
  font-size: 12px;
}

@media (max-width: 1120px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 860px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
    height: auto;
  }

  .nav {
    grid-template-columns: repeat(2, 1fr);
  }

  .workspace {
    height: auto;
  }

  .workspace > section {
    padding: 14px;
  }

  .topbar,
  .toolbar {
    align-items: stretch;
    flex-direction: column;
    height: auto;
    padding: 12px 14px;
  }
}
</style>
