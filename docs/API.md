# 校友有点闲 API 接口说明

## 1. 基本约定

后端默认地址：

```text
http://localhost:8081
```

前端开发环境通过 Vite 代理访问后端：

```text
/api -> http://localhost:8081/api
/uploads -> http://localhost:8081/uploads
```

除登录、注册和部分公开查询接口外，其余接口需要在请求头中携带 JWT：

```http
Authorization: Bearer <token>
```

统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

分页响应中的 `data` 格式：

```json
{
  "total": 100,
  "records": []
}
```

常见状态值：

| 类型 | 状态 | 含义 |
| --- | --- | --- |
| 用户角色 | `USER` | 普通用户 |
| 用户角色 | `ADMIN` | 管理员 |
| 用户状态 | `NORMAL` | 正常 |
| 商品状态 | `PENDING` | 待审核 |
| 商品状态 | `ON_SALE` | 在售 |
| 商品状态 | `OFF_SHELF` | 已下架 |
| 商品状态 | `SOLD` | 已售出 |
| 商品状态 | `REJECTED` | 审核拒绝 |
| 订单状态 | `CREATED` | 已创建，待发货 |
| 订单状态 | `SHIPPED` | 已发货，待收货 |
| 订单状态 | `FINISHED` | 已完成 |
| 订单状态 | `CANCELED` | 已取消 |
| 举报状态 | `PENDING` | 待处理 |
| 举报状态 | `RESOLVED` | 已处理，且处理结果生效 |
| 举报状态 | `REJECTED` | 举报驳回 |

常见错误响应：

```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": null
}
```

```json
{
  "code": 500,
  "message": "未登录或登录已过期",
  "data": null
}
```

```json
{
  "code": 500,
  "message": "当前用户无管理员权限",
  "data": null
}
```

说明：

1. 参数校验失败通常返回 `400`。
2. 业务异常通常返回 `500`，错误信息直接来自后端业务判断。
3. 文件超限时返回 `400`，提示单文件最大 10MB、单请求最大 20MB。

## 1.1 匿名访问矩阵

以下接口允许未登录访问：

| 接口 | 说明 |
| --- | --- |
| `POST /api/auth/register` | 注册 |
| `POST /api/auth/login` | 登录 |
| `GET /api/categories` | 分类列表 |
| `GET /api/goods/page` | 商品分页 |
| `GET /api/goods/featured/sections` | 首页特色专区 |
| `GET /api/goods/{goodsId}` | 商品详情 |
| `GET /api/goods/{goodsId}/messages` | 商品留言列表 |
| `GET /api/user/public/{userId}` | 公开资料 |

其余接口均要求登录。

## 2. 公开接口

### 2.1 用户注册

```http
POST /api/auth/register
```

请求体：

```json
{
  "username": "student2",
  "password": "your_demo_password",
  "nickname": "学生用户2",
  "phone": "13900000001"
}
```

字段说明：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `username` | 是 | 用户名 |
| `password` | 是 | 密码 |
| `nickname` | 是 | 昵称 |
| `phone` | 否 | 手机号 |

### 2.2 用户登录

```http
POST /api/auth/login
```

请求体：

```json
{
  "username": "admin",
  "password": "your_demo_password"
}
```

返回 `data` 示例：

```json
{
  "userId": 1,
  "username": "admin",
  "nickname": "管理员",
  "avatar": "/uploads/admin.png",
  "role": "ADMIN",
  "token": "eyJhbGciOi..."
}
```

### 2.3 商品分类列表

```http
GET /api/categories
```

说明：返回启用状态的商品分类列表。

### 2.4 商品分页列表

```http
GET /api/goods/page?pageNum=1&pageSize=10&keyword=耳机&categoryId=2
```

查询参数：

| 参数 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | 否 | `1` | 页码 |
| `pageSize` | 否 | `10` | 每页数量 |
| `keyword` | 否 | 无 | 商品标题关键字 |
| `categoryId` | 否 | 无 | 分类 ID |

