# 情侣/家庭服务点单小程序开发文档

## 1. 项目定位

本项目是一个给情侣、夫妻、家庭或小团体使用的轻量服务点单小程序。核心体验类似“点菜”：一方配置可选服务菜单，另一方在小程序里选择服务并提交，服务提供方收到通知后处理。

产品前期主打情侣场景，功能保持简洁、有趣、可装扮；后续扩展到多人家庭、朋友合租、小团队互助等场景。

当前项目结构：

```text
couple/
├── admin-front       # 管理端，Vue + Vite
├── wx-front          # 微信小程序端，微信原生小程序
└── couple-backend    # 后端，Spring Boot + MySQL
```

## 2. 核心用户角色

### 2.1 普通用户

通过微信小程序登录的用户，可以创建或加入一个关系空间。

### 2.2 空间管理员

通常是情侣中的男方、女方，或家庭中的任意管理者。可以管理服务分类、服务项目、成员、装扮、任务和部分空间设置。

### 2.3 服务请求方

进入菜单页，选择服务并下单的人。例如女方点“想喝奶茶”“抱抱”“帮我拿快递”。

### 2.4 服务处理方

收到服务通知并处理的人。例如男方收到请求后确认、完成或拒绝。

### 2.5 平台管理员

通过 `admin-front` 使用后台管理系统，配置活动、装扮素材、商品、用户反馈、违规内容等。

## 3. 功能总览

## 3.1 MVP 必做功能

### 微信登录

- 微信小程序一键登录。
- 后端通过 `code2Session` 获取 `openid`。
- 首次登录自动创建用户。
- 支持用户昵称、头像、性别、生日等基础资料。

### 关系空间

- 用户可以创建一个空间。
- 用户可以通过邀请码或二维码加入空间。
- MVP 阶段一个用户默认只加入一个主空间。
- 空间支持情侣模式，后续扩展家庭模式、朋友模式。

### 绑定关系

- 创建空间后生成邀请链接或邀请码。
- 对方加入后形成双人空间。
- 空间成员有角色：管理员、成员。
- 管理员可以移除成员、修改空间名称、设置空间封面。

### 服务分类

- 管理员可以创建分类，例如：
  - 今日投喂
  - 情绪安抚
  - 跑腿服务
  - 陪伴服务
  - 家务服务
- 分类可以设置排序、图标、是否显示。

### 服务项目

- 管理员可以在分类下创建服务项目。
- 服务项目字段建议包含：
  - 服务名称
  - 服务描述
  - 服务图片或图标
  - 所属分类
  - 预计完成时间
  - 消耗积分或免费
  - 是否需要填写备注
  - 是否需要选择时间
  - 是否上架
- 示例服务：
  - 买一杯奶茶
  - 抱抱 5 分钟
  - 陪看一集剧
  - 帮忙拿快递
  - 晚饭我来做

### 服务下单

- 请求方进入菜单页。
- 按分类浏览服务。
- 点击服务后确认下单。
- 可以填写备注，例如口味、时间、地址。
- 提交后生成服务订单。

### 服务通知

- 服务处理方收到微信订阅消息。
- 小程序内也显示待处理消息。
- 订单状态包括：
  - 待接单
  - 已接单
  - 已完成
  - 已取消
  - 已拒绝
- MVP 优先使用微信订阅消息；站内消息作为兜底。

### 服务订单记录

- 双方都可以查看历史点单记录。
- 支持按状态筛选。
- 支持查看订单详情。
- 完成后可以简单评价，例如“满意”“超棒”“下次还点”。

### 待办事项

- 空间成员可以创建待办。
- 可以指定执行人。
- 可以设置截止时间、优先级、备注。
- 支持完成、取消、延期。
- 示例：
  - 周五前交水电费
  - 明天拿快递
  - 周末买猫粮
  - 晚上记得吃药

### 装扮系统

- 管理员可以设置服务菜单页的视觉装扮。
- 可装扮内容：
  - 背景图
  - 分类按钮样式
  - 服务卡片样式
  - 主题色
  - 字体颜色
  - 顶部标题文案
  - 小挂件或角标
- 装扮可以来自：
  - 系统免费装扮
  - 活动获得装扮
  - 付费购买装扮
  - 签到兑换装扮

