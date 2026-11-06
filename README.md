# 次元小窝 · Ciyuan Xiaowo

**在线访问：<https://ciyuanxiaowo.art>**

一个面向二次元爱好者的图片分享社区。用户可以注册登录、上传插画 / 壁纸 / 立绘，通过标签分类浏览，在图片下评论、点赞、收藏，也能用私信互相交流。

前后端分离：后端是 Spring Boot 提供的 REST 接口，前端是 **Vue 3 + Vite** 构建的单页应用（SPA），路由用 vue-router，状态用 Pinia。

> ⚠️ **项目仍在持续完善中**：主体功能（注册登录、上传、浏览、详情、评论、点赞、收藏）已经可用，
> 私信箱、收藏夹、管理后台的部分模块还在开发，接口和页面都可能继续调整。

## 功能

| 模块 | 状态 | 说明 |
| --- | --- | --- |
| 用户 | ✅ | 注册、登录（Sa-Token 认证）、退出、个人资料与头像 |
| 图片 | ✅ | 上传（支持大文件）、瀑布流列表、分页、按标签筛选 |
| 详情 | ✅ | 图片详情页、评论区（发表 / 点赞评论） |
| 互动 | ✅ | 点赞、收藏 |
| 内容 | ✅ | 公告板、热门排行、风景壁纸、人物立绘分区 |
| 私信 | 🚧 开发中 | 会话界面已完成，后端消息接口待接入 |
| 收藏夹 | 🚧 开发中 | 列表页待后端接口 |
| 管理 | 🚧 开发中 | 仪表盘 / 标签排行已接真实数据，用户与内容管理待完成 |

## 技术栈

**后端**

- Java 21 + Spring Boot 4.1.1（内嵌 Tomcat）
- MyBatis-Plus 3.5.17（BOM 统一版本，含分页插件所需的 jsqlparser）
- Sa-Token 1.46.0（登录态、鉴权）
- MySQL 8.0 + HikariCP
- Spring Security Crypto（密码加密）
- Lombok
- Gradle（Wrapper 已内置，无需本机安装）

**前端**

- Vue 3.5（`<script setup>` 组合式 API）+ Vite 5
- vue-router 4（history 模式；`/mail`、`/my`、`/admin` 由全局守卫做登录校验）
- Pinia 2（登录态、上传通知、热门标签）
- 原生 CSS，按视图 `scoped` 隔离，不用 UI 组件库
- 开发环境由 Vite 代理 `/api`、`/uploads` 到 9090，前后端同源，登录 Cookie 不受 `localhost` / `127.0.0.1` 影响

## 目录结构

```
.
├── backend/
│   └── ciyuan/                  # Spring Boot 后端
│       ├── src/main/java/org/example/ljh/ciyuan/
│       │   ├── controller/      # 接口层
│       │   ├── service/         # 业务层
│       │   ├── mapper/          # MyBatis-Plus Mapper
│       │   ├── entity/          # 数据库实体
│       │   ├── dto/ vo/         # 出入参
│       │   ├── config/          # CORS、Sa-Token、上传配置
│       │   └── common/          # 统一返回体等
│       └── src/main/resources/
│           ├── application.yml          # 主配置（激活 dev）
│           ├── application-dev.yml      # 本地开发配置
│           ├── application-prod.yml     # 生产配置（读环境变量）
│           └── application-local.yml    # 本地密码（已 gitignore，不上传）
├── frontend/                    # Vue 3 + Vite 前端
│   ├── index.html               # 入口 HTML
│   ├── vite.config.js           # @ 别名、/api 与 /uploads 代理
│   ├── public/                  # 原样拷贝的静态资源（logo、Jost 字体）
│   └── src/
│       ├── main.js / App.vue    # 入口与布局壳（侧栏 / 顶栏 / 上传弹窗）
│       ├── router/              # 路由表与登录守卫
│       ├── stores/              # Pinia：auth / ui / tags
│       ├── utils/               # api 请求封装、格式化
│       ├── composables/         # useWaterfall 瀑布流布局
│       ├── components/          # 侧栏、顶栏、卡片、瀑布流、上传弹窗、头像裁剪
│       ├── views/               # 首页 / 分区 / 排行 / 公告 / 详情 / 登录 / 私信 / 个人中心 / 后台
│       └── assets/styles/       # 各页面样式（scoped 引入）
└── uploads/                     # 图片上传目录（运行时生成，不进仓库）
```