说明：只返回 `ON_SALE` 状态商品。

### 2.5 首页特色专区

```http
GET /api/goods/featured/sections
```

说明：返回启用状态的特色分类，以及每个特色分类下的商品列表。

### 2.6 商品详情

```http
GET /api/goods/{goodsId}
```

说明：公开可查看在售商品。若携带登录 token，返回结果会包含当前用户是否收藏等上下文信息。商品卖家和管理员可以查看非在售商品。

返回 `data` 示例：

```json
{
  "id": 1,
  "categoryId": 1,
  "categoryName": "教材书籍",
  "sellerId": 2,
  "sellerName": "学生用户",
  "title": "高数教材",
  "price": 25.00,
  "description": "九成新，适合大一学生",
  "coverImage": "/uploads/book.png",
  "status": "ON_SALE",
  "stock": 1,
  "latestAuditRemark": "审核通过",
  "favorited": false,
  "images": [],
  "messages": [],
  "auditLogs": [],
  "featuredCategories": []
}
```

### 2.7 商品留言列表

```http
GET /api/goods/{goodsId}/messages
```

说明：返回商品留言和回复树。未登录用户可查看，登录用户会带入当前用户上下文。

### 2.8 公开用户资料

```http
GET /api/user/public/{userId}
```

说明：返回用户公开资料、交易偏好和信用统计。手机号、微信号是否展示受用户隐私开关控制。

## 3. 用户与个人资料

### 3.1 当前用户资料

```http
GET /api/user/profile
```

权限：登录用户。

返回 `data` 结构与公开资料页一致，通常包含昵称、头像、联系方式、隐私设置、交易偏好、信用信息等字段。

### 3.2 修改个人资料

```http
PUT /api/user/profile
```

权限：登录用户。

请求体：

```json
{
  "nickname": "新的昵称",
  "phone": "13900000000",
  "gender": "MALE",
  "bio": "热爱校园交易",
  "school": "某某大学",
  "college": "计算机学院",
  "major": "软件工程",
  "grade": "2023级",
  "wechat": "student_2023",
  "qq": "123456789",
  "email": "student@example.com",
  "tradeLocation": "图书馆门口",
  "tradeMethods": "当面交易",
  "acceptBargain": true,
  "onlineTime": "晚上 8 点后",
  "phonePublic": false,
  "wechatPublic": true
}
```

校验规则：

| 字段 | 规则 |
| --- | --- |
| `nickname` | 必填，最长 50 字符 |
| `phone` | 空字符串或 11 位大陆手机号 |
| `gender` | 空字符串、`MALE`、`FEMALE`、`OTHER` |
| `bio` | 最长 500 字符 |
| `school` / `college` / `major` | 最长 100 字符 |
| `grade` / `wechat` / `onlineTime` | 最长 50 或 100 字符，按后端 DTO 校验 |
| `qq` | 空字符串或 5 到 12 位数字 |
| `email` | 邮箱格式，最长 100 字符 |

### 3.3 修改密码

```http
PUT /api/user/password
```

请求体：

```json
{
  "oldPassword": "your_current_password",
  "newPassword": "your_latest_password"
}
```

### 3.4 上传头像

```http
POST /api/files/avatar
Content-Type: multipart/form-data
```

表单字段：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `file` | 是 | 图片文件，支持 `jpg`、`jpeg`、`png`、`webp` |

返回 `data` 示例：

```json
{
  "avatar": "/uploads/xxx.png"
}
```

## 4. 商品接口

### 4.1 上传商品图片

```http
POST /api/files/upload
Content-Type: multipart/form-data
```

表单字段：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `file` | 是 | 图片文件，支持 `jpg`、`jpeg`、`png`、`webp` |

返回 `data` 示例：

```json
"/uploads/xxx.png"
```

### 4.2 发布商品

```http
POST /api/goods
```

权限：登录用户。

请求体：