### 付费能力预留

- 支持购买装扮、会员或空间权益。
- MVP 可以先设计数据结构和订单流程，不一定第一版就接微信支付。
- 付费商品示例：
  - 主题背景包
  - 服务卡片皮肤
  - 高级空间装扮
  - 更多服务分类数量
  - 更多空间成员数量

## 3.2 建议补充功能

这些是容易遗漏但很适合本产品的功能，可以按优先级逐步加入。

### 1. 愿望清单

用户可以把想要的服务、礼物、约会计划放进愿望清单。对方可以选择认领。

适用场景：

- 想吃某家店
- 想去某个地方
- 想要某个小礼物
- 想安排一次电影夜

### 2. 情绪状态

用户可以设置当天状态，例如开心、累了、需要陪伴、不想说话。菜单页可以根据状态展示推荐服务。

### 3. 快捷暗号

给常用需求设置暗号按钮，例如“救救我”“想你了”“快夸我”。点击后直接通知对方。

### 4. 积分系统

完成服务、签到、完成待办可以获得空间积分。积分用于兑换装扮或解锁小权益。

积分来源：

- 每日签到
- 完成服务订单
- 完成待办
- 连续互动
- 活动奖励

积分用途：

- 兑换背景图
- 兑换主题色
- 解锁限定服务图标
- 兑换称号

### 5. 签到活动

平台管理员可以配置签到活动。用户签到获得积分、装扮碎片或限定主题。

活动字段：

- 活动名称
- 活动时间
- 每日奖励
- 连续签到奖励
- 奖励装扮
- 是否启用

### 6. 纪念日

空间可以设置纪念日，例如恋爱纪念日、生日、结婚纪念日。小程序首页可以轻量展示倒计时。

### 7. 服务冷却时间

某些服务可以设置每天可点次数或冷却时间，避免频繁误点或刷屏。

### 8. 服务预约时间

支持“现在需要”和“预约时间”。例如今晚 8 点陪散步、明天中午买饭。

### 9. 服务模板广场

平台提供一些系统模板，用户可以一键导入：

- 情侣甜蜜模板
- 异地恋模板
- 家务分工模板
- 家庭点餐模板
- 合租待办模板

### 10. 家庭多人模式

后续支持一个空间多个成员。服务和待办都可以指定执行人，也可以设置多个候选处理人。

场景示例：

- 孩子点晚餐，父母收到通知
- 家庭成员指派家务
- 合租成员分配公共事项

### 11. 亲密度/贡献值

通过互动、完成服务、连续签到增加空间亲密度或贡献值。该功能不宜太复杂，只作为轻量反馈。

### 12. 黑名单与隐私

- 退出空间。
- 解散空间。
- 拉黑用户。
- 隐藏历史订单。
- 敏感服务仅自己可见或仅指定成员可见。

### 13. 防打扰设置

- 免打扰时间段。
- 某些服务不发送订阅消息，只进站内消息。
- 紧急服务可以绕过普通提醒规则。

### 14. 内容审核

服务名称、备注、背景图、头像等用户生成内容需要预留审核能力。前期可以先做基础敏感词过滤，后续接微信内容安全接口。

### 15. 数据回顾

每周或每月生成轻量回顾：

- 点了多少次服务
- 完成了多少待办
- 最常点的服务
- 最常互动的时间
- 本月获得了哪些装扮

## 4. 小程序页面规划

## 4.1 首页

目标：让用户快速进入点单和待办。

内容：

- 空间名称
- 当前装扮背景
- 今日状态
- 快捷入口：
  - 服务菜单
  - 待办事项
  - 我的订单
  - 装扮空间
- 纪念日或签到入口

## 4.2 服务菜单页

目标：完成核心点单体验。

内容：

- 背景图和主题样式
- 分类横向切换或纵向列表
- 服务卡片列表
- 服务详情弹窗
- 备注输入
- 预约时间选择
- 确认下单按钮

## 4.3 订单页

目标：查看请求和处理服务。

Tab：

- 我发起的
- 我收到的

状态筛选：

- 待接单
- 进行中
- 已完成
- 已取消

## 4.4 待办页

内容：

- 待办列表
- 创建待办
- 指定执行人
- 截止时间
- 优先级
- 完成状态

## 4.5 装扮页

