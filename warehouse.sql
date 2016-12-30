/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 60011
Source Host           : localhost:3306
Source Database       : eeda_yh

Target Server Type    : MYSQL
Target Server Version : 60011
File Encoding         : 65001

Date: 2016-06-02 15:09:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for warehouse
-- ----------------------------
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `warehouse_name` varchar(50) DEFAULT NULL,
  `warehouse_address` varchar(255) DEFAULT NULL,
  `warehouse_area` double DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `warehouse_type` varchar(255) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `code` varchar(20) DEFAULT NULL,
  `warehouse_desc` varchar(5120) DEFAULT NULL,
  `sp_id` bigint(20) DEFAULT NULL,
  `office_id` bigint(20) DEFAULT NULL,
  `sp_name` varchar(255) DEFAULT NULL,
  `notify_name` varchar(50) DEFAULT NULL,
  `notify_mobile` varchar(50) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sp_id` (`sp_id`),
  KEY `office_id` (`office_id`),
  CONSTRAINT `warehouse_ibfk_1` FOREIGN KEY (`sp_id`) REFERENCES `party` (`id`),
  CONSTRAINT `warehouse_ibfk_3` FOREIGN KEY (`office_id`) REFERENCES `office` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of warehouse
-- ----------------------------
INSERT INTO `warehouse` VALUES ('2', '济南中转仓', '济南市天桥区蓝翔中路2-1号 ', '1000', null, 'ownWarehouse', 'active', null, '', '93', '24', '济南骏运展达物流有限公司', '', '', '370100');
INSERT INTO `warehouse` VALUES ('3', '青岛中转仓', '青岛市西元庄物流市场45.46号库', '450', null, 'ownWarehouse', 'active', null, '', '93', '24', '济南骏运展达物流有限公司', '', '', '370200');
INSERT INTO `warehouse` VALUES ('4', '北京中转仓', '北京市朝阳区东风乡高庙村68号', '1000', null, 'ownWarehouse', 'active', null, '', '99', '8', '北京文成物流有限公司', '', '', '110100');
INSERT INTO `warehouse` VALUES ('5', '天津中转仓', '天津市北辰区外环线与北辰西道交口天津韩家墅海吉星物流园区商业31-32号，仓库4栋1号', '450', null, 'ownWarehouse', 'active', null, '', '202', '10', '天津源鸿物流有限公司', '', '', '120100');
INSERT INTO `warehouse` VALUES ('6', '太原中转仓', '太原小店区许坦东街23号', '450', null, 'ownWarehouse', 'active', null, '', '114', '17', '山西启程物流有限公司', '', '', '140100');
INSERT INTO `warehouse` VALUES ('7', '上海中转仓', '上海市南翔嘉好路230号', '450', null, 'ownWarehouse', 'active', null, '', '100', '9', '上海源鸿物流有限公司', '', '', '310100');
INSERT INTO `warehouse` VALUES ('8', '石家庄中转仓', '石家庄市西二环北路106号', '450', null, 'ownWarehouse', 'active', null, '', '101', '16', '石家庄源鸿物流有限公司', '', '', '130100');
INSERT INTO `warehouse` VALUES ('9', '杭州中转仓', '杭州市江干区九堡家苑二区多层公寓三幢三单元11-12号', '450', null, 'ownWarehouse', 'active', null, '', '11', '28', '杭州汉唐货运有限公司', '', '', '330100');
INSERT INTO `warehouse` VALUES ('10', '义乌中转仓', '义乌市西城一区22幢1号', '300', null, 'ownWarehouse', 'active', null, '', '11', '40', '杭州汉唐货运有限公司', '', '', '330782');
INSERT INTO `warehouse` VALUES ('11', '宁波中转仓', '宁波市江北区康庄南路', '300', null, 'ownWarehouse', 'active', null, '', '96', '43', '宁波天地货运', '', '', '330200');
INSERT INTO `warehouse` VALUES ('12', '西安中转仓', '西安市石化大道北徐十字西口广利丰物流园7号库，门口挂的是徐工集团的牌子 ', '450', null, 'ownWarehouse', 'active', null, '', '102', '18', '西安乐三家物流有限公司', '', '', '610100');
INSERT INTO `warehouse` VALUES ('13', '贵阳中转仓', '贵阳市南明区龙洞堡', '450', null, 'ownWarehouse', 'active', null, '', '98', '34', '贵州宝彩物流有限公司', '', '', '520100');
INSERT INTO `warehouse` VALUES ('14', '厦门中转仓', '厦门市湖里区电前6路', '450', null, 'ownWarehouse', 'active', null, '', '103', '39', '福建畅佳物流有限公司', '', '', '350200');
INSERT INTO `warehouse` VALUES ('15', '福州中转仓', '福州市仓山区战备路580盘屿仓库', '450', null, 'ownWarehouse', 'active', null, '', '103', '32', '福州宅急送物流有限公司', '', '', '350100');
INSERT INTO `warehouse` VALUES ('16', '南宁中转仓', '南宁市高新区科园大道70号', '600', null, 'ownWarehouse', 'active', null, '', '104', '36', '南宁源鸿物流有限公司', '董玲', '', '450100');
INSERT INTO `warehouse` VALUES ('17', '海口中转仓', '海口市椰海大道苍西村路口进来村1000米，二十六小便利店对面', '300', null, 'ownWarehouse', 'active', null, '', '10', '37', '海南嘉诚物流有限公司', '', '', '460100');
INSERT INTO `warehouse` VALUES ('18', '兰州中转仓', '兰州市城关区古城坪40号', '300', null, 'ownWarehouse', 'active', null, '', '105', '20', '兰州诚博物流有限公司', '', '', '620100');
INSERT INTO `warehouse` VALUES ('19', '拉萨中转仓', '拉萨市藏大西路', '150', null, 'ownWarehouse', 'active', null, '', '106', '23', '拉萨蓝天物流有限公司', '', '', '540100');
INSERT INTO `warehouse` VALUES ('20', '西宁中转仓', '西宁市城东区互助中路92号东方物流', '150', null, 'ownWarehouse', 'active', null, '', '1030', '21', '西宁东方货运有限公司', '', '', '630100');
INSERT INTO `warehouse` VALUES ('21', '武汉中转仓', '武汉市硚口区汉正西物流中心A座24号', '150', null, 'ownWarehouse', 'active', null, '', '108', '29', '武汉正旭物流有限公司', '', '', '420100');
INSERT INTO `warehouse` VALUES ('22', '长沙中转仓', '长沙市雨花区高桥物流中心新2号小区C23栋3单元302室', '100', null, 'ownWarehouse', 'active', null, '', null, '31', '长沙源鸿物流有限公司', '', '', '430100');
INSERT INTO `warehouse` VALUES ('23', '呼市中转仓', '内蒙古呼和浩特市110国道503公里处路南广硕物流园南方物流', '200', null, 'ownWarehouse', 'active', null, '', '109', '15', '南方物流有限公司', '', '', '150100');
INSERT INTO `warehouse` VALUES ('24', '成都中转仓', '成都市武侯区金花镇金兴北路518号', '200', null, 'ownWarehouse', 'active', null, '', '110', '33', '成都千顺物流有限公司', '', '', '510100');
INSERT INTO `warehouse` VALUES ('25', '郑州中转仓', '郑州市航海路七里河美景鸿5-1-2402 ', '300', null, 'ownWarehouse', 'active', null, '', '111', '25', '郑州晨曦物流有限公司', '', '', '410100');
INSERT INTO `warehouse` VALUES ('26', '沈阳中转仓', '沈阳市和平区三好街118号', '300', null, 'ownWarehouse', 'active', null, '', null, '14', '沈阳一运实业有限责任公司科技商城配送中心', '', '', '210100');
INSERT INTO `warehouse` VALUES ('27', '长春中转仓', '长春市绿园区青年路与荣祥路交汇西行300米金星技校院内联捷物流', '300', null, 'ownWarehouse', 'active', null, '', '113', '13', '吉林省联捷物流仓储有限公司', '', '', '220100');
INSERT INTO `warehouse` VALUES ('28', '重庆中转仓', '重庆市九龙坡区谢家湾正街3号', '200', null, 'ownWarehouse', 'active', null, '', '177', '11', '重庆兵工物流有限公司', '', '', '500100');
INSERT INTO `warehouse` VALUES ('29', '广州源鸿总仓', '广州市萝岗区宏明路严田商业街11号', '582', null, 'ownWarehouse', 'active', null, '广州总部仓', null, '4', null, '侯耀辉', '62192677', '440100');
INSERT INTO `warehouse` VALUES ('33', '哈尔滨中转仓', '哈尔滨', '500', null, 'ownWarehouse', 'active', null, '', '200', '12', null, '', '', '230100');
INSERT INTO `warehouse` VALUES ('34', '昆明中转仓', '昆明', '450', null, 'ownWarehouse', 'active', null, '', '197', '35', null, '朱长明', '', '530100');
INSERT INTO `warehouse` VALUES ('36', '温州中转仓', '温州市青年路', '1000', null, 'ownWarehouse', 'active', null, '', '201', '45', null, '温州仓库负责人', '666666666', '330300');
INSERT INTO `warehouse` VALUES ('37', '桂林中转仓', '桂林市', '1000', null, 'ownWarehouse', 'active', null, '', '104', '36', null, '桂林仓库老板', '666666666', '450300');
INSERT INTO `warehouse` VALUES ('38', '南京中转仓', '南京市', '1000', null, 'ownWarehouse', 'active', null, '', '344', '27', null, '南京仓库老板', '666666666', '320100');
INSERT INTO `warehouse` VALUES ('40', '合肥中转仓', '合肥市', '100', null, 'ownWarehouse', 'active', null, '', '199', '26', null, '', '', '340100');
INSERT INTO `warehouse` VALUES ('41', '柳州中转仓', '柳州市', '100', null, 'ownWarehouse', 'active', null, '', '104', '36', null, '董玲', '', '450100');
INSERT INTO `warehouse` VALUES ('42', '乌鲁木齐中转仓', '乌鲁木齐市', '100', null, 'ownWarehouse', 'active', null, '', '580', '19', null, '', '', '650100');
INSERT INTO `warehouse` VALUES ('43', '广州源鸿新塘仓库', '广州市新塘镇', '100', null, 'ownWarehouse', 'active', null, '源鸿广州新塘仓库', null, '4', null, '侯耀辉', '13926051226', '440100');
INSERT INTO `warehouse` VALUES ('44', '大连中转仓', '李天成', '200', null, 'ownWarehouse', 'active', null, '', '112', '14', null, '22', '', '210100');
INSERT INTO `warehouse` VALUES ('47', '南昌中转仓', '南昌市', '100', null, 'ownWarehouse', 'active', null, '100', '2961', '30', null, '', '', '360100');
INSERT INTO `warehouse` VALUES ('48', '梧州中转仓', '梧州市', '100', null, 'ownWarehouse', 'active', null, '', '104', '36', null, '董玲', '', '450100');
INSERT INTO `warehouse` VALUES ('49', '银川中转仓', '银川市', '100', null, 'ownWarehouse', 'active', null, '', '5311', '22', null, '', '', '640100');
INSERT INTO `warehouse` VALUES ('50', '玉林中转仓', '玉林市', '100', null, 'ownWarehouse', 'active', null, '', '104', '36', null, '董玲', '', '450900');
INSERT INTO `warehouse` VALUES ('51', '贺州中转仓', '贺州市', '100', null, 'ownWarehouse', 'active', null, '', '104', '36', null, '董玲', '', '451100');