```json
{
  "categoryId": 1,
  "title": "高数教材",
  "price": 25.00,
  "description": "九成新，适合大一学生",
  "coverImage": "/uploads/book.png",
  "imageUrls": ["/uploads/book.png"],
  "stock": 1
}
```

说明：发布后商品进入 `PENDING`，等待管理员审核。

### 4.3 我的商品

```http
GET /api/goods/my
```

权限：登录用户。

### 4.4 编辑我的商品

```http
PUT /api/goods/{goodsId}
```

权限：商品卖家。

请求体同发布商品。编辑成功后商品重新进入 `PENDING`。

### 4.5 上架或下架我的商品

```http
PUT /api/goods/{goodsId}/status
```

权限：商品卖家。

请求体：

```json
{
  "status": "OFF_SHELF"
}
```

说明：用户侧仅支持 `OFF_SHELF` 和 `ON_SALE`。重新上架要求商品当前为 `OFF_SHELF`。

### 4.6 我的收藏

```http
GET /api/goods/favorites
```

权限：登录用户。

### 4.7 收藏商品

```http
POST /api/goods/{goodsId}/favorite
```

权限：登录用户。

说明：不能收藏自己发布的商品。

### 4.8 取消收藏商品

```http
DELETE /api/goods/{goodsId}/favorite
```

权限：登录用户。

## 5. 购物车与订单

### 5.1 加入购物车

```http
POST /api/cart/add
```

请求体：

```json
{
  "goodsId": 1,
  "quantity": 1
}
```

校验规则：`goodsId` 必填，`quantity` 必填且至少为 1。

### 5.2 购物车列表

```http
GET /api/cart/list
```

权限：登录用户。

### 5.3 删除购物车项

```http
DELETE /api/cart/{cartItemId}
```

权限：登录用户。

### 5.4 提交订单

```http
POST /api/orders/submit
```

请求体可为空，也可带备注：

```json
{
  "remark": "晚饭后交易"
}
```

业务规则：

1. 购物车不能为空。
2. 不允许购买自己发布的商品。
3. 一次订单只支持同一卖家的商品。
4. 商品必须为 `ON_SALE` 且库存充足。
5. 下单后扣减库存，库存为 0 时商品变为 `SOLD`。

### 5.5 我的买家订单

```http
GET /api/orders/my
```

返回 `data` 示例：

```json
[
  {
    "id": 1,
    "orderNo": "ORD202605111230002",
    "buyerId": 2,
    "buyerName": "买家A",
    "sellerId": 3,
    "sellerName": "卖家B",
    "totalAmount": 25.00,
    "status": "CREATED",
    "remark": "晚饭后交易",
    "createTime": "2026-05-11 12:30:00",
    "items": [
      {
        "goodsId": 1,
        "goodsTitle": "高数教材",
        "goodsPrice": 25.00,
        "quantity": 1,
        "subtotalAmount": 25.00
      }
    ]
  }
]
```

### 5.6 我的卖家订单

```http
GET /api/orders/my/sell
```

### 5.7 买家订单详情

```http
GET /api/orders/{orderId}
```

### 5.8 卖家订单详情

```http
GET /api/orders/sell/{orderId}
```

### 5.9 取消订单

```http
PUT /api/orders/{orderId}/cancel
```

权限：买家。仅 `CREATED` 订单可取消，取消后库存回补。

### 5.10 卖家发货

```http
PUT /api/orders/{orderId}/ship
```

权限：卖家。仅 `CREATED` 订单可发货。

### 5.11 买家确认收货

```http
PUT /api/orders/{orderId}/finish
```

权限：买家。仅 `SHIPPED` 订单可确认收货。

## 6. 留言、通知与举报

### 6.1 发布留言或回复

```http
POST /api/goods/{goodsId}/messages
```

请求体：

```json
{
  "parentId": null,
  "content": "请问还能便宜一点吗？"
}
```

字段说明：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `parentId` | 否 | 父留言 ID，为空表示顶层留言 |
| `content` | 是 | 留言内容 |

