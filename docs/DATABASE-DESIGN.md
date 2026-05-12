# 校友有点闲 数据库设计说明

## 1. 设计目标

本项目数据库服务于校园二手交易平台，围绕“用户发布商品、管理员审核、买家下单、卖家发货、买家确认收货、平台治理”的完整业务闭环设计。

数据库设计重点如下：

1. 支持用户账号、个人资料、公开资料和隐私设置。
2. 支持商品分类、商品多图、商品审核、上下架和售出状态管理。
3. 支持购物车、订单、订单明细和库存扣减。
4. 支持商品收藏、留言咨询、站内消息通知和商品举报。
5. 支持管理员后台统计、用户治理、商品治理、留言监管、举报处理和首页特色专区维护。

初始化脚本位于：

1. `backend/src/main/resources/schema.sql`
2. `backend/src/main/resources/data.sql`

旧数据库升级脚本位于：

1. `backend/upgrade-existing-db.sql`
2. `backend/reset-default-passwords.sql`

脚本使用场景说明：

1. 首次本地运行、且数据库不存在时，只需执行 `schema.sql` 和 `data.sql` 即可。
2. `upgrade-existing-db.sql` 仅用于已经存在旧版本数据库、但又不想删库重建的场景，用来补字段、补表和补基础数据。
3. `reset-default-passwords.sql` 仅用于旧数据库中默认账号密码与当前 BCrypt 登录逻辑不一致的场景。
4. 如果当前环境没有旧库历史数据，这两个兼容脚本通常不需要执行，但保留在仓库中可用于兼容旧库迁移和还原开发演进过程。

## 2. 表清单

| 表名 | 中文含义 | 主要用途 |
| --- | --- | --- |
| `sys_user` | 用户表 | 保存普通用户和管理员账号、个人资料、联系方式、角色和状态 |
| `category` | 商品分类表 | 保存商品基础分类，如教材书籍、数码电子、生活用品等 |
| `goods` | 商品表 | 保存商品主信息、卖家、分类、价格、库存和业务状态 |
| `goods_image` | 商品图片表 | 保存商品多图信息 |
| `goods_audit_log` | 商品审核记录表 | 保存管理员对商品的审核结果和审核备注 |
| `featured_category` | 特色分类表 | 保存首页特色专区，如毕业甩卖、新生必备等 |
| `goods_featured_category` | 商品特色分类关联表 | 维护商品与特色专区的多对多关系 |
| `cart_item` | 购物车表 | 保存用户加入购物车的商品和数量 |
| `orders` | 订单表 | 保存订单主信息、买家、卖家、金额和订单状态 |
| `order_item` | 订单明细表 | 保存订单中的商品快照、数量和小计金额 |
| `favorite_goods` | 商品收藏表 | 保存用户收藏商品关系 |
| `goods_message` | 商品留言表 | 保存商品咨询留言和回复 |
| `user_notification` | 用户通知表 | 保存站内消息、审核结果、留言提醒等通知 |
| `goods_report` | 商品举报表 | 保存用户对商品的举报和管理员处理结果 |

## 3. 通用字段约定

多数业务表包含以下通用字段：

| 字段 | 含义 |
| --- | --- |
| `id` | 主键，自增 BIGINT |
| `deleted` | 逻辑删除标记，`0` 表示未删除，`1` 表示已删除 |
| `create_time` | 创建时间 |
| `update_time` | 更新时间 |

项目使用 MyBatis-Plus 逻辑删除配置：

| 配置项 | 值 |
| --- | --- |
| 逻辑删除字段 | `deleted` |
| 未删除值 | `0` |
| 已删除值 | `1` |

## 4. 核心表结构

### 4.1 用户表 sys_user

`sys_user` 保存平台账号、角色、状态、个人资料和公开联系方式设置。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 用户 ID |
| `username` | VARCHAR(50) | 登录用户名，唯一 |
| `password` | VARCHAR(100) | BCrypt 加密后的密码 |
| `nickname` | VARCHAR(50) | 用户昵称 |
| `avatar` | VARCHAR(255) | 头像地址 |
| `phone` | VARCHAR(20) | 手机号 |
| `gender` | VARCHAR(20) | 性别 |
| `bio` | VARCHAR(500) | 个人简介 |
| `school` | VARCHAR(100) | 学校 |
| `college` | VARCHAR(100) | 学院 |
| `major` | VARCHAR(100) | 专业 |
| `grade` | VARCHAR(50) | 年级 |
| `wechat` | VARCHAR(50) | 微信号 |
| `qq` | VARCHAR(30) | QQ 号 |
| `email` | VARCHAR(100) | 邮箱 |
| `trade_location` | VARCHAR(100) | 常用交易地点 |
| `trade_methods` | VARCHAR(100) | 支持交易方式 |
| `accept_bargain` | TINYINT | 是否接受议价，`1` 是，`0` 否 |
| `online_time` | VARCHAR(100) | 常在线时间 |
| `phone_public` | TINYINT | 是否公开手机号 |
| `wechat_public` | TINYINT | 是否公开微信号 |
| `role` | VARCHAR(20) | 角色，`USER` 或 `ADMIN` |
| `status` | VARCHAR(20) | 状态，`NORMAL` 或禁用状态 |
| `deleted` | TINYINT | 逻辑删除标记 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