内容：

- 当前装扮预览
- 我的装扮
- 装扮商店
- 活动装扮
- 积分兑换

## 4.6 空间设置页

内容：

- 空间名称
- 成员列表
- 邀请成员
- 成员权限
- 解散或退出空间
- 菜单管理入口

## 4.7 菜单管理页

给空间管理员使用。

内容：

- 分类管理
- 服务项目管理
- 排序
- 上下架
- 服务次数限制
- 服务价格或积分消耗

## 4.8 我的页

内容：

- 用户资料
- 我的积分
- 我的装扮
- 签到入口
- 反馈建议
- 设置

## 5. 管理端功能规划

管理端位于 `admin-front`，主要给平台管理员使用。

## 5.1 登录与权限

- 管理员账号登录。
- 角色权限：
  - 超级管理员
  - 运营管理员
  - 内容审核员

## 5.2 用户管理

- 用户列表
- 用户详情
- 所属空间
- 登录时间
- 封禁/解封
- 积分调整

## 5.3 空间管理

- 空间列表
- 空间成员
- 空间服务数量
- 空间订单统计
- 解散异常空间

## 5.4 装扮管理

- 背景图管理
- 主题管理
- 卡片样式管理
- 挂件管理
- 上下架
- 免费/付费/活动获得
- 稀有度配置

## 5.5 商品管理

- 商品列表
- 商品类型：
  - 装扮
  - 会员权益
  - 积分包
  - 空间扩容
- 价格
- 库存或限量
- 上下架

## 5.6 活动管理

- 签到活动
- 节日活动
- 限时装扮活动
- 连续互动奖励
- 活动奖励配置

## 5.7 订单与支付管理

- 支付订单列表
- 支付状态
- 退款状态
- 微信支付流水号
- 异常订单处理

## 5.8 内容审核

- 用户上传图片审核
- 服务名称审核
- 备注敏感词记录
- 举报处理

## 5.9 系统配置

- 微信小程序配置
- 微信支付配置
- 订阅消息模板配置
- 积分规则
- 默认装扮
- 默认服务模板

## 6. 后端模块设计

后端位于 `couple-backend`。

建议包结构：

```text
com.example.couplebackend
├── common          # 通用返回、异常、工具类
├── config          # Spring、Redis、MyBatis、微信配置
├── auth            # 登录认证
├── user            # 用户
├── space           # 关系空间和成员
├── menu            # 服务分类和服务项目
├── order           # 服务订单
├── todo            # 待办事项
├── dress           # 装扮系统
├── activity        # 活动和签到
├── point           # 积分
├── payment         # 支付
├── message         # 订阅消息和站内消息
├── admin           # 管理端接口
└── audit           # 内容审核
```

## 7. 技术栈

## 7.1 后端

基础技术：

- Java 21
- Spring Boot
- Spring Web MVC
- MySQL 8
- Redis，可选但建议接入
- Maven

建议增加：

- MyBatis-Plus：简化 CRUD 和分页。
- Spring Validation：参数校验。
- Spring Security 或 Sa-Token：登录态和权限控制。小项目推荐 Sa-Token，上手更快。
- JWT：小程序和管理端接口鉴权。
- Lombok：减少样板代码。
- Knife4j / springdoc-openapi：接口文档。
- Flyway 或 Liquibase：数据库版本管理。
- Hutool：常用工具。
- WxJava：微信小程序登录、订阅消息、微信支付封装。

Redis 用途：

- 登录 token 缓存。
- 验证码或临时邀请码。
- 订阅消息防重复发送。
- 签到状态缓存。
- 热门模板、装扮配置缓存。
- 分布式锁，例如支付回调幂等处理。

## 7.2 数据库

- MySQL 保存核心业务数据。
- 所有表建议包含：
  - `id`
  - `created_at`
  - `updated_at`
  - `deleted`
- 使用逻辑删除，避免误删关系数据。
- 金额字段使用 `BIGINT` 保存分，避免浮点误差。

## 7.3 小程序端

当前目录：`wx-front`

建议技术：

- 微信原生小程序。
- 原生 WXML/WXSS/JS。
- 使用微信登录能力。
- 使用微信订阅消息。
- 使用微信支付能力。

