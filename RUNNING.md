# 本地运行说明

## 后端

目录：`couple-backend`

```bash
mvn spring-boot:run
```

默认地址：

```text
http://localhost:8080
```

小程序接口：

```text
GET http://localhost:8080/api/wx/bootstrap
```

管理端接口：

```text
GET http://localhost:8080/api/admin/dashboard
```

当前已接入 MyBatis Spring Boot Starter，并配置了 MySQL / Redis 连接信息。MyBatis Mapper 位于：

```text
couple-backend/src/main/java/com/example/couplebackend/mapper
```

为了没有数据库时也能快速打开小程序看页面，当前业务 Service 仍保留 `memory/MemoryDataStore` 作为演示数据源。切换真实数据库时，把 Service 中的 `MemoryDataStore` 调用替换成对应 `*Mapper` 即可。

MySQL 表结构在：

```text
couple-backend/src/main/resources/schema.sql
```

## 管理端

目录：`admin-front`

安装依赖：

```bash
npm install
```

开发模式：

```bash
npm run dev
```

构建：

```bash
npm run build
```

本项目额外提供一个轻量预览脚本：

```bash
node serve-dist.cjs
```

默认预览地址：

```text
http://127.0.0.1:4173
```

## 小程序

目录：`wx-front`

使用微信开发者工具打开该目录。开发阶段需要在微信开发者工具里开启“不校验合法域名”，接口地址默认指向：

```text
http://localhost:8080/api/wx
```

如果后端没有启动，小程序会展示本地预览数据，但新增、接单、签到等写操作需要后端运行。

## 已覆盖功能

- 微信小程序点单菜单。
- 分类和服务管理。
- 服务订单创建、接单、完成、取消。
- 心情展示和心情切换。
- 快捷暗号。
- 待办事项和指定执行人。
- 装扮主题、签到、积分、模拟购买。
- 愿望清单和认领。
- 纪念日。
- 服务模板导入。
- 多成员空间数据模型。
- 管理端总览、用户、空间、订单、待办、装扮、活动、愿望、审核。
- MySQL 建表脚本预留。