## 本地运行

### 1. 准备环境

- JDK 21（`build.gradle` 里用 toolchain 指定，需本机可被 Gradle 找到）
- MySQL 8.0

### 2. 建库

```sql
CREATE DATABASE ciyuan_xiaowo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

表结构见 [`sql/schema.sql`](sql/schema.sql)，导入即可：

```bash
mysql -u root -p ciyuan_xiaowo < sql/schema.sql
```

### 3. 填本地数据库密码

仓库里不带任何密码。把示例配置复制一份，填上自己的：

```bash
cd backend/ciyuan/src/main/resources
cp application-local.yml.example application-local.yml
```

```yaml
# application-local.yml
spring:
  datasource:
    password: "你的MySQL密码"
```

这个文件已在 `.gitignore` 中，不会被提交。`application-dev.yml` 通过
`spring.config.import: optional:classpath:application-local.yml` 读取它；
也可以用环境变量 `DB_PASSWORD` 覆盖。

### 4. 启动后端

```bash
cd backend/ciyuan
./gradlew bootRun          # Windows: gradlew.bat bootRun
```

启动后接口地址：`http://localhost:9090/api`（`server.servlet.context-path` 为 `/api`）。

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

然后访问 `http://localhost:5173`。

Vite 已把 `/api`、`/uploads` 代理到 `localhost:9090`，页面和接口同源，
登录 Cookie 不会因为 `localhost` / `127.0.0.1` 不一致而丢失。

构建生产包：

```bash
npm run build      # 产物在 frontend/dist/
```

## 接口一览

所有接口以 `/api` 为前缀。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/register` | 注册 |
| POST | `/login` | 登录 |
| GET | `/me` | 当前登录用户 |
| POST | `/me` | 修改个人资料 |
| DELETE | `/me` | 退出登录 |
| GET | `/me/myImg` | 我上传的图片 |
| GET | `/img` | 图片列表（分页） |
| GET | `/img/{id}` | 图片详情 |
| GET | `/img/{id}/comments` | 评论列表 |
| POST | `/img/{id}/comments` | 发表评论 |
| POST | `/img/{id}/like` | 点赞 / 取消点赞 |
| POST | `/img/{id}/favorite` | 收藏 / 取消收藏 |
| POST | `/imgUpload` | 上传图片（multipart） |
| POST | `/upload` | 通用文件上传 |
| GET | `/tag` | 标签列表 |

统一返回体：`{ code, msg, data }`。

## 配置说明

- `application-dev.yml`：本地开发，端口 9090，SQL 日志打印到控制台。
- `application-prod.yml`：生产环境，端口 8080，数据库连接与密码全部走环境变量
  （`DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USER` / `DB_PASSWORD`），Cookie 开启 `secure`、`http-only`。
- 上传大小限制：开发 30MB / 生产 50MB。

## 说明

- `uploads/` 目录是运行时生成的图片存储目录，不入库；部署时需保证该目录可写。
- 生产环境建议在前面挂 Nginx / Caddy 做静态资源代理与 HTTPS（`forward-headers-strategy: framework` 已开启）。
- 前端是 SPA，`/api/*`、`/uploads/*` 之外的路径都要**回退到 `index.html`**
  （Nginx `try_files $uri $uri/ /index.html;`，Caddy `try_files {path} /index.html`），
  否则直接刷新 `/image/123` 这类地址会 404。
- 项目仍在持续迭代，接口与页面结构可能继续调整。
