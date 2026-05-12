# 校友有点闲 开发日志

## 项目概况

项目名称：校友有点闲

项目定位：面向高校师生的校园二手交易平台，支持普通用户发布商品、管理员审核商品、买家下单购买、卖家发货、买家确认收货，并逐步扩展首页专区、后台统计和特色分类管理能力。

当前目录结构：

1. backend：Spring Boot + MyBatis-Plus + MySQL 后端
2. frontend：Vue3 + Vite 前端
3. DEVELOPMENT-LOG.md：开发阶段、真实联调与问题修复记录
4. PROJECT-DEMO-SCRIPT.md：演示脚本
5. LOGIN-METHODS.md：账号与登录说明

## 开发阶段记录

### 第一阶段：项目方案与后端骨架搭建

已完成内容：

1. 明确项目为前后端分离结构
2. 后端采用 Spring Boot + Maven
3. 接入 MyBatis-Plus、MySQL、Lombok
4. 建立基础分层结构：controller、service、service impl、mapper、entity、dto、common、config
5. 统一返回结果 Result<T>
6. 补充全局异常处理与 MyBatis-Plus 分页配置

核心文件：

1. backend/pom.xml
2. backend/src/main/resources/application.yml
3. backend/src/main/java/com/campus/trade/CampusTradeApplication.java

### 第二阶段：数据库设计与初始化脚本

已完成内容：

1. 设计用户表、分类表、商品表、购物车表、订单表、订单明细表
2. 增加商品图片表 goods_image
3. 增加商品审核记录表 goods_audit_log
4. 增加特色分类表 featured_category
5. 增加商品与特色分类关联表 goods_featured_category
6. 编写 schema.sql 和 data.sql
7. 补充旧库升级脚本 upgrade-existing-db.sql
8. 补充默认账号密码重置脚本 reset-default-passwords.sql
9. 补充演示商品数据脚本 seed_more_goods.sql

核心文件：

1. backend/src/main/resources/schema.sql
2. backend/src/main/resources/data.sql
3. backend/upgrade-existing-db.sql
4. backend/reset-default-passwords.sql
5. backend/seed_more_goods.sql

### 第三阶段：后端核心业务实现

已完成内容：

1. 注册与登录接口
2. JWT 登录鉴权
3. 个人资料查询、修改、修改密码、头像上传
4. 商品分类查询
5. 商品分页、商品详情、我的商品、商品发布、编辑、上下架
6. 商品多图保存与展示
7. 管理员商品审核、审核备注、审核记录
8. 购物车新增、列表、删除
9. 订单提交、买家订单、卖家订单、订单详情、取消订单、发货、确认收货
10. 管理员用户分页与启用/禁用
11. 管理员首页概览统计、趋势统计、分类分布统计
12. 特色分类专区查询
13. 管理员特色分类分页、新增、编辑
14. 管理员对待审核商品和在售商品分配特色分类

核心文件：

1. backend/src/main/java/com/campus/trade/service/impl/UserServiceImpl.java
2. backend/src/main/java/com/campus/trade/service/impl/GoodsServiceImpl.java
3. backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java
4. backend/src/main/java/com/campus/trade/service/impl/CartServiceImpl.java
5. backend/src/main/java/com/campus/trade/controller/GoodsController.java
6. backend/src/main/java/com/campus/trade/controller/OrderController.java
7. backend/src/main/java/com/campus/trade/controller/AdminUserController.java
8. backend/src/main/java/com/campus/trade/controller/AdminOverviewController.java
9. backend/src/main/java/com/campus/trade/controller/AdminFeaturedCategoryController.java

### 第四阶段：前端项目初始化

已完成内容：

1. 创建 Vue3 + Vite 前端子项目
2. 配置 Vue Router 与 Pinia
3. 封装 Axios 请求层
4. 配置开发代理，联调 backend 端口
5. 设计统一全局样式

核心文件：

1. frontend/package.json
2. frontend/vite.config.js
3. frontend/src/router/index.js
4. frontend/src/stores/auth.js
5. frontend/src/utils/request.js
6. frontend/src/styles/global.css

### 第五阶段：前端页面开发

已完成页面：