业务说明：

1. 普通用户和管理员共用一张用户表，通过 `role` 区分权限。
2. 用户是否可以登录、发布商品等，由 `status` 控制。
3. 公开资料页会根据 `phone_public` 和 `wechat_public` 决定是否展示手机号和微信号。

### 4.2 商品分类表 category

`category` 保存普通商品分类。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 分类 ID |
| `name` | VARCHAR(50) | 分类名称 |
| `sort_order` | INT | 排序值，越小越靠前 |
| `status` | VARCHAR(20) | 分类状态，默认 `ENABLED` |
| `deleted` | TINYINT | 逻辑删除标记 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

业务说明：

1. 商品发布时必须选择一个有效分类。
2. 停用分类后，不应再允许新商品发布到该分类。

### 4.3 商品表 goods

`goods` 是交易业务的核心主表。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 商品 ID |
| `seller_id` | BIGINT | 卖家用户 ID，关联 `sys_user.id` |
| `category_id` | BIGINT | 分类 ID，关联 `category.id` |
| `title` | VARCHAR(100) | 商品标题 |
| `price` | DECIMAL(10,2) | 商品价格 |
| `description` | VARCHAR(500) | 商品描述 |
| `cover_image` | VARCHAR(255) | 封面图地址 |
| `status` | VARCHAR(20) | 商品状态 |
| `stock` | INT | 库存数量 |
| `deleted` | TINYINT | 逻辑删除标记 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

商品状态建议含义：

| 状态 | 含义 |
| --- | --- |
| `PENDING` | 待审核 |
| `ON_SALE` | 在售 |
| `OFF_SHELF` | 已下架 |
| `SOLD` | 已售出 |
| `REJECTED` | 审核拒绝 |

业务说明：

1. 用户发布商品后，商品默认进入 `PENDING`。
2. 管理员审核通过后，商品变为 `ON_SALE`，可在首页和商品列表展示。
3. 用户编辑商品后，商品会重新进入 `PENDING`。
4. 订单提交时会扣减 `stock`，库存为 0 时商品变为 `SOLD`。

### 4.4 商品图片表 goods_image

`goods_image` 用于保存商品多图。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 图片 ID |
| `goods_id` | BIGINT | 商品 ID，关联 `goods.id` |
| `image_url` | VARCHAR(255) | 图片地址 |
| `sort_order` | INT | 图片排序 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

业务说明：

1. 商品封面图保存在 `goods.cover_image`。
2. 商品详情页轮播或多图展示使用 `goods_image`。

### 4.5 商品审核记录表 goods_audit_log

`goods_audit_log` 保存管理员审核商品的历史记录。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 审核记录 ID |
| `goods_id` | BIGINT | 商品 ID，关联 `goods.id` |
| `admin_id` | BIGINT | 管理员 ID，关联 `sys_user.id` |
| `audit_status` | VARCHAR(20) | 审核结果 |
| `audit_remark` | VARCHAR(255) | 审核备注 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

业务说明：

1. 每次管理员审核商品都会产生一条记录。
2. 商品表保存当前状态，审核记录表保存历史轨迹。

### 4.6 特色分类表 featured_category

`featured_category` 保存首页特色专区配置。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 特色分类 ID |
| `name` | VARCHAR(50) | 特色分类名称 |
| `status` | VARCHAR(20) | 状态，默认 `ENABLED` |
| `sort_order` | INT | 排序值 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

业务说明：

1. 管理员可维护特色分类名称、状态和排序。
2. 首页只展示启用状态的特色分类。

### 4.7 商品特色分类关联表 goods_featured_category

`goods_featured_category` 维护商品和特色分类的多对多关系。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 关联 ID |
| `goods_id` | BIGINT | 商品 ID，关联 `goods.id` |
| `featured_category_id` | BIGINT | 特色分类 ID，关联 `featured_category.id` |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

约束说明：

