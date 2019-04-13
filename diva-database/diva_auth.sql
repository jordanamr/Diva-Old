/*
 Navicat Premium Data Transfer

 Source Server         : Localhost
 Source Server Type    : MariaDB
 Source Server Version : 100138
 Source Host           : localhost:3306
 Source Schema         : diva_auth

 Target Server Type    : MariaDB
 Target Server Version : 100138
 File Encoding         : 65001

 Date: 13/04/2019 22:21:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for accounts
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(33) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `secret_question` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Supprimer ?',
  `secret_answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'oui',
  `remaining_subscription` int(11) NOT NULL DEFAULT -1,
  `rank` int(11) NOT NULL DEFAULT 1,
  `community` tinyint(2) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  UNIQUE INDEX `nickname`(`nickname`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of accounts
-- ----------------------------
INSERT INTO `accounts` VALUES (1, 'test', 'test', 'test', 'Supprimer ?', 'oui', -1, 1, 0);

-- ----------------------------
-- Table structure for characters
-- ----------------------------
DROP TABLE IF EXISTS `characters`;
CREATE TABLE `characters`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) NOT NULL,
  `server_id` int(11) NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `class` tinyint(2) NOT NULL DEFAULT 1,
  `gender` tinyint(1) NOT NULL DEFAULT 0,
  `gfx_id` smallint(4) NOT NULL DEFAULT 1010,
  `color1` int(11) NOT NULL DEFAULT -1,
  `color2` int(11) NOT NULL DEFAULT -1,
  `color3` int(11) NOT NULL DEFAULT -1,
  `level` mediumint(7) NOT NULL DEFAULT 1,
  `xp` bigint(19) NOT NULL DEFAULT 0,
  `hp` int(11) NOT NULL DEFAULT 55,
  `energy` smallint(5) NOT NULL DEFAULT 10000,
  `kamas` int(11) NOT NULL DEFAULT 0,
  `capital_stats` smallint(4) NOT NULL DEFAULT 0,
  `capital_spells` smallint(4) NOT NULL DEFAULT 0,
  `align_id` tinyint(1) NOT NULL DEFAULT 0,
  `align_level` tinyint(3) NOT NULL DEFAULT 0,
  `align_rank` tinyint(2) NOT NULL DEFAULT 0,
  `align_honor` smallint(5) NOT NULL DEFAULT 0,
  `align_dishonor` smallint(5) NOT NULL DEFAULT 0,
  `align_wings` tinyint(1) NOT NULL DEFAULT 0,
  `is_merchant` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`, `account_id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE,
  UNIQUE INDEX `id`(`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of characters
-- ----------------------------
INSERT INTO `characters` VALUES (1, 1, 900, 'Cry-Warre', 3, 0, 30, -1, -1, -1, 1, 0, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

-- ----------------------------
-- Table structure for ranks
-- ----------------------------
DROP TABLE IF EXISTS `ranks`;
CREATE TABLE `ranks`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `console_access` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of ranks
-- ----------------------------
INSERT INTO `ranks` VALUES (1, 'Joueur', 0);
INSERT INTO `ranks` VALUES (2, 'Modérateur', 1);
INSERT INTO `ranks` VALUES (3, 'Administrateur', 1);

-- ----------------------------
-- Table structure for servers
-- ----------------------------
DROP TABLE IF EXISTS `servers`;
CREATE TABLE `servers`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `completion` tinyint(1) NOT NULL DEFAULT 1,
  `p2p` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 901 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of servers
-- ----------------------------
INSERT INTO `servers` VALUES (900, 'Test', 0, 0);

SET FOREIGN_KEY_CHECKS = 1;