1. 登录/注册页
2. 首页
3. 商品详情页
4. 个人资料页
5. 发布商品页
6. 我的商品页
7. 订单页
8. 管理员首页
9. 管理员特色分类管理页

页面增强内容：

1. 全局 loading
2. 全局 toast
3. 空状态组件
4. 分页组件
5. 商品图片失败占位
6. 商品编辑弹层
7. 订单详情弹层
8. 后台表格化管理视图

核心文件：

1. frontend/src/views/HomeView.vue
2. frontend/src/views/LoginView.vue
3. frontend/src/views/GoodsDetailView.vue
4. frontend/src/views/ProfileView.vue
5. frontend/src/views/PublishGoodsView.vue
6. frontend/src/views/MyGoodsView.vue
7. frontend/src/views/OrdersView.vue
8. frontend/src/views/AdminDashboardView.vue
9. frontend/src/views/AdminFeaturedCategoriesView.vue

### 第六阶段：首页产品化改造

已完成内容：

1. 首页主视觉区产品化文案
2. 分类入口区
3. 特色专区区
4. 在售商品区
5. 平台优势区
6. 交易流程区
7. 特色专区每个专题默认每页展示 3 件商品
8. 在售商品默认每页展示 6 件商品
9. 顶部品牌统一为：
主标题：Campus Re:Trade
副标题：校友有点闲

核心文件：

1. frontend/src/views/HomeView.vue
2. frontend/src/components/AppHeader.vue
3. frontend/src/components/AppFooter.vue

### 第七阶段：后台管理模块拆分与商品管理增强

已完成内容：

1. 新增管理员商品管理页，支持商品列表、筛选、详情查看、编辑、上架、下架和删除
2. 后端补充管理员商品编辑、状态变更、删除接口
3. 管理员删除商品时同步清理商品图片记录、审核记录、特色分类关联和购物车引用
4. 将原管理后台中的商品审核模块拆分为独立页面
5. 将原管理后台中的用户管理模块拆分为独立页面
6. 管理后台首页改为总览页，保留统计卡片和图表
7. 管理后台首页新增四个单行入口卡片：商品审核、商品管理、用户管理、特色分类管理
8. 顶部导航在“管理后台”右侧按顺序展示：商品审核、商品管理、用户管理、特色分类管理
9. 四个管理页面均保留分页切换能力

核心文件：

1. frontend/src/views/AdminDashboardView.vue
2. frontend/src/views/AdminGoodsAuditView.vue
3. frontend/src/views/AdminGoodsManagementView.vue
4. frontend/src/views/AdminUsersView.vue
5. frontend/src/views/AdminFeaturedCategoriesView.vue
6. frontend/src/router/index.js
7. frontend/src/components/AppHeader.vue
8. frontend/src/api/goods.js
9. backend/src/main/java/com/campus/trade/controller/GoodsController.java
10. backend/src/main/java/com/campus/trade/service/GoodsService.java
11. backend/src/main/java/com/campus/trade/service/impl/GoodsServiceImpl.java

### 第八阶段：个人资料体系与公开资料页增强

已完成内容：

1. 扩展用户资料字段，新增性别、个人简介、学校、学院、专业、年级、微信号、QQ号、邮箱、常用交易地点、支持交易方式、是否接受议价、常在线时间、公开手机号、公开微信号
2. 重构个人资料页，按“基础资料、联系方式、交易偏好、隐私设置”四个分区展示
3. 将修改密码功能并入“隐私设置”分区
4. 新增公开资料接口，支持按用户 ID 查看公开资料
5. 新增公开资料页，展示卖家的基础资料、联系方式和交易偏好
6. 让“公开手机号/公开微信号”真正影响公开资料页展示结果，未公开时显示“未公开”
7. 在商品详情页增加卖家资料入口，可点击跳转到公开资料页
8. 在订单详情弹层中，将买家/卖家 ID 改为可跳转公开资料的用户昵称
9. 为个人资料修改增加前后端双层校验，覆盖手机号、邮箱、QQ、微信号和文本长度限制
10. 更新新库建表脚本和旧库升级脚本，并为本地数据库补齐资料字段

核心文件：

