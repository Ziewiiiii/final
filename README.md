# 余卓炜  202330452181 

# Web 项目说明文档

## 一、项目概览

本项目为课程设计的前后端分离 Web 应用，整体结构如下：

- `frontend/`：基于 Vue 3 + Vite 的前端单页应用（SPA），负责页面展示和交互。
- `backend/`：基于 Spring Boot 3 的后端服务，提供业务接口、数据持久化、远程 MySQL 访问、本地 H2 缓存以及邮件发送等能力。
- 其他：日志文件、数据库文件（H2 缓存）、配置文件、开发文档等。

前后端通过 HTTP 接口进行通信，后端默认运行在 `http://localhost:8080`，前端通过配置好的接口地址访问后端和上游 API。

---

## 二、技术栈说明

### 1. 前端（frontend）

前端主要技术：

- **框架**：Vue 3
- **构建工具**：Vite
- **路由**：Vue Router 4
- **状态管理**：Pinia + `pinia-plugin-persistedstate`（状态持久化）
- **UI 组件库**：Element Plus
- **请求库**：Axios
- **工具库**：
  - `@vueuse/core`：常用 Vue 组合式工具函数
  - `dayjs`：日期时间处理
- **样式**：Sass（SCSS）
- **开发辅助**：
  - `@vitejs/plugin-vue`
  - `unplugin-auto-import`
  - `unplugin-vue-components`
  - `vite-plugin-vue-devtools`

特点：

- 使用组合式 API 构建组件，结构清晰、易于扩展。
- 使用 Element Plus 统一 UI 风格，提升界面一致性和开发效率。
- 使用 Pinia 统一管理全局状态（如用户信息、业务数据等），并支持持久化存储。
- 使用 Axios 封装接口请求，便于统一处理请求拦截、错误处理等逻辑。

### 2. 后端（backend）

后端主要技术：

- **运行框架**：Spring Boot 3.5.6
- **语言与版本**：Java 17
- **Web 框架**：
  - `spring-boot-starter-web`（传统 MVC 风格 REST 接口）
  - `spring-boot-starter-webflux`（作为 HTTP 客户端访问外部接口）
- **数据访问**：
  - `spring-boot-starter-data-jpa`（基于 JPA 的数据持久化）
  - **主数据库：远程 MySQL**（存放核心业务数据）
  - **本地 H2 数据库**：作为缓存/本地快速存储，减少对远程 MySQL 的直接压力
- **序列化**：`jackson-databind`（JSON 序列化与反序列化）
- **工具**：Lombok（简化实体类、DTO 等样板代码）
- **测试**：`spring-boot-starter-test`
- **邮件发送**：`spring-boot-starter-mail`（用于业务相关的邮件通知）

配置要点（见 `backend/src/main/resources/application.properties` 及相关配置）：

- 应用名：`spring.application.name=myweb2`
- 服务端口：`server.port=8080`
- **数据库**：
  - **远程 MySQL**：作为主数据源，存放核心业务表。
  - **H2 文件数据库**：`jdbc:h2:file:./data/myweb2;MODE=MYSQL;...`，用于本地缓存或辅助存储，方便开发测试和提升部分查询性能。
  - JPA 自动建表与更新：`spring.jpa.hibernate.ddl-auto=update`
  - H2 控制台：`/h2-console`（启用，用于本地调试查看缓存数据）。
- **密码映射与加密配置**：
  - `password.mapping.encryption-key`、`password.mapping.algorithm` 等，用于对敏感信息做加密映射。
- **邮件配置**：
  - 使用 QQ 邮箱 SMTP 作为发件邮箱（具体账号和授权码配置在 `spring.mail.*` 中）。

---

## 三、功能模块

根据当前技术栈和配置，系统通常会包含以下类型的功能模块（可根据你的实际业务补充/修改）：

- **用户与认证相关模块**
  - 用户信息的管理与展示。
  - 密码加密映射、账号安全相关功能。