| 约束 | 说明 |
| --- | --- |
| `uk_goods_featured` | 保证同一商品不能重复加入同一特色分类 |

业务说明：

1. 一个商品可以进入多个特色专区。
2. 一个特色专区可以包含多个商品。

### 4.8 购物车表 cart_item

`cart_item` 保存用户准备购买的商品。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 购物车项 ID |
| `user_id` | BIGINT | 用户 ID，关联 `sys_user.id` |
| `goods_id` | BIGINT | 商品 ID，关联 `goods.id` |
| `quantity` | INT | 购买数量 |
| `deleted` | TINYINT | 逻辑删除标记 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

约束说明：

| 约束 | 说明 |
| --- | --- |
| `uk_user_goods` | 同一用户对同一商品只能有一条购物车记录 |

业务说明：

1. 用户不能购买自己发布的商品。
2. 订单提交后，对应购物车项会被删除。
3. 当前业务限制一次订单只支持同一卖家的商品。

### 4.9 订单表 orders

`orders` 保存订单主信息。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 订单 ID |
| `order_no` | VARCHAR(32) | 订单编号，唯一 |
| `buyer_id` | BIGINT | 买家用户 ID，关联 `sys_user.id` |
| `seller_id` | BIGINT | 卖家用户 ID，关联 `sys_user.id` |
| `total_amount` | DECIMAL(10,2) | 订单总金额 |
| `status` | VARCHAR(20) | 订单状态 |
| `remark` | VARCHAR(255) | 买家备注 |
| `deleted` | TINYINT | 逻辑删除标记 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

订单状态建议含义：

| 状态 | 含义 |
| --- | --- |
| `CREATED` | 已创建，待卖家发货 |
| `SHIPPED` | 已发货，待买家确认收货 |
| `FINISHED` | 已完成 |
| `CANCELED` | 已取消 |

业务说明：

1. 买家提交订单后生成 `CREATED` 状态订单。
2. 卖家发货后变为 `SHIPPED`。
3. 买家确认收货后变为 `FINISHED`。
4. 买家取消订单后变为 `CANCELED`，库存会回补。

### 4.10 订单明细表 order_item

`order_item` 保存订单中的商品快照。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 明细 ID |
| `order_id` | BIGINT | 订单 ID，关联 `orders.id` |
| `goods_id` | BIGINT | 商品 ID，关联 `goods.id` |
| `goods_title` | VARCHAR(100) | 下单时的商品标题快照 |
| `goods_price` | DECIMAL(10,2) | 下单时的商品价格快照 |
| `quantity` | INT | 购买数量 |
| `subtotal_amount` | DECIMAL(10,2) | 小计金额 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

业务说明：

1. 明细表保留商品标题和价格快照，避免商品后续修改影响历史订单。
2. 一个订单可以包含多个订单明细。

### 4.11 商品收藏表 favorite_goods

`favorite_goods` 保存用户收藏商品关系。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 收藏 ID |
| `user_id` | BIGINT | 用户 ID，关联 `sys_user.id` |
| `goods_id` | BIGINT | 商品 ID，关联 `goods.id` |
| `deleted` | TINYINT | 逻辑删除标记 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

约束说明：

| 约束 | 说明 |
| --- | --- |
| `uk_favorite_user_goods` | 同一用户不能重复收藏同一商品 |

业务说明：

1. 用户可以收藏和取消收藏商品。
2. 用户不能收藏自己发布的商品。

### 4.12 商品留言表 goods_message

`goods_message` 保存围绕商品的留言咨询和回复。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 留言 ID |
| `goods_id` | BIGINT | 商品 ID，关联 `goods.id` |
| `sender_id` | BIGINT | 留言用户 ID，关联 `sys_user.id` |
| `parent_id` | BIGINT | 父留言 ID，用于回复关系 |
| `content` | VARCHAR(500) | 留言内容 |
| `deleted` | TINYINT | 逻辑删除标记 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

业务说明：

1. `parent_id` 为空表示一级留言。
2. `parent_id` 不为空表示对某条留言的回复。
3. 管理员可以在后台删除违规留言。

### 4.13 用户通知表 user_notification

`user_notification` 保存站内消息。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 通知 ID |
| `user_id` | BIGINT | 接收用户 ID，关联 `sys_user.id` |
| `type` | VARCHAR(30) | 通知类型 |
| `title` | VARCHAR(100) | 通知标题 |
| `content` | VARCHAR(255) | 通知内容 |
| `action_url` | VARCHAR(255) | 跳转地址 |
| `read_flag` | TINYINT | 是否已读，`0` 未读，`1` 已读 |
| `deleted` | TINYINT | 逻辑删除标记 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