1. frontend/src/views/ProfileView.vue
2. frontend/src/views/PublicProfileView.vue
3. frontend/src/views/GoodsDetailView.vue
4. frontend/src/views/OrdersView.vue
5. frontend/src/api/auth.js
6. frontend/src/router/index.js
7. frontend/src/utils/validators.js
8. backend/src/main/java/com/campus/trade/controller/UserController.java
9. backend/src/main/java/com/campus/trade/dto/UserProfileUpdateRequest.java
10. backend/src/main/java/com/campus/trade/dto/UserVO.java
11. backend/src/main/java/com/campus/trade/dto/OrderVO.java
12. backend/src/main/java/com/campus/trade/entity/User.java
13. backend/src/main/java/com/campus/trade/service/UserService.java
14. backend/src/main/java/com/campus/trade/service/impl/UserServiceImpl.java
15. backend/src/main/java/com/campus/trade/service/impl/OrderServiceImpl.java
16. backend/src/main/resources/schema.sql
17. backend/upgrade-existing-db.sql

### 第九阶段：用户收藏功能增强

已完成内容：

1. 新增收藏表 favorite_goods，用于记录用户与商品的收藏关系
2. 新增用户收藏商品接口与取消收藏接口
3. 新增“我的收藏”接口，返回当前用户收藏的商品列表
4. 在商品详情页增加“收藏商品 / 取消收藏”按钮
5. 顶部导航新增“我的收藏”入口
6. 新增“我的收藏”页面，支持查看收藏商品和取消收藏
7. 为商品 VO 增加 favorited 标记，用于前端判断当前商品是否已被收藏
8. 管理员删除商品时同步清理关联的收藏记录，避免脏数据残留
9. 更新新库建表脚本和旧库升级脚本，并为本地数据库补齐收藏表

核心文件：

1. frontend/src/views/GoodsDetailView.vue
2. frontend/src/views/MyFavoritesView.vue
3. frontend/src/components/AppHeader.vue
4. frontend/src/router/index.js
5. frontend/src/api/goods.js
6. backend/src/main/java/com/campus/trade/controller/GoodsController.java
7. backend/src/main/java/com/campus/trade/dto/GoodsVO.java
8. backend/src/main/java/com/campus/trade/entity/FavoriteGoods.java
9. backend/src/main/java/com/campus/trade/mapper/FavoriteGoodsMapper.java
10. backend/src/main/java/com/campus/trade/service/GoodsService.java
11. backend/src/main/java/com/campus/trade/service/impl/GoodsServiceImpl.java
12. backend/src/main/resources/schema.sql
13. backend/upgrade-existing-db.sql

### 第十阶段：留言咨询、消息通知与监管能力增强

已完成内容：

1. 新增商品留言表 goods_message，支持商品详情页的留言与回复数据存储
2. 新增商品留言接口，支持买家发布顶层留言、其他用户对任意留言继续回复
3. 商品详情页新增“留言 / 咨询”区，支持递归回复，交互模式接近评论区
4. 每条留言和回复均展示发送时间，按两行显示年月日与时分秒
5. 新增站内通知表 user_notification，用于消息提醒买卖双方的新留言与新回复
6. 顶部导航新增“消息中心”，并展示未读数量角标
7. 新增消息中心页面，支持查看详情、标记已读、全部已读、单条删除、全部删除
8. 通知列表按最新回复/通知时间倒序排列，越新的消息越靠前
9. 新增管理员“留言管理”页面，可查看买卖家留言与回复并进行删除监管
10. 管理员留言管理支持按留言内容和发送者用户名筛选，并支持全部删除
11. 管理后台首页新增“留言管理”入口，顶部导航同步新增“留言管理”入口
12. 更新新库建表脚本和旧库升级脚本，并为本地数据库补齐 goods_message 与 user_notification 表

核心文件：