### 6.2 通知列表

```http
GET /api/user/notifications
```

权限：登录用户。

返回 `data` 示例：

```json
[
  {
    "id": 1,
    "type": "GOODS_REPORT_RESULT",
    "title": "举报处理完成",
    "content": "商品《高数教材》的举报已处理，结果：OFF_SHELF_GOODS",
    "actionUrl": "/report-detail/1",
    "readFlag": false,
    "createTime": "2026-05-11 13:00:00"
  }
]
```

### 6.3 未读通知数量

```http
GET /api/user/notifications/unread-count
```

### 6.4 标记单条通知已读

```http
PUT /api/user/notifications/{notificationId}/read
```

### 6.5 全部通知已读

```http
PUT /api/user/notifications/read-all
```

### 6.6 删除单条通知

```http
DELETE /api/user/notifications/{notificationId}
```

### 6.7 删除全部通知

```http
DELETE /api/user/notifications
```

### 6.8 举报商品

```http
POST /api/goods/{goodsId}/report
```

请求体：

```json
{
  "reportType": "FAKE_GOODS",
  "reportReason": "商品描述与实际不符",
  "reportDescription": "图片和说明存在明显夸大"
}
```

### 6.9 举报详情

```http
GET /api/goods-reports/{reportId}
```

权限：登录用户。用于从消息中心查看举报处理结果详情。

说明：当前实现只允许举报人或被举报用户查看，且对举报人返回匿名化详情，不展示举报人身份字段。

返回 `data` 示例：

```json
{
  "id": 1,
  "goodsId": 1,
  "goodsTitle": "高数教材",
  "reporterId": null,
  "reporterName": null,
  "reportedUserId": 3,
  "reportedUserName": "卖家B",
  "reportType": "FAKE_GOODS",
  "reportReason": "商品描述与实际不符",
  "reportDescription": "图片和说明存在明显夸大",
  "status": "RESOLVED",
  "handleResult": "OFF_SHELF_GOODS",
  "handleRemark": "举报属实，已下架商品",
  "handlerId": 1,
  "handlerName": "管理员",
  "handleTime": "2026-05-11 13:10:00",
  "createTime": "2026-05-11 12:50:00"
}
```

## 7. 管理员接口

所有管理员接口都需要登录且当前用户角色为 `ADMIN`。

如果权限不足，后端返回：

```json
{
  "code": 500,
  "message": "当前用户无管理员权限",
  "data": null
}
```

### 7.1 后台概览统计

```http
GET /api/admin/statistics/overview
```

返回用户总数、商品总数、订单总数、待审核商品数量等。

### 7.2 近 7 天趋势

```http
GET /api/admin/statistics/trend
```

返回商品新增趋势和订单新增趋势。

### 7.3 分类分布统计

```http
GET /api/admin/statistics/category
```

返回各商品分类下的商品数量分布。

### 7.4 管理员用户分页

```http
GET /api/admin/users/page?pageNum=1&pageSize=10&username=student&role=USER&status=NORMAL
```

查询参数：

| 参数 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | 否 | `1` | 页码 |
| `pageSize` | 否 | `10` | 每页数量 |
| `username` | 否 | 无 | 用户名筛选 |
| `role` | 否 | 无 | 角色筛选 |
| `status` | 否 | 无 | 状态筛选 |

### 7.5 更新用户状态

```http
PUT /api/admin/users/{userId}/status
```

请求体：

```json
{
  "status": "DISABLED"
}
```

说明：常见值包括 `NORMAL`、`DISABLED`，具体以用户状态业务约束为准。

### 7.6 管理员商品分页

```http
GET /api/goods/admin/page?pageNum=1&pageSize=10&keyword=教材&categoryId=1&status=PENDING
```

查询参数：

| 参数 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | 否 | `1` | 页码 |
| `pageSize` | 否 | `10` | 每页数量 |
| `keyword` | 否 | 无 | 商品标题关键字 |
| `categoryId` | 否 | 无 | 分类 ID |
| `status` | 否 | 无 | 商品状态 |