如果后续页面变复杂，可以考虑迁移到 Taro 或 uni-app，但 MVP 用微信原生更轻。

## 7.4 管理端

当前目录：`admin-front`

建议技术：

- Vue 3
- Vite
- Pinia：状态管理
- Vue Router：路由
- Axios：请求
- Element Plus：管理端组件库
- ECharts：运营数据图表

管理端应保持实用，不做复杂动效。重点是列表、表单、审核、配置和数据统计。

## 7.5 文件存储

背景图、装扮图、头像等图片不建议直接存数据库。

推荐：

- 开发阶段：本地文件存储或 MinIO。
- 上线阶段：腾讯云 COS / 阿里云 OSS / 七牛云。

数据库只保存文件 URL、宽高、大小、审核状态。

## 7.6 部署

建议：

- 后端：Docker + Spring Boot Jar
- 数据库：MySQL 8
- 缓存：Redis
- 管理端：Nginx 静态部署
- 小程序：微信开发者工具上传发布

## 8. 数据库表设计草案

以下是第一版核心表，字段可根据实现再细化。

## 8.1 用户表 `user`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| openid | VARCHAR(64) | 微信 openid |
| unionid | VARCHAR(64) | 微信 unionid，可为空 |
| nickname | VARCHAR(64) | 昵称 |
| avatar_url | VARCHAR(255) | 头像 |
| gender | TINYINT | 性别 |
| birthday | DATE | 生日 |
| status | TINYINT | 状态 |
| last_login_at | DATETIME | 最后登录时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

## 8.2 空间表 `space`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| name | VARCHAR(64) | 空间名称 |
| type | VARCHAR(32) | couple/family/team |
| owner_user_id | BIGINT | 创建者 |
| avatar_url | VARCHAR(255) | 空间头像 |
| current_theme_id | BIGINT | 当前主题 |
| status | TINYINT | 状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

## 8.3 空间成员表 `space_member`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| space_id | BIGINT | 空间 ID |
| user_id | BIGINT | 用户 ID |
| role | VARCHAR(32) | owner/admin/member |
| display_name | VARCHAR(64) | 空间内昵称 |
| joined_at | DATETIME | 加入时间 |
| status | TINYINT | 状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

## 8.4 邀请表 `space_invite`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| space_id | BIGINT | 空间 ID |
| invite_code | VARCHAR(32) | 邀请码 |
| created_by | BIGINT | 创建人 |
| expire_at | DATETIME | 过期时间 |
| max_use_count | INT | 最大使用次数 |
| used_count | INT | 已使用次数 |
| status | TINYINT | 状态 |

## 8.5 服务分类表 `service_category`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| space_id | BIGINT | 空间 ID |
| name | VARCHAR(64) | 分类名称 |
| icon_url | VARCHAR(255) | 图标 |
| sort_order | INT | 排序 |
| visible | TINYINT | 是否显示 |
| created_by | BIGINT | 创建人 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

## 8.6 服务项目表 `service_item`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| space_id | BIGINT | 空间 ID |
| category_id | BIGINT | 分类 ID |
| name | VARCHAR(64) | 服务名称 |
| description | VARCHAR(255) | 服务描述 |
| image_url | VARCHAR(255) | 图片 |
| point_cost | INT | 消耗积分 |
| require_remark | TINYINT | 是否需要备注 |
| require_appoint_time | TINYINT | 是否需要预约时间 |
| daily_limit | INT | 每日限制次数 |
| cooldown_minutes | INT | 冷却时间 |
| sort_order | INT | 排序 |
| status | TINYINT | 状态 |
| created_by | BIGINT | 创建人 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

## 8.7 服务订单表 `service_order`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| order_no | VARCHAR(64) | 订单号 |
| space_id | BIGINT | 空间 ID |
| service_item_id | BIGINT | 服务项目 ID |
| requester_id | BIGINT | 发起人 |
| assignee_id | BIGINT | 处理人 |
| status | VARCHAR(32) | pending/accepted/completed/canceled/rejected |
| remark | VARCHAR(500) | 备注 |
| appoint_time | DATETIME | 预约时间 |
| accepted_at | DATETIME | 接单时间 |
| completed_at | DATETIME | 完成时间 |
| canceled_at | DATETIME | 取消时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