业务说明：

1. 商品审核结果、留言提醒、举报处理结果等均可通过通知触达用户。
2. 前端可查询未读数量、标记单条已读、全部已读和删除通知。

### 4.14 商品举报表 goods_report

`goods_report` 保存用户对商品的举报和管理员处理结果。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT | 举报 ID |
| `goods_id` | BIGINT | 被举报商品 ID，关联 `goods.id` |
| `reporter_id` | BIGINT | 举报人 ID，关联 `sys_user.id` |
| `reported_user_id` | BIGINT | 被举报用户 ID，关联 `sys_user.id` |
| `report_type` | VARCHAR(30) | 举报类型 |
| `report_reason` | VARCHAR(100) | 举报原因 |
| `report_description` | VARCHAR(500) | 举报补充描述 |
| `status` | VARCHAR(20) | 举报状态，默认 `PENDING` |
| `handle_result` | VARCHAR(30) | 处理结果 |
| `handle_remark` | VARCHAR(255) | 处理备注 |
| `handler_id` | BIGINT | 处理管理员 ID |
| `handle_time` | DATETIME | 处理时间 |
| `deleted` | TINYINT | 逻辑删除标记 |
| `create_time` | DATETIME | 创建时间 |
| `update_time` | DATETIME | 更新时间 |

举报状态建议含义：

| 状态 | 含义 |
| --- | --- |
| `PENDING` | 待处理 |
| `HANDLED` | 已处理 |

业务说明：

1. 用户可举报违规商品。
2. 管理员处理举报时可记录处理结果、备注、处理人和处理时间。
3. 举报处理结果可以通过站内通知反馈给举报人。

## 5. 表关系说明

### 5.1 用户与商品

| 关系 | 说明 |
| --- | --- |
| `sys_user.id` -> `goods.seller_id` | 一个用户可以发布多个商品，一个商品只属于一个卖家 |
| `category.id` -> `goods.category_id` | 一个分类可以包含多个商品，一个商品只属于一个普通分类 |

### 5.2 商品与扩展信息

| 关系 | 说明 |
| --- | --- |
| `goods.id` -> `goods_image.goods_id` | 一个商品可以有多张图片 |
| `goods.id` -> `goods_audit_log.goods_id` | 一个商品可以有多条审核记录 |
| `sys_user.id` -> `goods_audit_log.admin_id` | 一个管理员可以审核多个商品 |
| `goods.id` -> `goods_message.goods_id` | 一个商品可以有多条留言 |
| `goods_message.id` -> `goods_message.parent_id` | 留言支持父子回复关系 |

### 5.3 商品与特色专区

| 关系 | 说明 |
| --- | --- |
| `goods.id` -> `goods_featured_category.goods_id` | 商品可进入多个特色专区 |
| `featured_category.id` -> `goods_featured_category.featured_category_id` | 特色专区可包含多个商品 |

商品与特色专区是多对多关系，通过 `goods_featured_category` 中间表维护。

### 5.4 用户、购物车与收藏

| 关系 | 说明 |
| --- | --- |
| `sys_user.id` -> `cart_item.user_id` | 一个用户可以有多个购物车项 |
| `goods.id` -> `cart_item.goods_id` | 一个购物车项对应一个商品 |
| `sys_user.id` -> `favorite_goods.user_id` | 一个用户可以收藏多个商品 |
| `goods.id` -> `favorite_goods.goods_id` | 一个商品可以被多个用户收藏 |

购物车和收藏都通过唯一约束避免同一用户重复关联同一商品。

### 5.5 用户、订单与订单明细

| 关系 | 说明 |
| --- | --- |
| `sys_user.id` -> `orders.buyer_id` | 一个用户可以作为买家产生多个订单 |
| `sys_user.id` -> `orders.seller_id` | 一个用户可以作为卖家收到多个订单 |
| `orders.id` -> `order_item.order_id` | 一个订单可以包含多个订单明细 |
| `goods.id` -> `order_item.goods_id` | 一个订单明细对应一个商品 |

订单表记录交易双方和总金额，订单明细表记录商品快照。这样即使商品后续被编辑，历史订单仍能保持下单时的标题、价格和数量。

### 5.6 用户、通知与举报

| 关系 | 说明 |
| --- | --- |
| `sys_user.id` -> `user_notification.user_id` | 一个用户可以收到多条通知 |
| `goods.id` -> `goods_report.goods_id` | 一个商品可以被多次举报 |
| `sys_user.id` -> `goods_report.reporter_id` | 一个用户可以提交多条举报 |
| `sys_user.id` -> `goods_report.reported_user_id` | 一个用户可以作为被举报对象 |
| `sys_user.id` -> `goods_report.handler_id` | 一个管理员可以处理多条举报 |

