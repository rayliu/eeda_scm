/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 60011
Source Host           : localhost:3306
Source Database       : eeda_scm

Target Server Type    : MYSQL
Target Server Version : 60011
File Encoding         : 65001

Date: 2016-05-19 10:33:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL,
  `order_no` varchar(24) DEFAULT NULL,
  `order_time` timestamp NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `goods_value` double(19,5) DEFAULT NULL,
  `freight` double(19,5) DEFAULT NULL,
  `currency` varchar(3) DEFAULT NULL,
  `condignee_id` varchar(20) DEFAULT NULL,
  `condignee_type` varchar(1) DEFAULT NULL,
  `consignee` varchar(100) DEFAULT NULL,
  `consignee_address` varchar(200) DEFAULT NULL,
  `consignee_telephone` varchar(50) DEFAULT NULL,
  `consignee_country` varchar(3) DEFAULT NULL,
  `province` varchar(6) DEFAULT NULL,
  `city` varchar(6) DEFAULT NULL,
  `district` varchar(6) DEFAULT NULL,
  `pro_amount` double(19,5) DEFAULT NULL,
  `pro_remark` varchar(1000) DEFAULT NULL,
  `note` varchar(1000) DEFAULT NULL,
  `pay_no` varchar(24) DEFAULT NULL,
  `pay_platform` varchar(10) DEFAULT NULL,
  `payer_account` varchar(50) DEFAULT NULL,
  `payer_name` varchar(50) DEFAULT NULL,
  `is_pay_pass` varchar(1) DEFAULT NULL,
  `pass_pay_no` varchar(30) DEFAULT NULL,
  `pay_code` varchar(10) DEFAULT NULL,
  `pay_name` varchar(100) DEFAULT NULL,
  `process_status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
