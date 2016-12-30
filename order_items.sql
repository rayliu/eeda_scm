/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 60011
Source Host           : localhost:3306
Source Database       : eeda_scm

Target Server Type    : MYSQL
Target Server Version : 60011
File Encoding         : 65001

Date: 2016-05-19 10:17:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items` (
  `id` bigint(20) NOT NULL,
  `item_no` varchar(20) DEFAULT NULL,
  `cus_item_no` varchar(20) DEFAULT NULL,
  `unit` varchar(3) DEFAULT NULL,
  `currency` varchar(3) DEFAULT NULL,
  `qty` double(19,5) DEFAULT NULL,
  `price` double(19,5) DEFAULT NULL,
  `total` double(19,5) DEFAULT NULL,
  `gift_flag` varchar(1) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
