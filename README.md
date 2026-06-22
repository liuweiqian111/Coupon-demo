# 基于 Spring Boot 的优惠券领取系统

## 项目介绍

面向 Spring Boot 的优惠券领取系统，覆盖 MVC、IOC、事务、AOP、拦截器、MySQL 索引、Redis 缓存/分布式锁、RocketMQ 异步消息等知识点。

## 技术栈

| 技术 | 版本 | 说明 |
|---|---|---|
| Spring Boot | 3.2.12 | 项目基础框架 |
| MyBatis-Plus | 3.5.7 | ORM 框架 |
| MySQL | 8.x | 业务数据存储 |
| Druid | 1.2.23 | 数据库连接池 |
| Redis | 3.2+ | 缓存、分布式锁 |
| RocketMQ | 5.1.4 | 异步消息处理 |
| Thymeleaf | - | 页面模板 |
| Lombok | - | 简化代码 |

## 项目结构

```
coupon-demo
├── docker-compose.yml          # RocketMQ Docker 部署
├── broker.conf                 # RocketMQ Broker 配置
├── coupon_db.sql               # 数据库初始化脚本
├── pom.xml                     # Maven 依赖
├── README.md                   # 项目说明
└── src/main
    ├── java/com/example/coupondemo
    │   ├── config/             # Redis、RocketMQ、拦截器、AOP
    │   ├── controller/         # 登录、活动、领券接口
    │   ├── domain/             # 实体类
    │   ├── mapper/             # MyBatis-Plus Mapper
    │   ├── mq/                 # 生产者、消费者、消息对象
    │   ├── service/            # 业务逻辑
    │   ├── utils/              # MD5、UUID
    │   └── vo/                 # 统一返回对象
    └── resources
        ├── application.yml     # 配置文件
        └── templates/          # 前端页面
```

## 核心功能

### 1. 用户登录
- 手机号 + 密码（MD5 加盐）登录
- 生成 Token 存入 Redis（30 分钟有效期）
- 拦截器校验后续请求

**测试账号**：`18712345678` / `123456`

### 2. 活动列表与详情
- 查询进行中的活动，Redis 缓存 5 分钟
- 展示优惠金额、库存、时间等信息

### 3. 领取优惠券（核心流程）
1. 拦截器校验 Token
2. AOP 记录接口耗时
3. Redis 分布式锁防并发重复领取
4. Redis 检查是否已领取
5. Redis 原子预减库存（`DECR`）
6. 发送 RocketMQ 异步处理
7. 消费者事务写入：领券记录 + 扣减库存 + 消息日志
8. 幂等校验（`message_id` 唯一索引）

## 数据库设计

| 表名 | 说明 | 核心索引 |
|------|------|---------|
| user | 用户表 | phone（唯一） |
| coupon_activity | 活动表 | status + time（联合） |
| user_coupon | 领券记录 | user_id + activity_id（唯一，防重复） |
| message_log | 消息日志 | message_id（唯一，幂等） |

## Redis 设计

| Key | 说明 |
|------|------|
| `token:{token}` | 登录态 |
| `stock:{activityId}` | 实时库存 |
| `received:{userId}:{activityId}` | 领取标记 |
| `lock:receive:{userId}:{activityId}` | 分布式锁 |
| `activity:list` / `activity:{id}` | 活动缓存 |

## RocketMQ 设计

| 组件 | 配置 |
|------|------|
| Topic | `coupon-topic` |
| Producer Group | `coupon-producer-group` |
| Consumer Group | `coupon-consumer-group` |
| NameServer | `127.0.0.1:9876` |

## 启动步骤

### 1. 启动中间件

```bash
# 启动 RocketMQ（项目根目录）
docker-compose up -d

# 启动 Redis（需单独安装）
redis-server

# 启动 MySQL（需单独安装）
```

### 2. 初始化数据库

```sql
CREATE DATABASE coupon_db DEFAULT CHARACTER SET utf8mb4;
```

执行项目根目录 `coupon_db.sql`。

### 3. 初始化 Redis 库存

```bash
redis-cli
SET stock:1 100
SET stock:2 50
```

### 4. 修改配置

打开 `src/main/resources/application.yml`，修改：
- MySQL 用户名/密码
- Redis 密码（如有）

### 5. 启动项目

运行 `CouponDemoApplication.java` 或：

```bash
./mvnw spring-boot:run
```

### 6. 访问系统

- 登录页：`http://localhost:8080/login`
- 活动列表：`http://localhost:8080/activity/list`

## 接口说明

| 接口 | 方法 | 登录 | 说明 |
|------|------|------|------|
| `/login/doLogin` | POST | 否 | 登录，返回 token |
| `/activity/list` | GET | 否 | 活动列表 |
| `/activity/detail/{id}` | GET | 否 | 活动详情 |
| `/coupon/receive/{id}` | POST | 是 | 领取优惠券（Header 带 token） |

## 测试流程

1. 访问 `http://localhost:8080/login`
2. 登录 `18712345678` / `123456`
3. 进入活动列表，点击详情
4. 点击"立即领取"
5. 验证：
   - `user_coupon` 新增记录
   - `message_log` status=1
   - Redis `stock:1` 减少
   - 再次点击提示"已领取"

## 注意事项

1. 每次重启电脑后需重新启动 MySQL、Redis、RocketMQ（`docker-compose up -d`）
2. Redis 库存必须提前初始化，否则领券提示库存不足
3. 若 MQ 消费失败，Redis 库存已预扣，需手动恢复
