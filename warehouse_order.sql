/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 60011
Source Host           : localhost:3306
Source Database       : eeda_yh

Target Server Type    : MYSQL
Target Server Version : 60011
File Encoding         : 65001

Date: 2016-06-02 15:09:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for warehouse_order
-- ----------------------------
DROP TABLE IF EXISTS `warehouse_order`;
CREATE TABLE `warehouse_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `party_id` bigint(20) DEFAULT NULL,
  `warehouse_id` bigint(20) DEFAULT NULL,
  `order_no` varchar(50) DEFAULT NULL,
  `order_type` varchar(50) DEFAULT NULL,
  `STATUS` varchar(50) DEFAULT NULL,
  `qualifier` varchar(50) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `creator` bigint(20) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `last_updater` bigint(20) DEFAULT NULL,
  `last_update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of warehouse_order
-- ----------------------------
INSERT INTO `warehouse_order` VALUES ('1', '75', '29', 'CK2015070800362', '出库', '新建', '', '', '30', '2015-07-08 22:27:35', '30', '2015-07-08 22:28:41');
INSERT INTO `warehouse_order` VALUES ('2', '2', '43', 'RK2015072900001', '入库', '新建', '', '', '30', '2015-07-29 09:00:30', '30', '2016-04-13 15:36:08');
INSERT INTO `warehouse_order` VALUES ('3', '2', '34', 'RK2016041200025', '入库', '新建', '', '', '28', '2016-04-12 09:15:28', null, null);