## 8.8 待办表 `todo_item`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| space_id | BIGINT | 空间 ID |
| title | VARCHAR(100) | 标题 |
| description | VARCHAR(500) | 描述 |
| creator_id | BIGINT | 创建人 |
| assignee_id | BIGINT | 执行人 |
| priority | TINYINT | 优先级 |
| due_time | DATETIME | 截止时间 |
| status | VARCHAR(32) | pending/done/canceled |
| completed_at | DATETIME | 完成时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

## 8.9 装扮表 `dress_item`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| name | VARCHAR(64) | 装扮名称 |
| type | VARCHAR(32) | background/theme/card/widget |
| image_url | VARCHAR(255) | 资源 URL |
| config_json | JSON | 样式配置 |
| obtain_type | VARCHAR(32) | free/paid/activity/point |
| price_cent | BIGINT | 价格，单位分 |
| point_price | INT | 积分价格 |
| rarity | VARCHAR(32) | 稀有度 |
| status | TINYINT | 状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

## 8.10 空间装扮拥有表 `space_dress`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| space_id | BIGINT | 空间 ID |
| dress_item_id | BIGINT | 装扮 ID |
| obtained_by | BIGINT | 获取人 |
| obtain_source | VARCHAR(32) | 来源 |
| active | TINYINT | 是否当前使用 |
| created_at | DATETIME | 创建时间 |

## 8.11 积分流水表 `point_record`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| space_id | BIGINT | 空间 ID |
| user_id | BIGINT | 用户 ID |
| change_value | INT | 积分变化 |
| balance_after | INT | 变化后余额 |
| biz_type | VARCHAR(32) | 业务类型 |
| biz_id | BIGINT | 业务 ID |
| remark | VARCHAR(255) | 备注 |
| created_at | DATETIME | 创建时间 |

## 8.12 签到表 `checkin_record`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| space_id | BIGINT | 空间 ID |
| user_id | BIGINT | 用户 ID |
| checkin_date | DATE | 签到日期 |
| continuous_days | INT | 连续天数 |
| reward_point | INT | 奖励积分 |
| reward_dress_id | BIGINT | 奖励装扮 |
| created_at | DATETIME | 创建时间 |

## 8.13 支付订单表 `payment_order`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| pay_no | VARCHAR(64) | 支付单号 |
| user_id | BIGINT | 用户 ID |
| space_id | BIGINT | 空间 ID |
| product_type | VARCHAR(32) | 商品类型 |
| product_id | BIGINT | 商品 ID |
| amount_cent | BIGINT | 金额，单位分 |
| status | VARCHAR(32) | pending/paid/closed/refunded |
| wx_transaction_id | VARCHAR(128) | 微信支付流水号 |
| paid_at | DATETIME | 支付时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

## 8.14 站内消息表 `message`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键 |
| space_id | BIGINT | 空间 ID |
| receiver_id | BIGINT | 接收人 |
| sender_id | BIGINT | 发送人 |
| type | VARCHAR(32) | 消息类型 |
| title | VARCHAR(100) | 标题 |
| content | VARCHAR(500) | 内容 |
| biz_id | BIGINT | 关联业务 ID |
| read_status | TINYINT | 是否已读 |
| created_at | DATETIME | 创建时间 |

## 9. 接口规划

接口统一前缀建议：

```text
/api/wx/**       # 小程序接口
/api/admin/**    # 管理端接口
```

## 9.1 小程序接口

### 登录

```text
POST /api/wx/auth/login
```

请求：

```json
{
  "code": "微信登录 code"
}
```

返回：

```json
{
  "token": "登录 token",
  "user": {}
}
```

### 获取当前空间

```text
GET /api/wx/spaces/current
```

### 创建空间

```text
POST /api/wx/spaces
```

### 加入空间

```text
POST /api/wx/spaces/join
```

### 获取服务菜单

```text
GET /api/wx/spaces/{spaceId}/menu
```

### 创建分类

```text
POST /api/wx/service-categories
```

### 创建服务项目

```text
POST /api/wx/service-items
```

### 发起服务订单

```text
POST /api/wx/service-orders
```

### 接单

```text
POST /api/wx/service-orders/{id}/accept
```

### 完成订单

```text
POST /api/wx/service-orders/{id}/complete
```

### 取消订单

