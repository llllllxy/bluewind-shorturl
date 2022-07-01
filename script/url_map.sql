/*
 Navicat MySQL Data Transfer

 Source Server         : 阿里云RDS-MySQL
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : mysql.rds.aliyuncs.com:3306
 Source Schema         : bluewind-shorturl

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 01/07/2022 09:30:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for s_access_log
-- ----------------------------
DROP TABLE IF EXISTS `s_access_log`;
CREATE TABLE `s_access_log`  (
                                 `log_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID',
                                 `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户ID',
                                 `surl` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '短链',
                                 `access_time` char(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '访问时间：格式为yyyyMMddHHmmss',
                                 `access_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问IP',
                                 `access_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问地址',
                                 `access_user_agent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问者的user_agent',
                                 `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '访问日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of s_access_log
-- ----------------------------
INSERT INTO `s_access_log` VALUES ('1', '231233522', '17mVP6', '20220518112401', '[0:0:0:0:0:0:0:1]', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-05-18 11:24:01');
INSERT INTO `s_access_log` VALUES ('1527832830138134528', 'common', '1HdSE5', '20220521100507', '10.49.12.120', NULL, '{\"operatingSystem\":\"WINDOWS_10\",\"browser\":\"CHROME10\",\"id\":35131151,\"browserVersion\":{\"version\":\"101.0.4951.64\",\"majorVersion\":\"101\",\"minorVersion\":\"0\"}}', '2022-05-21 10:05:07');
INSERT INTO `s_access_log` VALUES ('1527834752045146112', 'common', '4GAYP3', '20220521101245', '10.49.12.120', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-05-21 10:12:45');
INSERT INTO `s_access_log` VALUES ('1527835119554256896', 'common', '4GAYP3', '20220521101412', '10.49.12.120', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-05-21 10:14:12');
INSERT INTO `s_access_log` VALUES ('1537432309921411072', 'common', '10wFKq', '20220616215001', '192.168.0.104', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-16 21:50:01');
INSERT INTO `s_access_log` VALUES ('1537432501563355136', 'common', '10wFKq', '20220616215047', '192.168.0.104', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-16 21:50:47');
INSERT INTO `s_access_log` VALUES ('1538040047671980032', 'common', 'xiumG', '20220618140457', NULL, NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-18 14:04:57');
INSERT INTO `s_access_log` VALUES ('1538040077216657408', 'common', 'xiumG', '20220618140504', NULL, NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-18 14:05:04');
INSERT INTO `s_access_log` VALUES ('1538447510813212672', 'common', '2qgB0p', '20220619170404', NULL, NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-19 17:04:04');
INSERT INTO `s_access_log` VALUES ('1538495100816314368', 'common', '3M2nrc', '20220619201310', NULL, NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-19 20:13:10');
INSERT INTO `s_access_log` VALUES ('1538530576799014912', 'common', '3M2nrc', '20220619223408', '192.168.0.104', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-19 22:34:08');
INSERT INTO `s_access_log` VALUES ('1538532039819182080', '1', '2qve90', '20220619223957', '192.168.16.66', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-19 22:39:57');
INSERT INTO `s_access_log` VALUES ('1538771853564391424', '1', '27EV2p', '20220620143253', '10.49.12.120', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-20 14:32:53');
INSERT INTO `s_access_log` VALUES ('1538771999480033280', '1', 'U3zg5', '20220620143328', '192.168.16.64', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-20 14:33:28');
INSERT INTO `s_access_log` VALUES ('1538821074062020608', '1', '2tzPxo', '20220620174828', '192.168.16.69', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-20 17:48:28');
INSERT INTO `s_access_log` VALUES ('1540350795741069312', 'common', '2pEKPH', '20220624230702', NULL, NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-24 23:07:02');
INSERT INTO `s_access_log` VALUES ('1540968987316887552', 'common', '2pEKPH', '20220626160331', NULL, NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-26 16:03:31');
INSERT INTO `s_access_log` VALUES ('1540969143026229248', 'common', '2pEKPH', '20220626160408', NULL, NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-26 16:04:08');
INSERT INTO `s_access_log` VALUES ('1540971103980789760', 'common', '1Y7UNq', '20220626161155', NULL, NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-26 16:11:55');
INSERT INTO `s_access_log` VALUES ('1541344409451421696', '1', 'U3zg5', '20220627165518', NULL, NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-27 16:55:18');
INSERT INTO `s_access_log` VALUES ('1541652457951526912', 'common', '2lwnLd', '20220628131923', NULL, 'ip为空，无法获取位置', '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-28 13:19:23');
INSERT INTO `s_access_log` VALUES ('1541654832798208000', '1', 'U3zg5', '20220628132849', NULL, 'ip为空，无法获取位置', '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-06-28 13:28:49');
INSERT INTO `s_access_log` VALUES ('2', '231233522', '2EeMFq', '20220518112449', '10.49.12.120', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-05-18 11:24:49');
INSERT INTO `s_access_log` VALUES ('3', '231233522', 'XFhg0', '20220518184342', '192.168.16.64', NULL, '{\"operatingSystem\":\"UNKNOWN\",\"browser\":\"UNKNOWN\",\"id\":16843022,\"browserVersion\":null}', '2022-05-18 18:43:42');
INSERT INTO `s_access_log` VALUES ('4', '231233522', 'XFhg0', '20220518185614', '192.168.16.64', NULL, '{\"operatingSystem\":\"WINDOWS_10\",\"browser\":\"CHROME10\",\"id\":35131151,\"browserVersion\":{\"version\":\"101.0.4951.64\",\"majorVersion\":\"101\",\"minorVersion\":\"0\"}}', '2022-05-18 18:56:14');
INSERT INTO `s_access_log` VALUES ('5', '231233522', '3bcrpl', '20220518190729', '192.168.16.64', NULL, '{\"operatingSystem\":\"WINDOWS_10\",\"browser\":\"CHROME10\",\"id\":35131151,\"browserVersion\":{\"version\":\"101.0.4951.64\",\"majorVersion\":\"101\",\"minorVersion\":\"0\"}}', '2022-05-18 19:07:29');

-- ----------------------------
-- Table structure for s_admin
-- ----------------------------
DROP TABLE IF EXISTS `s_admin`;
CREATE TABLE `s_admin`  (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                            `account` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
                            `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
                            `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
                            `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号',
                            `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
                            `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '状态（0--正常 1--冻结）',
                            `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0--未删除1--已删除）',
                            `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 183892222923 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of s_admin
-- ----------------------------
INSERT INTO `s_admin` VALUES (150917903665, 'admin2', '2f9af0ca3f620f3fc3ac3d24a8f7b97d272f886bd3dc40e57b0ab82699201507', '哈哈', '17862789900', '145788931@qq.com', '0', '0', '2022-03-30 22:41:36');
INSERT INTO `s_admin` VALUES (183892222922, 'admin', '2f9af0ca3f620f3fc3ac3d24a8f7b97d272f886bd3dc40e57b0ab82699201507', '喵喵(123456)', '17862789922', '1457889221@qq.com', '0', '0', '2020-09-06 19:40:49');

-- ----------------------------
-- Table structure for s_tenant
-- ----------------------------
DROP TABLE IF EXISTS `s_tenant`;
CREATE TABLE `s_tenant`  (
                             `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户ID，这里不能使用自增主键，容易被猜出来租户编码，使用UUID或者雪花ID',
                             `tenant_account` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户账号',
                             `tenant_password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户密码',
                             `tenant_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户名称',
                             `tenant_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户手机号',
                             `tenant_email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户邮箱',
                             `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '状态（0--正常 1--冻结）',
                             `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0--未删除1--已删除）',
                             `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `access_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户的AccessKey',
                             `access_key_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '租户的AccessKey Secret',
                             PRIMARY KEY (`tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of s_tenant
-- ----------------------------
INSERT INTO `s_tenant` VALUES ('1', 'tenant1', '2f9af0ca3f620f3fc3ac3d24a8f7b97d272f886bd3dc40e57b0ab82699201507', '租户1', '17862719591', NULL, '0', '0', '2022-05-26 22:01:38', 'b6feebb10fff47f49c451046c1afb4f3', 'NWRhNDM0OTlkYjFmMzZmNDMwMGE4NTU5YzVlZTg0YTU');
INSERT INTO `s_tenant` VALUES ('1535513792758063104', 'tenant12', '2f9af0ca3f620f3fc3ac3d24a8f7b97d272f886bd3dc40e57b0ab82699201507', 'c测试组织', '17862719591', NULL, '0', '0', '2022-06-11 14:46:31', '5df5537174db45cbbc024336d30eacb9', 'NTk5OWY1ZjQxZGRlN2NmYzdkNWI4ZTY3OGYzMjY5MDQ');

-- ----------------------------
-- Table structure for s_url_map
-- ----------------------------
DROP TABLE IF EXISTS `s_url_map`;
CREATE TABLE `s_url_map`  (
                              `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务主键',
                              `surl` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '短链接',
                              `lurl` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '原始链接',
                              `views` int(11) NOT NULL COMMENT '访问次数',
                              `expire_time` char(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '到期时间，格式：yyyyMMddHHmmss',
                              `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '状态标志（0--启用1--禁用）',
                              `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志（0--未删除1--已删除）',
                              `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
                              `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'common' COMMENT '租户ID',
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `surl`(`surl`) USING BTREE COMMENT '唯一索引，surl不允许重复'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短链基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of s_url_map
-- ----------------------------
INSERT INTO `s_url_map` VALUES ('100', 'FgyZV', 'http://www.metools.info/master/shorturl180.html', 0, '20230401215411', '0', '1', '2022-03-13 17:48:34', NULL, '1');
INSERT INTO `s_url_map` VALUES ('103', '1Qu5QD', 'http://www.metools.info/master/shorturl180.html', 0, '20230401215411', '0', '1', '2022-03-13 17:48:38', NULL, '1');
INSERT INTO `s_url_map` VALUES ('107', '1mCpmx', 'http://www.metools.info/master/shorturl180.html', 0, '20230401215411', '0', '0', '2022-03-13 17:48:41', NULL, '1');
INSERT INTO `s_url_map` VALUES ('112', '35QE6u', 'http://www.metools.info/master/shorturl180.html', 0, '20230401215411', '0', '0', '2022-03-13 17:49:01', NULL, '1');
INSERT INTO `s_url_map` VALUES ('118', '17mVP6', 'http://www.metools.info/master/shorturl180.html', 3, '20230401215411', '0', '0', '2022-03-13 17:49:02', NULL, '1');
INSERT INTO `s_url_map` VALUES ('119', '2EeMFq', 'https://www.cnblogs.com/sjks/p/10741299.html', 3, '20230401215411', '0', '0', '2022-03-13 17:54:29', NULL, '1');
INSERT INTO `s_url_map` VALUES ('120', '1wPqTf', 'https://www.runoob.com/markdown/md-advance.html', 0, '20230401215411', '0', '0', '2022-03-13 18:47:10', NULL, '1');
INSERT INTO `s_url_map` VALUES ('122', 'XFhg0', 'https://www.runoob.com/markdown/md-advance.html', 2, '20230401215411', '0', '0', '2022-03-13 21:20:28', NULL, '1');
INSERT INTO `s_url_map` VALUES ('123', 'ucgRD', 'https://www.qiwenshare.com/opensource', 1, '20230401215411', '0', '0', '2022-03-16 15:48:55', NULL, '1');
INSERT INTO `s_url_map` VALUES ('124', '4eTNxX', 'http://jnhrss.jinan.gov.cn/art/2022/2/28/art_41078_4781298.html', 1, '20230401215411', '0', '0', '2022-03-25 21:54:12', NULL, '1');
INSERT INTO `s_url_map` VALUES ('125', '3bcrpl', 'https://zhuanlan.zhihu.com/p/137613156', 1, '20220525190720', '0', '0', '2022-05-18 19:07:20', NULL, '1');
INSERT INTO `s_url_map` VALUES ('126', '1HdSE5', 'https://www.iconfont.cn/search/index?searchType=icon&q=%E5%8A%A0%E5%8F%B7', 2, '20991231235959', '0', '0', '2022-05-21 09:48:50', NULL, '1');
INSERT INTO `s_url_map` VALUES ('1527834727567187968', '4GAYP3', 'https://juejin.cn/post/6844904184953700360#heading-32', 2, '20991231235959', '0', '0', '2022-05-21 10:12:39', NULL, '1');
INSERT INTO `s_url_map` VALUES ('1537432300576501760', '10wFKq', 'https://www.ixigua.com/7094934811709538829?logTag=280d5ed0051d2a2dca06', 2, '20220623214959', '0', '0', '2022-06-16 21:49:59', NULL, '1');
INSERT INTO `s_url_map` VALUES ('1537683048126234624', 'GmPcI', 'https://juejin.cn/post/7107046500340858893', 0, '20220917142622', '0', '0', '2022-06-17 14:26:22', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1537683682653126656', '4NjS3b', 'https://docs.qq.com/desktop/?u=cc71a31084e746569a193deb598940ea', 0, '20220624142853', '0', '0', '2022-06-17 14:28:55', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1537685071940235264', '3OL2ec', 'http://10.49.12.120:8076/4NjS3b', 0, '20220624143424', '0', '0', '2022-06-17 14:34:25', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1537687861632090112', '2Df81A', 'https://blog.csdn.net/qq_25046827/article/details/124697105', 0, '20220624144529', '0', '0', '2022-06-17 14:45:31', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1537693843346440192', '11xkeR', 'http://git.inspur.com/ecbh/isurvey/backend/isurvey/blob/master/src/main/java/com/inspur/isurvey/common/utils/FileUtils.java', 0, '20220624150915', '0', '0', '2022-06-17 15:09:16', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1537695043819159552', 'jHZCW', 'https://office.inspur.com/eportal/ui?pageId=2026743', 0, '20220624151402', '0', '0', '2022-06-17 15:14:04', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1537969891319197696', '2oaD3z', 'http://git.inspur.com/ecbh', 0, '20220625092610', '0', '0', '2022-06-18 09:26:11', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1538016758065614848', '4LI1Pa', 'http://10.110.34.64:84/operate/#/portal/frame/lcm/spart/account/index?cache=true', 0, '20220625123224', '1', '0', '2022-06-18 12:32:25', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1538035478574161920', '4fNg9Y', 'https://blog.csdn.net/qq_41701956/article/details/124825393', 0, '20220625134647', '0', '0', '2022-06-18 13:46:48', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1538040019704360960', 'xiumG', 'https://element.eleme.cn/#/zh-CN/component/icon', 2, '20220625140450', '0', '0', '2022-06-18 14:04:51', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1538447490886074368', '2qgB0p', 'https://www.ixigua.com/7088899011519873566?logTag=086f860ed8a7a4b628ec', 1, '20220626170359', '0', '0', '2022-06-19 17:03:59', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1538447860274233344', '1krcKX', 'https://baijiahao.baidu.com/s?id=1735974204923473521', 0, '20221219170527', '0', '0', '2022-06-19 17:05:27', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1538495076132835328', '3M2nrc', 'https://juejin.cn/post/7016520448204603423#comment', 2, '20220626201304', '0', '0', '2022-06-19 20:13:04', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1538531991156867072', '2qve90', 'https://www.jianshu.com/p/7d4bc61c1a5b', 1, '20220919223945', '1', '0', '2022-06-19 22:39:46', NULL, '1');
INSERT INTO `s_url_map` VALUES ('1538771782051508224', '27EV2p', 'https://juejin.cn/post/6844903644798664712', 1, '20220920143236', '0', '0', '2022-06-20 14:32:36', NULL, '1');
INSERT INTO `s_url_map` VALUES ('1538771965342593024', 'U3zg5', 'https://md5jiami.bmcx.com/', 3, '20220920143320', '0', '0', '2022-06-20 14:33:20', NULL, '1');
INSERT INTO `s_url_map` VALUES ('1538798928825401344', 'R6R4i', 'https://blog.csdn.net/nandao158/article/details/121929764', 0, '20220627162028', '0', '0', '2022-06-20 16:20:29', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1538821039484178432', '2tzPxo', 'https://www.163.com/dy/article/H4EHJDBA0552VYQ0.html', 1, '20220620194656', '0', '0', '2022-06-20 17:48:20', NULL, '1');
INSERT INTO `s_url_map` VALUES ('1538884714644860928', '3rg8nz', 'https://blog.csdn.net/hesqlplus730/article/details/123105322', 0, '20220627220121', '0', '0', '2022-06-20 22:01:23', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('1540350772466876416', '2pEKPH', 'https://codingdict.com/os/software/84258', 3, '20220701230657', '0', '0', '2022-06-24 23:06:57', '门户平台生成', 'common');
INSERT INTO `s_url_map` VALUES ('1540351174360891392', 'RSvW9', 'https://www.runoob.com/bootstrap/bootstrap-grid-system.html', 0, '20220712000000', '1', '0', '2022-06-24 23:08:33', '哈哈哈2', '1');
INSERT INTO `s_url_map` VALUES ('1540970988704538624', '1Y7UNq', 'https://www.ixigua.com/7111323385874874887?logTag=8ed728b94d4d118e4e7f', 1, '20220703161128', '0', '0', '2022-06-26 16:11:28', '门户平台生成', 'common');
INSERT INTO `s_url_map` VALUES ('1541652364145917952', '2lwnLd', 'http://qr.bslyun.com/statistical?time=&dlj=&type=shorten', 1, '20220705131900', '0', '0', '2022-06-28 13:19:02', '门户平台生成', 'common');
INSERT INTO `s_url_map` VALUES ('87', '4ZkMwo', 'https://www.cnblogs.com/javayida/p/13346223232937.html', 0, '20230401215411', '0', '0', '2022-03-13 14:36:51', NULL, '1');
INSERT INTO `s_url_map` VALUES ('89', '3MV6Nv', 'https://www.cnblogs.com/javayida/p/13346223232937.html', 0, NULL, '0', '0', '2022-03-13 14:37:56', NULL, '1');
INSERT INTO `s_url_map` VALUES ('92', '1ZbUeG', 'https://www.cnblogs.com/javayida/p/13346223232937.html', 0, NULL, '0', '0', '2022-03-13 14:38:24', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('96', '3p4qRD', 'https://www.cnblogs.com/javayida/p/13346223232937.html', 0, NULL, '0', '0', '2022-03-13 14:38:25', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('97', '4Kz101', 'https://www.baidu.com/s?wd=%E5%B9%BF%E4%B8%9C%E5%85%AC%E5%AE%89%E5%8E%85%E5%89%AF%E5%8E%85%E9%95%BF%E7%AD%896%E4%BA%BA%E6%8A%97%E7%96%AB%E4%B8%8D%E5%8A%9B%E8%A2%AB%E5%85%8D&tn=baiduhome_pg&rsv_idx=2&ie=utf-8&rsv_pq=c3c3d0170000c4d2&oq=%E7%9F%AD%E9%93%BE%E6%8E%A5%E7%94%9F%E6%88%90%E5%99%A8&rsv_t=4edcnb0uxI86fiOtflAPf4uGqqR9xM6Z4vrPJP0Trb8m9HKV4gt1HiU%2BGRSWlLvMDN%2FB&rqid=c3c3d0170000c4d2&rsf=a77c88916041436e13099de6edb9e869_1_10_3&rsv_dl=0_right_fyb_pchot_20811&sa=0_right_fyb_pchot_20811', 1, '20230401215411', '0', '0', '2022-03-13 17:45:42', NULL, 'common');
INSERT INTO `s_url_map` VALUES ('98', '2NqTut', 'http://www.metools.info/master/shorturl180.html', 0, '20230401215411', '0', '0', '2022-03-13 17:48:30', NULL, 'common');

SET FOREIGN_KEY_CHECKS = 1;