## 6. 业务关系与数据流

### 6.1 用户注册与登录

1. 用户注册时写入 `sys_user`。
2. 密码使用 BCrypt 加密后保存到 `sys_user.password`。
3. 登录成功后，后端根据 `sys_user.id`、`username`、`role` 等生成 JWT。
4. 前端后续请求携带 JWT，后端通过用户上下文识别当前用户。

### 6.2 商品发布与审核

1. 用户发布商品时写入 `goods`，状态为 `PENDING`。
2. 商品图片写入 `goods_image`。
3. 管理员审核商品时更新 `goods.status`。
4. 每次审核都写入 `goods_audit_log`。
5. 审核通过的商品进入 `ON_SALE`，可在公开商品列表展示。

### 6.3 首页展示与特色专区

1. 首页普通分类来自 `category`。
2. 首页在售商品来自 `goods` 中状态为 `ON_SALE` 的记录。
3. 首页特色专区来自 `featured_category`。
4. 特色专区中的商品通过 `goods_featured_category` 关联查询。

### 6.4 购物车与订单

1. 用户将商品加入购物车时写入 `cart_item`。
2. 提交订单时读取当前用户的购物车项。
3. 后端校验商品是否在售、是否同一卖家、库存是否充足。
4. 创建 `orders` 订单主记录。
5. 创建 `order_item` 订单明细快照。
6. 扣减 `goods.stock`，库存为 0 时设置 `goods.status` 为 `SOLD`。
7. 删除已提交的 `cart_item`。

### 6.5 订单状态流转

订单状态流转如下：

```text
CREATED -> SHIPPED -> FINISHED
   |
   v
CANCELED
```

业务规则：

1. 买家提交订单后状态为 `CREATED`。
2. 买家只能取消 `CREATED` 状态的订单。
3. 取消订单后库存回补。
4. 卖家只能对 `CREATED` 状态订单发货。
5. 买家只能对 `SHIPPED` 状态订单确认收货。

### 6.6 留言、通知与监管

1. 用户在商品详情页留言，写入 `goods_message`。
2. 回复留言时写入 `parent_id`，形成父子关系。
3. 商品卖家或相关用户可以收到 `user_notification`。
4. 管理员可在后台查看并删除违规留言。

### 6.7 举报处理

1. 用户举报商品时写入 `goods_report`，状态为 `PENDING`。
2. 管理员处理举报时更新 `status`、`handle_result`、`handle_remark`、`handler_id` 和 `handle_time`。
3. 根据处理结果，可对商品或用户进行进一步治理。
4. 处理结果可通过 `user_notification` 通知举报人。

## 7. ER 关系简图

```text
sys_user 1 ---- N goods
sys_user 1 ---- N orders (buyer_id)
sys_user 1 ---- N orders (seller_id)
sys_user 1 ---- N cart_item
sys_user 1 ---- N favorite_goods
sys_user 1 ---- N goods_message
sys_user 1 ---- N user_notification
sys_user 1 ---- N goods_report (reporter_id / reported_user_id / handler_id)

category 1 ---- N goods

goods 1 ---- N goods_image
goods 1 ---- N goods_audit_log
goods 1 ---- N order_item
goods 1 ---- N cart_item
goods 1 ---- N favorite_goods
goods 1 ---- N goods_message
goods 1 ---- N goods_report

orders 1 ---- N order_item

goods N ---- N featured_category
       via goods_featured_category

goods_message 1 ---- N goods_message (parent_id)
```

## 8. 初始化数据说明

`data.sql` 默认插入以下基础数据：

1. 管理员账号 `admin`。
2. 普通用户账号 `student1`。
3. 商品分类，如教材书籍、数码电子、生活用品、学习办公等。
4. 特色分类，如毕业甩卖、新生必备、考研专区、宿舍神器等。
5. 示例商品数据，如高数教材、二手耳机等。

默认账号的详细登录方式见：

1. `docs/LOGIN-METHODS.md`

## 9. 后续优化建议

1. 订单库存扣减可增加乐观锁或条件更新，降低并发下超卖风险。
2. 商品列表、订单列表可优化为批量查询或关联查询，减少 N+1 查询。
3. 文件上传可增加 MIME 校验、图片内容校验和安全重命名策略。
4. 生产环境中数据库密码和 JWT 密钥应改为环境变量或外部配置。
5. 可为高频查询字段增加索引，如 `goods.status`、`goods.category_id`、`orders.buyer_id`、`orders.seller_id`、`user_notification.user_id`。