```text
POST /api/wx/service-orders/{id}/cancel
```

### 获取待办列表

```text
GET /api/wx/todos
```

### 创建待办

```text
POST /api/wx/todos
```

### 完成待办

```text
POST /api/wx/todos/{id}/complete
```

### 获取装扮列表

```text
GET /api/wx/dress/items
```

### 应用装扮

```text
POST /api/wx/dress/items/{id}/apply
```

### 签到

```text
POST /api/wx/checkin
```

## 9.2 管理端接口

```text
POST /api/admin/auth/login
GET  /api/admin/users
GET  /api/admin/spaces
GET  /api/admin/service-orders
GET  /api/admin/todos
GET  /api/admin/dress/items
POST /api/admin/dress/items
PUT  /api/admin/dress/items/{id}
POST /api/admin/dress/items/{id}/enable
POST /api/admin/dress/items/{id}/disable
GET  /api/admin/activities
POST /api/admin/activities
PUT  /api/admin/activities/{id}
GET  /api/admin/payment-orders
GET  /api/admin/audit/records
```

## 10. 权限规则

## 10.1 小程序权限

- 用户只能访问自己所在空间的数据。
- 空间管理员可以管理菜单、成员、装扮。
- 普通成员可以点单、创建待办、完成分配给自己的待办。
- 服务订单的发起人可以取消订单。
- 服务订单的处理人可以接单、完成、拒绝。
- 空间 owner 可以解散空间。

## 10.2 管理端权限

- 超级管理员拥有全部权限。
- 运营管理员可以配置活动、装扮、商品。
- 审核员只能处理内容审核和举报。

## 11. 消息通知设计

## 11.1 微信订阅消息

适合发送：

- 新服务订单提醒
- 服务被接单提醒
- 服务完成提醒
- 待办分配提醒
- 待办临近截止提醒
- 签到奖励提醒

注意：

- 微信订阅消息需要用户授权。
- 每类消息需要配置模板。
- 用户未授权时，写入站内消息兜底。

## 11.2 站内消息

站内消息用于补充订阅消息。

消息类型：

- service_order_created
- service_order_accepted
- service_order_completed
- todo_assigned
- todo_due
- dress_obtained
- activity_reward

## 12. 支付设计

## 12.1 商品类型

- 装扮商品
- 积分包
- 会员权益
- 空间扩容

## 12.2 支付流程

1. 用户选择商品。
2. 后端创建支付订单。
3. 后端调用微信支付生成预支付参数。
4. 小程序调用微信支付。
5. 微信支付回调后端。
6. 后端校验签名和金额。
7. 更新支付订单状态。
8. 发放商品权益。

## 12.3 幂等要求

支付回调必须幂等。可以使用：

- 支付单状态判断。
- Redis 分布式锁。
- 数据库唯一索引。

## 13. 装扮配置设计

装扮建议使用 `config_json` 存样式，便于后续扩展。

示例：

```json
{
  "background": {
    "imageUrl": "https://cdn.example.com/bg/sakura.png",
    "color": "#fff5f7"
  },
  "category": {
    "activeColor": "#ff6b9a",
    "inactiveColor": "#ffffff",
    "textColor": "#333333"
  },
  "card": {
    "backgroundColor": "rgba(255,255,255,0.86)",
    "borderColor": "#ffd1dc",
    "radius": 16
  },
  "button": {
    "backgroundColor": "#ff6b9a",
    "textColor": "#ffffff"
  }
}
```

小程序端根据配置动态渲染：

- 页面背景。
- 分类按钮。
- 服务卡片。
- 主按钮。
- 标题文案。

## 14. 推荐 MVP 迭代路线

## 14.1 第一阶段：基础闭环

目标：能登录、绑定、配置菜单、点单、收到消息。

任务：

- 微信登录。
- 用户表和空间表。
- 创建/加入空间。
- 分类管理。
- 服务项目管理。
- 服务菜单展示。
- 服务下单。
- 订单状态流转。
- 站内消息。

## 14.2 第二阶段：体验完善

目标：让产品更像情侣小程序，而不是普通工具。

任务：

- 微信订阅消息。
- 首页装扮。
- 服务卡片图标。
- 纪念日。
- 签到。
- 积分。
- 装扮兑换。
- 待办事项。

