/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 60011
Source Host           : localhost:3306
Source Database       : eeda_yh

Target Server Type    : MYSQL
Target Server Version : 60011
File Encoding         : 65001

Date: 2016-06-02 16:56:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fin_account
-- ----------------------------
DROP TABLE IF EXISTS `fin_account`;
CREATE TABLE `fin_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) DEFAULT NULL,
  `currency` varchar(50) DEFAULT NULL,
  `bank_name` varchar(50) DEFAULT NULL,
  `account_no` varchar(50) DEFAULT NULL,
  `bank_person` varchar(50) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `creator` bigint(20) DEFAULT NULL,
  `office_id` bigint(20) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `is_stop` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of fin_account
-- ----------------------------
INSERT INTO `fin_account` VALUES ('1', 'ALL', '人民币', '工商银行', '12123123123', '滨港', '', null, '4', '-7684.809999999998', null);
INSERT INTO `fin_account` VALUES ('2', 'ALL', '人民币', '东亚银行', '111610000', '源鸿物流', '', null, '4', '-9023806.95', null);
INSERT INTO `fin_account` VALUES ('3', 'ALL', '人民币', '广州银行', '12123123123', '源鸿物流', '', null, '4', null, null);
INSERT INTO `fin_account` VALUES ('4', 'ALL', '人民币', '现金', '123456', '源鸿物流', '这是现金账户', null, '4', '1868900.9400000002', null);
INSERT INTO `fin_account` VALUES ('5', 'ALL', '人民币', '广州银行（滨港）', '123456', '滨港货运', '', null, '4', null, null);
INSERT INTO `fin_account` VALUES ('6', 'ALL', '人民币', '东亚银行（供应链）', '123456', '源鸿供应链', '', null, '4', '4045000', null);
INSERT INTO `fin_account` VALUES ('7', 'ALL', '人民币', '广州银行（供应链）', '广州银行（供应链）', '广州银行（供应链）', '', null, '4', '50005', null);
