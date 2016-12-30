/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 60011
Source Host           : localhost:3306
Source Database       : eeda_scm

Target Server Type    : MYSQL
Target Server Version : 60011
File Encoding         : 65001

Date: 2016-05-23 16:41:22
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for custom_company
-- ----------------------------
DROP TABLE IF EXISTS `custom_company`;
CREATE TABLE `custom_company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `shop_no` varchar(30) DEFAULT NULL,
  `shop_name` varchar(255) DEFAULT NULL,
  `balance` double(20,2) DEFAULT NULL,
  `legal_person` varchar(255) DEFAULT NULL,
  `contact_person` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(50) DEFAULT NULL,
  `company_phone` varchar(50) DEFAULT NULL,
  `cus_code` varchar(10) DEFAULT NULL,
  `ciq_code` varchar(10) DEFAULT NULL,
  `sign` varchar(10) DEFAULT NULL,
  `sort_code` varchar(10) DEFAULT NULL,
  `create_stamp` timestamp NULL DEFAULT '0000-00-00 00:00:00',
  `create_by` bigint(20) DEFAULT NULL,
  `update_stamp` timestamp NULL DEFAULT '0000-00-00 00:00:00',
  `update_by` bigint(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  `org_code` varchar(18) DEFAULT NULL,
  `warehouse_no` varchar(30) DEFAULT NULL,
  `ebp_code_cus` varchar(10) DEFAULT NULL,
  `ebp_code_ciq` varchar(10) DEFAULT NULL,
  `ebp_name` varchar(100) DEFAULT NULL,
  `ebc_code_cus` varchar(10) DEFAULT NULL,
  `ebc_code_ciq` varchar(10) DEFAULT NULL,
  `ebc_name` varchar(100) DEFAULT NULL,
  `agent_code_cus` varchar(10) DEFAULT NULL,
  `agent_code_ciq` varchar(10) DEFAULT NULL,
  `agent_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