1. frontend/src/views/GoodsDetailView.vue
2. frontend/src/components/GoodsMessageThread.vue
3. frontend/src/views/NotificationsView.vue
4. frontend/src/views/AdminGoodsMessagesView.vue
5. frontend/src/components/AppHeader.vue
6. frontend/src/views/AdminDashboardView.vue
7. frontend/src/api/auth.js
8. frontend/src/api/admin.js
9. frontend/src/router/index.js
10. backend/src/main/java/com/campus/trade/controller/GoodsMessageController.java
11. backend/src/main/java/com/campus/trade/controller/AdminGoodsMessageController.java
12. backend/src/main/java/com/campus/trade/controller/UserNotificationController.java
13. backend/src/main/java/com/campus/trade/dto/GoodsMessageSaveRequest.java
14. backend/src/main/java/com/campus/trade/dto/GoodsMessageVO.java
15. backend/src/main/java/com/campus/trade/dto/AdminGoodsMessageQueryDTO.java
16. backend/src/main/java/com/campus/trade/dto/UserNotificationVO.java
17. backend/src/main/java/com/campus/trade/entity/GoodsMessage.java
18. backend/src/main/java/com/campus/trade/entity/UserNotification.java
19. backend/src/main/java/com/campus/trade/mapper/GoodsMessageMapper.java
20. backend/src/main/java/com/campus/trade/mapper/UserNotificationMapper.java
21. backend/src/main/java/com/campus/trade/service/GoodsMessageService.java
22. backend/src/main/java/com/campus/trade/service/UserNotificationService.java
23. backend/src/main/java/com/campus/trade/service/impl/GoodsMessageServiceImpl.java
24. backend/src/main/java/com/campus/trade/service/impl/UserNotificationServiceImpl.java
25. backend/src/main/resources/schema.sql
26. backend/upgrade-existing-db.sql

### 第十一阶段：举报商品、后台处理与信用统计增强

已完成内容：

1. 新增举报表 goods_report，用于记录用户对商品的举报信息
2. 新增商品详情页“举报商品”入口和举报弹窗
3. 新增用户举报接口，支持按举报类型、举报原因和补充说明提交举报
4. 新增管理员“举报管理”页面，支持分页查看举报记录
5. 新增管理员处理举报接口，支持忽略、下架商品、删除商品、禁用用户等处理结果
6. 举报处理结果可联动商品状态或用户状态，形成平台治理闭环
7. 新增用户信用统计规则，基于成交、审核通过商品、有效举报和处罚次数实时计算信用分
8. 公开资料页新增信用与成交信息展示
9. 后台用户管理页新增信用分、信用等级、有效举报次数展示
10. 更新新库建表脚本和旧库升级脚本，并为本地数据库补齐 goods_report 表结构

核心文件：

1. frontend/src/views/GoodsDetailView.vue
2. frontend/src/views/AdminGoodsReportsView.vue
3. frontend/src/views/PublicProfileView.vue
4. frontend/src/views/AdminUsersView.vue
5. frontend/src/views/AdminDashboardView.vue
6. frontend/src/components/AppHeader.vue
7. frontend/src/router/index.js
8. frontend/src/api/goods.js
9. frontend/src/api/admin.js
10. backend/src/main/java/com/campus/trade/controller/GoodsReportController.java
11. backend/src/main/java/com/campus/trade/controller/AdminGoodsReportController.java
12. backend/src/main/java/com/campus/trade/dto/GoodsReportSaveRequest.java
13. backend/src/main/java/com/campus/trade/dto/AdminGoodsReportQueryDTO.java
14. backend/src/main/java/com/campus/trade/dto/AdminHandleGoodsReportRequest.java
15. backend/src/main/java/com/campus/trade/dto/GoodsReportVO.java
16. backend/src/main/java/com/campus/trade/dto/UserCreditVO.java
17. backend/src/main/java/com/campus/trade/dto/UserVO.java
18. backend/src/main/java/com/campus/trade/entity/GoodsReport.java
19. backend/src/main/java/com/campus/trade/mapper/GoodsReportMapper.java
20. backend/src/main/java/com/campus/trade/service/GoodsReportService.java
21. backend/src/main/java/com/campus/trade/service/UserService.java
22. backend/src/main/java/com/campus/trade/service/impl/GoodsReportServiceImpl.java
23. backend/src/main/java/com/campus/trade/service/impl/UserServiceImpl.java
24. backend/src/main/resources/schema.sql
25. backend/upgrade-existing-db.sql

### 第十二阶段：举报通知、匿名详情与状态中文化收尾

已完成内容：