## 14.3 第三阶段：管理端上线

目标：平台可以运营活动和装扮。

任务：

- 管理端登录。
- 用户管理。
- 空间管理。
- 装扮管理。
- 活动管理。
- 签到奖励配置。
- 内容审核。

## 14.4 第四阶段：商业化

目标：接入付费能力。

任务：

- 商品管理。
- 微信支付。
- 支付订单。
- 支付回调。
- 装扮发放。
- 会员权益。

## 14.5 第五阶段：多人空间

目标：扩展情侣以外的场景。

任务：

- 空间多成员。
- 服务指定处理人。
- 待办多人协作。
- 家庭服务模板。
- 成员权限细化。

## 15. 开发优先级

P0：

- 微信登录
- 创建/加入空间
- 分类和服务项目
- 服务菜单
- 服务下单
- 订单状态
- 站内消息

P1：

- 微信订阅消息
- 待办事项
- 装扮基础配置
- 签到
- 积分
- 管理端装扮管理

P2：

- 活动管理
- 装扮商店
- 微信支付
- 内容审核
- 纪念日
- 愿望清单

P3：

- 多人家庭模式
- 数据回顾
- 服务模板广场
- 会员权益
- 高级权限

## 16. 前端体验原则

小程序要简洁，不要像复杂后台。

设计建议：

- 首页只保留最重要入口。
- 点单流程最多 2 步：选择服务，确认提交。
- 管理菜单入口不要干扰普通点单。
- 装扮要有趣，但不能影响文字可读性。
- 情侣模式可以温柔可爱，家庭模式要清爽实用。
- 支付入口不要过早打扰用户，先让用户形成使用习惯。

## 17. 后端实现建议

## 17.1 统一返回格式

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

## 17.2 异常处理

使用全局异常处理：

- 参数错误
- 未登录
- 无权限
- 资源不存在
- 业务状态错误
- 系统异常

## 17.3 业务状态校验

服务订单状态需要严格校验：

- 待接单才能接单。
- 待接单或已接单才能取消。
- 已接单才能完成。
- 已完成不能再次修改。

## 17.4 数据隔离

所有空间内数据查询必须带 `space_id`。这是后续支持多人、多空间的基础。

## 17.5 图片审核

用户上传装扮背景图时，需要：

- 限制大小。
- 限制格式。
- 内容审核。
- 审核通过后才允许其他成员可见。

## 18. 风险与注意事项

### 微信订阅消息限制

订阅消息不是无限发送，用户需要授权。必须有站内消息兜底。

### 付费装扮审核

如果涉及支付和虚拟商品，需要遵守微信小程序平台规则。商品描述、发放记录、退款处理都要清晰。

### 情侣关系解除

需要设计退出和解绑流程。历史数据是否保留要给用户选择。

### 用户上传图片

背景图和头像可能涉及违规内容，必须预留审核能力。

### 多人模式提前预留

即使 MVP 只做情侣双人，也不要把数据库写死成 `boy_user_id` 和 `girl_user_id`。应该用 `space` + `space_member` 模型，后续才能自然扩展。

## 19. 推荐首版页面清单

小程序首版：

- 登录页
- 首页
- 创建/加入空间页
- 服务菜单页
- 服务确认弹窗
- 订单列表页
- 订单详情页
- 菜单管理页
- 分类编辑页
- 服务编辑页
- 我的页

管理端首版：

- 登录页
- 用户列表
- 空间列表
- 装扮列表
- 装扮编辑
- 活动列表
- 活动编辑

后端首版：

- 登录接口
- 空间接口
- 成员接口
- 分类接口
- 服务项目接口
- 服务订单接口
- 站内消息接口

## 20. 推荐近期开发顺序

1. 后端搭建基础依赖：MySQL、MyBatis-Plus、Sa-Token/JWT、统一返回、全局异常。
2. 设计并创建核心表：用户、空间、成员、分类、服务、订单、消息。
3. 完成微信登录。
4. 完成创建空间和邀请加入。
5. 完成服务分类和服务项目 CRUD。
6. 完成小程序服务菜单展示。
7. 完成服务下单和订单状态流转。
8. 完成站内消息。
9. 再接微信订阅消息。
10. 加入待办、签到、积分和基础装扮。

