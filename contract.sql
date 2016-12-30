/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 60011
Source Host           : localhost:3306
Source Database       : eeda_yh

Target Server Type    : MYSQL
Target Server Version : 60011
File Encoding         : 65001

Date: 2016-05-30 15:20:36
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for contract
-- ----------------------------
DROP TABLE IF EXISTS `contract`;
CREATE TABLE `contract` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `party_id` bigint(20) DEFAULT NULL,
  `period_from` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `period_to` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `remark` varchar(255) DEFAULT NULL,
  `create_by` bigint(20) DEFAULT NULL,
  `is_stop` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of contract
-- ----------------------------
INSERT INTO `contract` VALUES ('8', '干线公司合同-海南专线', 'SERVICE_PROVIDER', '10', '2015-03-24 16:25:37', '2017-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('10', '淇辉物流-杭州专线', 'SERVICE_PROVIDER', '5', '2015-03-24 15:38:35', '2017-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('13', '世纪顺通-济南专线', 'SERVICE_PROVIDER', '7', '2015-03-24 15:38:36', '2015-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('18', '盛丰物流-福州专线', 'SERVICE_PROVIDER', '8', '2015-03-24 15:38:34', '2014-10-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('20', '安顺物流-厦门专线', 'SERVICE_PROVIDER', '76', '2015-03-24 15:38:37', '2015-12-01 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('22', '浙信物流-义乌专线', 'SERVICE_PROVIDER', '77', '2015-03-24 15:38:38', '2017-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('24', '大顺发物流-乌市拉萨专线', 'SERVICE_PROVIDER', '78', '2015-03-24 15:38:37', '2020-12-01 00:00:00', '合同无限期', null, null);
INSERT INTO `contract` VALUES ('26', '万发物流-东北三省专线', 'SERVICE_PROVIDER', '79', '2015-03-24 15:38:34', '2015-10-24 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('28', '山西货运-山西/呼市专线', 'SERVICE_PROVIDER', '80', '2015-03-24 15:38:36', '2016-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('30', '超越运输-青岛专线', 'SERVICE_PROVIDER', '81', '2015-01-01 00:00:00', '2016-09-01 00:00:00', '新合同未签', null, null);
INSERT INTO `contract` VALUES ('35', '大广运输-贵阳专线', 'SERVICE_PROVIDER', '82', '2015-03-24 15:38:35', '2016-02-29 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('40', '干线公司合同-北京专线#2', 'SERVICE_PROVIDER', '84', '2015-03-24 16:25:28', '2015-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('42', '杰鑫物流-上海专线', 'SERVICE_PROVIDER', '86', '2015-03-24 15:38:37', '2015-12-01 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('43', '万载龙翔物流-兰州专线', 'SERVICE_PROVIDER', '87', '2015-03-24 15:38:36', '2015-12-01 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('45', '鹏龙物流-武汉专线', 'SERVICE_PROVIDER', '89', '2015-03-24 15:38:37', '2015-11-01 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('46', '宝洁物流-石家庄专线', 'SERVICE_PROVIDER', '90', '2015-03-24 15:38:37', '2016-01-01 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('48', '桂华物流-广西专线', 'SERVICE_PROVIDER', '91', '2015-03-24 15:38:35', '2014-10-30 00:00:00', '未签新合同', null, null);
INSERT INTO `contract` VALUES ('49', '兴华兄弟货运-南昌专线', 'SERVICE_PROVIDER', '92', '2015-03-24 15:38:36', '2015-01-31 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('50', '骏运展达物流-山东配送', 'DELIVERY_SERVICE_PROVIDER', '93', '2015-03-24 15:38:39', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('52', '汉唐货运-杭州配送', 'DELIVERY_SERVICE_PROVIDER', '11', '2015-03-24 15:38:38', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('54', '宅急送-福州配送', 'DELIVERY_SERVICE_PROVIDER', '14', '2015-03-24 15:38:34', '2015-12-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('56', '文成物流-北京配送', 'DELIVERY_SERVICE_PROVIDER', '99', '2015-03-24 15:38:33', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('57', '上海源鸿-上海配送', 'DELIVERY_SERVICE_PROVIDER', '100', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('58', '合旭家政-河北配送', 'DELIVERY_SERVICE_PROVIDER', '101', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('59', '乐三家物流-陕西配送', 'DELIVERY_SERVICE_PROVIDER', '102', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('60', '畅佳货运-厦门配送', 'DELIVERY_SERVICE_PROVIDER', '103', '2015-03-24 15:38:39', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('61', '诚博物流-广西配送', 'DELIVERY_SERVICE_PROVIDER', '104', '2015-03-24 15:38:38', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('62', '涴鸿物流-甘肃配送', 'DELIVERY_SERVICE_PROVIDER', '105', '2015-03-24 15:38:39', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('63', '蓝天物流-西藏配送', 'DELIVERY_SERVICE_PROVIDER', '106', '2015-03-24 15:38:39', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('64', '新宇物流-青海配送', 'DELIVERY_SERVICE_PROVIDER', '107', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('65', '武汉正旭-湖北配送', 'DELIVERY_SERVICE_PROVIDER', '108', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('66', '南方物流-内蒙配送', 'DELIVERY_SERVICE_PROVIDER', '109', '2015-03-24 15:38:39', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('67', '成都配送中心-成都千顺', 'DELIVERY_SERVICE_PROVIDER', '110', '2015-03-24 16:25:25', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('68', '晨曦物流-河南配送', 'DELIVERY_SERVICE_PROVIDER', '111', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('69', '沈阳一运-辽宁配送', 'DELIVERY_SERVICE_PROVIDER', '112', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('70', '联捷物流-吉林配送', 'DELIVERY_SERVICE_PROVIDER', '113', '2015-03-24 15:38:38', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('71', '北京励志运输有限公司-山西配送', 'DELIVERY_SERVICE_PROVIDER', '114', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('72', '日通雅马哈钢琴2014-2015年度合同', 'CUSTOMER', '27', '2014-04-29 00:00:00', '2015-07-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('73', '广电运通2014-2015年度合同', 'CUSTOMER', '1', '2015-01-01 00:00:00', '2015-08-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('74', '东方通信2014-2015年度合同', 'CUSTOMER', '28', '2014-01-01 00:00:00', '2015-08-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('78', '远拓物流-西安专线', 'SERVICE_PROVIDER', '165', '2015-03-24 15:38:37', '2014-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('79', '文成物流-北京外发干线', 'SERVICE_PROVIDER', '99', '2015-03-24 15:38:33', '2015-12-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('80', '重庆金桥-重庆配送', 'DELIVERY_SERVICE_PROVIDER', '177', '2015-03-24 15:38:41', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('81', '万达金诚物流-宁波专线', 'SERVICE_PROVIDER', '178', '2015-03-24 15:38:36', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('82', '瑞千鑫物流-昆明贵阳专线', 'SERVICE_PROVIDER', '179', '2015-03-24 15:38:36', '2016-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('83', '渝粤物流-成都重庆专线', 'SERVICE_PROVIDER', '180', '2015-03-24 15:38:34', '2016-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('84', '锦遥物流-太原专线', 'SERVICE_PROVIDER', '182', '2015-03-24 15:38:37', '2014-10-30 00:00:00', '未签新合同', null, null);
INSERT INTO `contract` VALUES ('85', '秦源物流-上海专线', 'SERVICE_PROVIDER', '184', '2015-03-24 15:38:37', '2015-12-01 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('87', '盛裕物流-云浮专线', 'SERVICE_PROVIDER', '189', '2015-03-24 15:38:35', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('88', '嘉里大通2014年年度合同', 'CUSTOMER', '181', '2014-01-01 00:00:00', '2015-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('89', '创丰物流-南宁专线', 'SERVICE_PROVIDER', '190', '2015-03-24 15:38:36', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('91', '顺伟物流-潮汕专线', 'SERVICE_PROVIDER', '185', '2015-03-24 15:38:34', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('92', '博英物流-粤北专线', 'SERVICE_PROVIDER', '192', '2015-03-24 15:38:34', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('93', '宁波声广2014年度合同', 'CUSTOMER', '136', '2014-01-01 00:00:00', '2015-12-05 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('95', '隆安物流-河源梅州专线', 'SERVICE_PROVIDER', '193', '2015-03-24 15:38:35', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('96', '互盈物流-粤西专线', 'SERVICE_PROVIDER', '194', '2015-03-24 15:38:35', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('98', '昆明优迅-云南配送', 'DELIVERY_SERVICE_PROVIDER', '197', '2015-03-24 15:38:39', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('99', '珠海近铁2014年合同', 'CUSTOMER', '183', '2014-01-01 00:00:00', '2015-12-05 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('100', '北京思创2014年度合同', 'CUSTOMER', '187', '2014-12-05 09:51:34', '2015-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('101', '亿轮达-合肥配送', 'DELIVERY_SERVICE_PROVIDER', '199', '2015-03-24 15:38:38', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('102', '恒路物流-黑龙江配送', 'DELIVERY_SERVICE_PROVIDER', '200', '2015-03-24 15:38:38', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('103', '宝彩物流-贵州配送', 'DELIVERY_SERVICE_PROVIDER', '98', '2015-03-24 15:38:38', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('104', '温州环成-温州配送', 'DELIVERY_SERVICE_PROVIDER', '201', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('105', '刘根喜-天津配送', 'DELIVERY_SERVICE_PROVIDER', '202', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('108', '长城信息2014-2015年度合同', 'CUSTOMER', '2', '2014-08-15 00:00:00', '2015-08-14 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('109', '北京中钞2014-2015年度合同', 'CUSTOMER', '3', '2014-09-01 00:00:00', '2015-08-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('117', '皇金海-杭州外发专线', 'SERVICE_PROVIDER', '338', '2015-03-24 15:38:38', '2015-12-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('121', '天地货运-宁波配送', 'DELIVERY_SERVICE_PROVIDER', '96', '2015-03-24 15:38:39', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('122', '杭州平盛-南京配送', 'DELIVERY_SERVICE_PROVIDER', '344', '2015-03-24 15:38:39', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('124', '新宇物流-西宁专线', 'SERVICE_PROVIDER', '107', '2015-03-24 15:38:37', '2015-01-31 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('125', '正旭物流-长沙武汉专线', 'SERVICE_PROVIDER', '492', '2015-03-24 15:38:38', '2015-12-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('126', '昊驰物流-海南专线', 'SERVICE_PROVIDER', '500', '2015-03-24 15:38:35', '2016-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('127', '富邦物流-杭州专线', 'SERVICE_PROVIDER', '501', '2015-03-24 15:38:36', '2016-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('128', '巨邦物流-温州专线', 'SERVICE_PROVIDER', '502', '2015-03-24 15:38:37', '2016-12-01 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('129', '联发运输-宁波专线', 'SERVICE_PROVIDER', '503', '2015-03-24 15:38:36', '2015-12-01 00:00:00', '合同未签', null, null);
INSERT INTO `contract` VALUES ('130', '同星物流-兰州西宁拉萨专线', 'SERVICE_PROVIDER', '504', '2015-03-24 15:38:36', '2015-01-31 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('131', '汇通物流-石家庄专线', 'SERVICE_PROVIDER', '506', '2015-03-24 15:38:37', '2015-01-31 00:00:00', '未签合同', null, null);
INSERT INTO `contract` VALUES ('133', '广州源鸿-珠三角配送', 'DELIVERY_SERVICE_PROVIDER', '524', '2015-03-24 15:38:41', '2029-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('134', '元大物流-潮汕长春专线', 'SERVICE_PROVIDER', '526', '2015-03-24 15:38:33', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('135', '安南达-潮汕西安西宁兰州专线', 'SERVICE_PROVIDER', '527', '2015-03-24 15:38:33', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('136', '潮州老苏-潮汕乌市专线', 'SERVICE_PROVIDER', '528', '2015-03-24 15:38:33', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('137', '海川运输-潮汕成都专线', 'SERVICE_PROVIDER', '529', '2015-03-24 15:38:33', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('138', '兴捷货运-潮汕西宁专线', 'SERVICE_PROVIDER', '530', '2015-03-24 15:38:33', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('139', '潮州吴总-潮汕武汉专线', 'SERVICE_PROVIDER', '531', '2015-03-24 15:38:33', '2016-01-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('142', '康玉红-新疆配送', 'DELIVERY_SERVICE_PROVIDER', '580', '2015-03-24 15:38:40', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('143', '中发物流-合肥专线', 'SERVICE_PROVIDER', '581', '2015-03-24 15:38:36', '2014-10-30 00:00:00', '未签新合同', null, null);
INSERT INTO `contract` VALUES ('144', '翔宇物流-西安专线', 'SERVICE_PROVIDER', '812', '2015-03-24 15:38:37', '2016-11-27 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('145', '爱必达-青海配送', 'DELIVERY_SERVICE_PROVIDER', '1030', '2015-03-24 15:38:39', '2017-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('146', '广电运通2015年合同', 'CUSTOMER', '1', '2015-01-13 00:00:00', '2015-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('147', '东方通信2015年度合同', 'CUSTOMER', '28', '2015-01-01 00:00:00', '2015-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('148', '干线公司合同-海南豪邦专线合同', 'SERVICE_PROVIDER', '2429', '2015-01-01 00:00:00', '2016-07-01 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('149', '干线公司合同长沙专线', 'SERVICE_PROVIDER', null, '2015-06-01 00:00:00', '2017-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('150', '广电国际2015-2016年合同', 'CUSTOMER', '3540', '2015-01-01 00:00:00', '2016-12-31 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('151', '北京京达-北京专线', 'SERVICE_PROVIDER', null, '2015-07-27 00:00:00', '2016-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('152', '天邦-东北三省专线', 'SERVICE_PROVIDER', null, '2015-05-01 00:00:00', '2017-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('153', '天邦物流-东北三省专线', 'SERVICE_PROVIDER', null, '2015-01-01 00:00:00', '2017-12-30 00:00:00', '', null, null);
INSERT INTO `contract` VALUES ('154', '（上海）北京励志运输有限公司', 'DELIVERY_SERVICE_PROVIDER', '114', '2015-12-01 00:00:00', '2017-12-30 00:00:00', '', null, null);