1. 新增举报详情页，支持从消息中心查看举报处理结果的匿名详情
2. 举报详情页只展示被举报商品、举报类型、举报原因、举报状态、处理结果和处理备注，不展示举报人信息
3. 为历史举报记录补发举报处理结果通知，确保举报人和被举报商家都能在消息中心收到对应提醒
4. 修复举报通知点击“查看详情”时报错的问题，统一举报详情接口路径
5. 优化消息中心排版，补充已读 / 未读状态、双行时间显示、单条删除和全部删除操作
6. 管理员留言管理页支持按发送者用户名筛选，并新增“全部删除”与“处理时间”展示
7. 用户管理、商品审核、商品详情、我的商品、订单页、特色分类管理页等用户可见状态值统一改为中文映射展示
8. 个人资料页新增“信用与成交”分区，供用户直接查看信用与成交统计

核心文件：

1. frontend/src/views/NotificationsView.vue
2. frontend/src/views/ReportDetailView.vue
3. frontend/src/views/AdminGoodsMessagesView.vue
4. frontend/src/views/AdminGoodsReportsView.vue
5. frontend/src/views/ProfileView.vue
6. frontend/src/views/AdminUsersView.vue
7. frontend/src/views/AdminGoodsAuditView.vue
8. frontend/src/views/MyGoodsView.vue
9. frontend/src/views/OrdersView.vue
10. frontend/src/views/GoodsDetailView.vue
11. frontend/src/views/AdminFeaturedCategoriesView.vue
12. frontend/src/components/AppHeader.vue
13. frontend/src/api/goods.js
14. backend/src/main/java/com/campus/trade/controller/GoodsReportController.java
15. backend/src/main/java/com/campus/trade/service/impl/GoodsReportServiceImpl.java

### 第十三阶段：项目文档规范化与后端测试建设

已完成内容：

1. 统一整理项目文档目录，将开发日志、演示脚本、登录说明集中放入 docs 目录管理
2. 新增数据库设计说明文档，系统梳理表结构、字段含义、表关系和业务数据流
3. 新增接口说明文档，补充接口参数、权限要求、错误响应、状态值和返回示例
4. 更新 README，补充文档入口、测试建设说明和更准确的后续优化方向
5. 为后端引入 H2 测试依赖和测试环境配置，建立可重复执行的集成测试基础设施
6. 新增后端服务层关键集成测试，覆盖登录、商品审核、订单流转和举报处理主链路
7. 新增 Controller 层接口测试，覆盖登录、未登录拦截、管理员权限校验、参数校验和部分关键接口返回行为
8. 修复管理员特色分类接口缺少显式管理员权限校验的问题，确保文档与实现保持一致
9. 前端顶部品牌区改为使用 logo 图片资源，统一品牌展示形式

核心文件：

1. README.md
2. docs/API.md
3. docs/DATABASE-DESIGN.md
4. docs/DEVELOPMENT-LOG.md
5. backend/pom.xml
6. backend/src/test/resources/application-test.yml
7. backend/src/test/java/com/campus/trade/IntegrationTestSupport.java
8. backend/src/test/java/com/campus/trade/service/UserServiceIntegrationTest.java
9. backend/src/test/java/com/campus/trade/service/GoodsServiceIntegrationTest.java
10. backend/src/test/java/com/campus/trade/service/OrderServiceIntegrationTest.java
11. backend/src/test/java/com/campus/trade/service/GoodsReportServiceIntegrationTest.java
12. backend/src/test/java/com/campus/trade/controller/ControllerTestSupport.java
13. backend/src/test/java/com/campus/trade/controller/AuthControllerTest.java
14. backend/src/test/java/com/campus/trade/controller/OrderControllerTest.java
15. backend/src/test/java/com/campus/trade/controller/GoodsReportControllerTest.java
16. backend/src/test/java/com/campus/trade/controller/AdminGoodsReportControllerTest.java
17. backend/src/main/java/com/campus/trade/controller/AdminFeaturedCategoryController.java
18. frontend/src/components/AppHeader.vue
19. frontend/src/assets/brand-logo-generated.png


## 测试发现的运行问题

测试环境：

1. 后端目录：backend
2. 前端目录：frontend
3. 后端端口：8081
4. 前端端口：5173
5. 数据库：campus_trade

已自主确认的核心能力：

