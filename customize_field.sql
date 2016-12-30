/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 60011
Source Host           : localhost:3306
Source Database       : eeda_yh

Target Server Type    : MYSQL
Target Server Version : 60011
File Encoding         : 65001

Date: 2016-05-30 15:17:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for customize_field
-- ----------------------------
DROP TABLE IF EXISTS `customize_field`;
CREATE TABLE `customize_field` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_type` varchar(50) DEFAULT NULL,
  `office_id` bigint(20) DEFAULT NULL,
  `field_code` varchar(50) DEFAULT NULL,
  `field_name` varchar(50) DEFAULT NULL,
  `field_desc` varchar(500) DEFAULT NULL,
  `is_hidden` tinyint(1) DEFAULT NULL,
  `customize_name` varchar(50) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of customize_field
-- ----------------------------
INSERT INTO `customize_field` VALUES ('1', 'TO', '52', 'EX_CARGO', 'ATM', null, null, 'ATM', null);
INSERT INTO `customize_field` VALUES ('2', 'PO', '52', 'EX_TYPE', '源鸿自提', null, null, '源鸿自提', null);
INSERT INTO `customize_field` VALUES ('4', 'latestOrderNo', null, 'PS2016052700004', null, null, null, null, null);