- **业务数据模块**
  - 通过后端访问 **远程 MySQL** 进行核心业务数据的增删改查。
  - 部分热点或频繁访问的数据会同步/缓存到本地 **H2 数据库** 中，提升查询效率或做离线调试。
  - 部分数据由后端转发或聚合外部接口 `pcapi-xiaotuxian-front-devtest.itheima.net` 返回的结果。
- **通知与邮件模块**
  - 通过 Spring Mail 发送业务相关邮件（如操作通知、状态变更提醒等）。

你可以根据课程要求，在此处具体写清：

- 每个模块对应的前端页面路径（例如：`/login`、`/orders`、`/settings` 等）。
- 主要接口路径（如：`/api/users`、`/api/orders` 等）。
- 每个接口的请求方式、请求参数及返回结果格式。

---

## 四、项目运行说明

### 1. 环境准备

- 已安装 **Node.js**（建议与 `frontend/package.json` 中 `engines` 要求兼容，如 Node 20+）。
- 已安装 **npm** 或 **yarn**。
- 已安装 **JDK 17**（与 Spring Boot 配置一致）。
- 已安装 **Maven**（如果使用项目自带 `mvnw`/`mvnw.cmd`，可不单独安装 Maven，但需要命令行可执行）。
- 已准备好 **远程 MySQL 数据库** 环境，并在配置文件中正确填写连接信息（URL、用户名、密码等）。

### 2. 启动后端（backend）

进入 `backend` 目录，执行以下命令之一：

```bash
# 使用项目自带 Maven Wrapper（推荐）
./mvnw spring-boot:run   # Linux / MacOS
mvnw.cmd spring-boot:run # Windows
```

或使用本地 Maven：

```bash
mvn spring-boot:run
```

启动成功后：

- 后端接口默认地址：`http://localhost:8080`
- H2 数据库控制台（缓存数据查看）：`http://localhost:8080/h2-console`

> 注意：确保 MySQL 数据库连通、账号密码正确，否则应用在启动或访问某些接口时可能会报错。

### 3. 启动前端（frontend）

进入 `frontend` 目录，安装依赖并启动开发服务器：

```bash
npm install
npm run dev
```

或使用 yarn：

```bash
yarn
yarn dev
```

启动成功后，根据 Vite 输出信息，在浏览器访问类似：

- `http://localhost:5173`（默认端口，具体以终端输出为准）。

> 注意：
> - 确保前端中配置的后端接口地址（如 `.env` 或配置文件中的 `VITE_API_BASE_URL` 等）指向正在运行的后端 `http://localhost:8080`。
> - 如果有使用代理（Vite dev server proxy）转发接口，也需要确保代理配置正确。

### 4. 前后端联调流程

1. 启动远程 MySQL 服务，并确认连接信息正确。
2. 启动后端，确认端口 `8080` 正常监听，并能访问 `/h2-console` 或某个测试接口。  
3. 启动前端，打开浏览器访问前端地址。  
4. 在前端页面执行登录、数据查询等操作，观察浏览器控制台和后端日志（`backend.log`），确认请求链路以及对 MySQL/H2 的读写是否正常。  

---

## 五、构建与部署

### 1. 前端构建

在 `frontend` 目录下执行：

```bash
npm run build
```

构建完成后会生成 `dist/` 目录，里面是打包好的静态文件，可以：

- 部署到 Nginx 等静态服务器。
- 或部署到 Vercel、Netlify 等前端托管平台。

### 2. 后端打包与部署

在 `backend` 目录下执行：

```bash
mvnw.cmd clean package  # Windows
# 或
mvn clean package       # 使用本地 Maven
```

打包成功后会在 `backend/target/` 目录下生成可执行的 `jar` 文件，例如：

```bash
java -jar target/myweb2-0.0.1-SNAPSHOT.jar
```

可将该 `jar` 部署到：

- 学校/实验室提供的服务器。  
- 个人云服务器（配合 `screen`、`nohup` 或 `systemd` 等保持后台运行）。

部署时需确保：

- 服务器可以访问远程 MySQL 数据库。
- 本地 `./data/` 目录有读写权限，以便 H2 缓存正常工作。

---

  