1. 默认管理员账号可以正常登录：admin / your_demo_password
2. 普通用户可完成商品发布、管理员审核、买家下单、卖家发货、买家确认收货的主链路
3. 管理后台统计、用户管理、商品管理、留言管理、举报管理等页面可以访问
4. 个人资料页四个资料分区、公开资料页、收藏列表、消息中心等新增页面可以访问
5. 商品详情页支持收藏、留言、递归回复、举报商品等用户侧增强能力
6. 公开资料页和后台用户管理页可以展示信用统计信息

由自主测试发现并反馈、且已修复的问题：

1. 卖家回复买家留言时，会提示“卖家无法对自己的商品进行顶层留言”，导致卖家无法回复。已调整留言回复逻辑，允许对现有留言进行正常回复。
2. 买家或卖家收到新留言 / 新回复时，没有任何消息通知，导致双方无法及时获知沟通状态。已新增站内消息通知和消息中心。
3. 留言右侧时间直接显示为 `yyyy-MM-ddTHH:mm:ss`，其中 `T` 影响观感。已改为两行显示：第一行年月日，第二行时分秒。
4. “沟通监管”命名不够直观。已统一更名为“留言管理”。
5. 留言管理页面最初按昵称筛选不符合测试需求。已改为按用户名筛选发送者。
6. 消息中心最初缺少已读状态、单条删除、全部删除等管理能力，且排序与展示方式不够友好。现已支持未读/已读、单条删除、全部删除，并按最新时间倒序显示。
7. 消息中心页面留白偏多，排版观感一般。已优化状态栏、时间位置和删除按钮布局。
8. 个人资料页最初缺少更严格的输入校验。现已补充手机号、邮箱、QQ、微信号及长度限制校验。
9. 公开手机号 / 微信号最初仅能保存设置，不影响实际展示。现已在公开资料页中按隐私开关生效。
10. 订单详情里买家 / 卖家最初只显示 ID，不利于查看资料。现已改为可跳转公开资料页的用户昵称。
11. 用户收藏功能最初不存在。现已支持商品收藏、取消收藏和“我的收藏”列表管理。
12. 商品详情页最初不支持举报商品，后台也没有对应治理页面。现已补齐举报商品入口、后台举报管理和处理能力。
13. 公开资料页和后台用户页最初缺少信用与成交统计展示。现已补充信用分、信用等级和相关统计数据。
14. 补发的举报通知最初出现问号显示，原因是终端字符集写入异常。已修正通知内容。
15. 点击举报通知“查看详情”最初会报 `No static resource api/goods-reports/...`。已修复前后端举报详情接口路径并重新启动后端。
16. 用户可见页面中仍残留部分英文状态值。现已统一映射为中文展示。

## 当前项目状态

当前项目已经达到：

1. 基础业务闭环跑通
2. 前后端目录独立
3. 旧库升级脚本可用
4. 默认测试账号可用
5. 前端页面具备基础产品化展示效果
6. 后台管理已覆盖审核、商品、用户、统计、特色分类管理
7. 管理后台首页已形成总览与入口导航，具体管理操作拆分到独立页面
8. 用户资料体系已覆盖联系信息、交易偏好、隐私开关与公开资料展示
9. 用户侧已具备商品收藏、取消收藏和收藏列表管理能力
10. 商品咨询、消息通知与留言监管能力已形成闭环
11. 举报商品、后台处理与信用统计能力已形成最小可用闭环

## 后续可继续开发的方向

1. 商品列表独立页与更多筛选排序
2. 首页特色专区“查看全部”页
3. 管理员特色分类删除与拖拽排序
4. 订单页加入购物车到下单的完整可视化入口
5. 更完整的错误边界与空数据引导
6. 文件上传前的静态提示与压缩处理
7. 部署文档和演示环境脚本
8. 管理员商品管理操作增加二次确认、操作日志和更细粒度权限说明
9. 公开资料页可继续增加历史成交、信用标签、校园认证等信任信息
10. 收藏列表可继续补充筛选、分页和收藏时间展示
11. 留言与消息通知可继续补充敏感词过滤、举报机制和消息类型图标
12. 举报管理可继续补充处理详情弹窗、敏感词识别和举报重复限制策略