### 7.7 审核商品

```http
PUT /api/goods/admin/{goodsId}/audit
```

请求体：

```json
{
  "approved": true,
  "auditRemark": "内容真实，审核通过",
  "featuredCategoryIds": [1, 2]
}
```

字段说明：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `approved` | 是 | 是否通过审核 |
| `auditRemark` | 是 | 审核备注 |
| `featuredCategoryIds` | 否 | 审核通过时分配的特色分类 ID 列表 |

### 7.8 管理员编辑商品

```http
PUT /api/goods/admin/{goodsId}
```

请求体同发布商品。

### 7.9 管理员更新商品状态

```http
PUT /api/goods/admin/{goodsId}/status
```

请求体：

```json
{
  "status": "OFF_SHELF"
}
```

说明：管理员可按后台业务规则操作商品状态，具体允许值以服务层校验为准。

### 7.10 管理员删除商品

```http
DELETE /api/goods/admin/{goodsId}
```

说明：会清理商品图片、审核记录、特色分类关联、购物车和收藏等相关记录。

### 7.11 分配商品特色分类

```http
PUT /api/goods/admin/{goodsId}/featured-categories
```

请求体：

```json
[1, 2, 3]
```

### 7.12 特色分类分页

```http
GET /api/admin/featured-categories/page?pageNum=1&pageSize=10&name=毕业&status=ENABLED
```

### 7.13 创建特色分类

```http
POST /api/admin/featured-categories
```

请求体：

```json
{
  "name": "毕业甩卖",
  "status": "ENABLED",
  "sortOrder": 1
}
```

### 7.14 更新特色分类

```http
PUT /api/admin/featured-categories/{id}
```

请求体同创建特色分类。

### 7.15 管理员留言分页

```http
GET /api/admin/goods-messages/page?pageNum=1&pageSize=10&keyword=价格&senderUsername=student1
```

查询参数：

| 参数 | 必填 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `pageNum` | 否 | `1` | 页码 |
| `pageSize` | 否 | `10` | 每页数量 |
| `keyword` | 否 | 无 | 留言内容关键字 |
| `senderUsername` | 否 | 无 | 发送者用户名 |

### 7.16 删除留言

```http
DELETE /api/admin/goods-messages/{messageId}
```

### 7.17 删除全部留言

```http
DELETE /api/admin/goods-messages
```

### 7.18 管理员举报分页

```http
GET /api/admin/goods-reports/page?pageNum=1&pageSize=10&keyword=虚假&status=PENDING
```

### 7.19 处理举报

```http
PUT /api/admin/goods-reports/{reportId}/handle
```

请求体：

```json
{
  "handleResult": "OFF_SHELF_GOODS",
  "handleRemark": "举报属实，已下架商品"
}
```

当前实现支持的 `handleResult` 值：

| 值 | 含义 |
| --- | --- |
| `IGNORE` | 忽略举报，举报状态会变为 `REJECTED` |
| `OFF_SHELF_GOODS` | 下架商品，举报状态会变为 `RESOLVED` |
| `DELETE_GOODS` | 删除商品，举报状态会变为 `RESOLVED` |
| `DISABLE_USER` | 禁用被举报用户，举报状态会变为 `RESOLVED` |

说明：处理结果会联动商品状态或用户状态，并向举报人、被举报卖家发送通知。

## 8. 文件访问

上传后的文件通过 `/uploads/**` 访问。例如：

```text
http://localhost:8081/uploads/xxx.png
```

上传限制：

1. 单个文件最大 10MB。
2. 单次请求最大 20MB。
3. 图片后缀支持 `jpg`、`jpeg`、`png`、`webp`。

超限时错误示例：

```json
{
  "code": 400,
  "message": "上传失败：文件大小超出限制，单个文件最大 10MB，请求总大小最大 20MB",
  "data": null
}
```