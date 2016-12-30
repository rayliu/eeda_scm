/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 60011
Source Host           : localhost:3306
Source Database       : eeda_yh

Target Server Type    : MYSQL
Target Server Version : 60011
File Encoding         : 65001

Date: 2016-06-02 15:08:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for privileges
-- ----------------------------
DROP TABLE IF EXISTS `privileges`;
CREATE TABLE `privileges` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `privilege` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of privileges
-- ----------------------------
INSERT INTO `privileges` VALUES ('1', '*');
INSERT INTO `privileges` VALUES ('2', 'view');
INSERT INTO `privileges` VALUES ('3', 'create');
INSERT INTO `privileges` VALUES ('4', 'update');
INSERT INTO `privileges` VALUES ('5', 'delete');
