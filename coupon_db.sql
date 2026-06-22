/*
 Navicat Premium Dump SQL

 Source Server         : xt
 Source Server Type    : MySQL
 Source Server Version : 50726 (5.7.26)
 Source Host           : localhost:3306
 Source Schema         : coupon_db

 Target Server Type    : MySQL
 Target Server Version : 50726 (5.7.26)
 File Encoding         : 65001

 Date: 22/06/2026 17:59:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for coupon_activity
-- ----------------------------
DROP TABLE IF EXISTS `coupon_activity`;
CREATE TABLE `coupon_activity`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动名称',
  `total_amount` int(11) NOT NULL DEFAULT 0 COMMENT '发行总量',
  `remain_amount` int(11) NOT NULL DEFAULT 0 COMMENT '剩余数量',
  `discount_amount` decimal(10, 2) NOT NULL COMMENT '优惠金额',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '1:进行中 0:已结束',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_time`(`start_time`, `end_time`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_status_time`(`status`, `start_time`, `end_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '优惠券活动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coupon_activity
-- ----------------------------
INSERT INTO `coupon_activity` VALUES (1, '新人专享券', 100, 99, 50.00, '2026-06-20 00:00:00', '2026-07-20 23:59:59', 1);
INSERT INTO `coupon_activity` VALUES (2, '618大促券', 50, 49, 100.00, '2026-06-20 00:00:00', '2026-06-30 23:59:59', 1);

-- ----------------------------
-- Table structure for message_log
-- ----------------------------
DROP TABLE IF EXISTS `message_log`;
CREATE TABLE `message_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息唯一ID',
  `user_id` bigint(20) NOT NULL,
  `activity_id` bigint(20) NOT NULL,
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '0:待处理 1:成功 2:失败',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `message_id`(`message_id`) USING BTREE,
  INDEX `idx_message_id`(`message_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息消费日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_log
-- ----------------------------
INSERT INTO `message_log` VALUES (1, '4aab9a4c83b94645', 1, 1, 1, '2026-06-21 18:36:53');
INSERT INTO `message_log` VALUES (2, '71d0657c5d0b49b8', 1, 2, 1, '2026-06-22 14:35:50');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码（MD5）',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `phone`(`phone`) USING BTREE,
  INDEX `idx_phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '18712345678', '4e847ba0ccdfa60f2e4cffda15546d04', '测试用户1', '2026-06-21 16:43:45');
INSERT INTO `user` VALUES (2, '18787654321', '4e847ba0ccdfa60f2e4cffda15546d04', '测试用户2', '2026-06-22 18:00:00');

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `coupon_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '券码',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '1:未使用 2:已使用 3:已过期',
  `receive_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `coupon_code`(`coupon_code`) USING BTREE,
  UNIQUE INDEX `uk_user_activity`(`user_id`, `activity_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户领券记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------
INSERT INTO `user_coupon` VALUES (1, 1, 1, '826603a079b44a0c', 1, '2026-06-21 18:36:53');
INSERT INTO `user_coupon` VALUES (2, 1, 2, '5c4c5245eec64b82', 1, '2026-06-22 14:35:50');

SET FOREIGN_KEY_CHECKS = 1;
