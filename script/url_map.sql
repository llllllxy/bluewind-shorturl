/*
 Navicat MySQL Data Transfer

 Source Server         : 本地MySQL-127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : localhost:3306
 Source Schema         : short_url

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 19/05/2022 17:49:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for s_access_log
-- ----------------------------
DROP TABLE IF EXISTS `s_access_log`;
CREATE TABLE `s_access_log`  (
  `id` bigint(29) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `surl` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '短链',
  `access_time` char(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '访问时间：格式为yyyyMMddHHmmss',
  `access_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问IP',
  `access_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问者的user_agent',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '访问日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of s_access_log
-- ----------------------------
INSERT INTO `s_access_log` VALUES (1, '17mVP6', '20220518112401', '[0:0:0:0:0:0:0:1]', '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-05-18 11:24:01');
INSERT INTO `s_access_log` VALUES (2, '2EeMFq', '20220518112449', '10.49.12.120', '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-05-18 11:24:49');
INSERT INTO `s_access_log` VALUES (3, 'XFhg0', '20220518184342', '192.168.16.64', '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-05-18 18:43:42');
INSERT INTO `s_access_log` VALUES (4, 'XFhg0', '20220518185614', '192.168.16.64', '{\"operatingSystem\":\"WINDOWS_10\",\"browser\":\"CHROME10\",\"id\":35131151,\"browserVersion\":{\"version\":\"101.0.4951.64\",\"majorVersion\":\"101\",\"minorVersion\":\"0\"}}', '2022-05-18 18:56:14');
INSERT INTO `s_access_log` VALUES (5, '3bcrpl', '20220518190729', '192.168.16.64', '{\"operatingSystem\":\"WINDOWS_10\",\"browser\":\"CHROME10\",\"id\":35131151,\"browserVersion\":{\"version\":\"101.0.4951.64\",\"majorVersion\":\"101\",\"minorVersion\":\"0\"}}', '2022-05-18 19:07:29');

-- ----------------------------
-- Table structure for s_url_map
-- ----------------------------
DROP TABLE IF EXISTS `s_url_map`;
CREATE TABLE `s_url_map`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `surl` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '短链接',
  `lurl` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '原始链接',
  `views` int(11) NOT NULL COMMENT '访问次数',
  `expire_time` char(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '到期时间，格式：yyyyMMddHHmmss',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0--未删除1--已删除）',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `surl`(`surl`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 126 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短链基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of s_url_map
-- ----------------------------
INSERT INTO `s_url_map` VALUES (87, '4ZkMwo', 'https://www.cnblogs.com/javayida/p/13346223232937.html', 0, '20230401215411', '', '2022-03-13 14:36:51', NULL);
INSERT INTO `s_url_map` VALUES (89, '3MV6Nv', 'https://www.cnblogs.com/javayida/p/13346223232937.html', 0, NULL, '', '2022-03-13 14:37:56', NULL);
INSERT INTO `s_url_map` VALUES (92, '1ZbUeG', 'https://www.cnblogs.com/javayida/p/13346223232937.html', 0, NULL, '', '2022-03-13 14:38:24', NULL);
INSERT INTO `s_url_map` VALUES (96, '3p4qRD', 'https://www.cnblogs.com/javayida/p/13346223232937.html', 0, NULL, '', '2022-03-13 14:38:25', NULL);
INSERT INTO `s_url_map` VALUES (97, '4Kz101', 'https://www.baidu.com/s?wd=%E5%B9%BF%E4%B8%9C%E5%85%AC%E5%AE%89%E5%8E%85%E5%89%AF%E5%8E%85%E9%95%BF%E7%AD%896%E4%BA%BA%E6%8A%97%E7%96%AB%E4%B8%8D%E5%8A%9B%E8%A2%AB%E5%85%8D&tn=baiduhome_pg&rsv_idx=2&ie=utf-8&rsv_pq=c3c3d0170000c4d2&oq=%E7%9F%AD%E9%93%BE%E6%8E%A5%E7%94%9F%E6%88%90%E5%99%A8&rsv_t=4edcnb0uxI86fiOtflAPf4uGqqR9xM6Z4vrPJP0Trb8m9HKV4gt1HiU%2BGRSWlLvMDN%2FB&rqid=c3c3d0170000c4d2&rsf=a77c88916041436e13099de6edb9e869_1_10_3&rsv_dl=0_right_fyb_pchot_20811&sa=0_right_fyb_pchot_20811', 1, '20230401215411', '', '2022-03-13 17:45:42', NULL);
INSERT INTO `s_url_map` VALUES (98, '2NqTut', 'http://www.metools.info/master/shorturl180.html', 0, '20230401215411', '', '2022-03-13 17:48:30', NULL);
INSERT INTO `s_url_map` VALUES (100, 'FgyZV', 'http://www.metools.info/master/shorturl180.html', 0, '20230401215411', '', '2022-03-13 17:48:34', NULL);
INSERT INTO `s_url_map` VALUES (103, '1Qu5QD', 'http://www.metools.info/master/shorturl180.html', 0, '20230401215411', '', '2022-03-13 17:48:38', NULL);
INSERT INTO `s_url_map` VALUES (107, '1mCpmx', 'http://www.metools.info/master/shorturl180.html', 0, '20230401215411', '', '2022-03-13 17:48:41', NULL);
INSERT INTO `s_url_map` VALUES (112, '35QE6u', 'http://www.metools.info/master/shorturl180.html', 0, '20230401215411', '', '2022-03-13 17:49:01', NULL);
INSERT INTO `s_url_map` VALUES (118, '17mVP6', 'http://www.metools.info/master/shorturl180.html', 3, '20230401215411', '', '2022-03-13 17:49:02', NULL);
INSERT INTO `s_url_map` VALUES (119, '2EeMFq', 'https://www.cnblogs.com/sjks/p/10741299.html', 3, '20230401215411', '', '2022-03-13 17:54:29', NULL);
INSERT INTO `s_url_map` VALUES (120, '1wPqTf', 'https://www.runoob.com/markdown/md-advance.html', 0, '20230401215411', '', '2022-03-13 18:47:10', NULL);
INSERT INTO `s_url_map` VALUES (122, 'XFhg0', 'https://www.runoob.com/markdown/md-advance.html', 2, '20230401215411', '', '2022-03-13 21:20:28', NULL);
INSERT INTO `s_url_map` VALUES (123, 'ucgRD', 'https://www.qiwenshare.com/opensource', 1, '20230401215411', '', '2022-03-16 15:48:55', NULL);
INSERT INTO `s_url_map` VALUES (124, '4eTNxX', 'http://jnhrss.jinan.gov.cn/art/2022/2/28/art_41078_4781298.html', 1, '20230401215411', '', '2022-03-25 21:54:12', NULL);
INSERT INTO `s_url_map` VALUES (125, '3bcrpl', 'https://zhuanlan.zhihu.com/p/137613156', 1, '20220525190720', '0', '2022-05-18 19:07:20', NULL);

-- ----------------------------
-- Table structure for s_user
-- ----------------------------
DROP TABLE IF EXISTS `s_user`;
CREATE TABLE `s_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '状态（0--正常 1--冻结）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0--未删除1--已删除）',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 183892222923 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of s_user
-- ----------------------------
INSERT INTO `s_user` VALUES (150917903665, 'admin2', '2f9af0ca3f620f3fc3ac3d24a8f7b97d272f886bd3dc40e57b0ab82699201507', '哈哈', NULL, '0', '0', '2022-03-30 22:41:36');
INSERT INTO `s_user` VALUES (183892222922, 'admin', '2f9af0ca3f620f3fc3ac3d24a8f7b97d272f886bd3dc40e57b0ab82699201507', '喵喵(123456)', 'http://halo.lxyccc.top/头像.jpg', '0', '0', '2020-09-06 19:40:49');

SET FOREIGN_KEY_CHECKS = 1;
