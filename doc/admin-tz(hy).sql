/*
Navicat MySQL Data Transfer

Source Server         : 阿里云
Source Server Version : 50173
Source Host           : 106.15.89.145:3306
Source Database       : admin-tz

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2018-10-18 15:50:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dev_deploy
-- ----------------------------
DROP TABLE IF EXISTS `dev_deploy`;
CREATE TABLE `dev_deploy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `node_id` int(11) DEFAULT NULL COMMENT '节点',
  `type` int(11) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `dir` varchar(200) DEFAULT NULL,
  `jar_file` varchar(100) DEFAULT NULL COMMENT '路径',
  `args` varchar(50) DEFAULT NULL COMMENT '参数',
  `status` varchar(255) DEFAULT NULL,
  `uptime` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='程序部署';

-- ----------------------------
-- Records of dev_deploy
-- ----------------------------

-- ----------------------------
-- Table structure for dev_manifest
-- ----------------------------
DROP TABLE IF EXISTS `dev_manifest`;
CREATE TABLE `dev_manifest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `os` varchar(20) DEFAULT NULL COMMENT '操作系统',
  `host` varchar(50) DEFAULT NULL COMMENT '主机',
  `net_no` varchar(5) DEFAULT NULL COMMENT '编号',
  `user` varchar(50) DEFAULT NULL,
  `pwd` varchar(100) DEFAULT NULL,
  `tag` varchar(200) DEFAULT NULL COMMENT '标签',
  `note` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='清单';

-- ----------------------------
-- Records of dev_manifest
-- ----------------------------
INSERT INTO `dev_manifest` VALUES ('1', 'Linux', null, '23', null, null, '测试', '测试', '2018-10-15 15:55:27');

-- ----------------------------
-- Table structure for dev_node
-- ----------------------------
DROP TABLE IF EXISTS `dev_node`;
CREATE TABLE `dev_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `type` varchar(10) DEFAULT NULL COMMENT '类型',
  `os` varchar(20) DEFAULT NULL COMMENT '系统',
  `cpu_core` int(11) DEFAULT NULL COMMENT 'CPU核心数',
  `memory_size` int(11) DEFAULT NULL COMMENT '内存空间(GB)',
  `disk_size` int(11) DEFAULT NULL COMMENT '磁盘空间(GB',
  `host` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `port` int(11) DEFAULT NULL COMMENT '登录端口',
  `user` varchar(50) DEFAULT NULL COMMENT '登录用户',
  `pwd` varchar(100) DEFAULT NULL COMMENT '用户密码',
  `path` varchar(200) DEFAULT NULL,
  `check_on` int(11) DEFAULT NULL COMMENT '开启监控',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8 COMMENT='集群节点';

-- ----------------------------
-- Records of dev_node
-- ----------------------------
INSERT INTO `dev_node` VALUES ('19', '29', null, 'Linux', '16', '31', '264', '192.168.1.29', '22', 'root', '123456', '/opt/java/monitor/monitor-client.jar', '1', '2018-10-18 14:06:44');
INSERT INTO `dev_node` VALUES ('20', '28', null, 'Linux', '16', '31', '264', '192.168.1.28', '22', 'root', '123456', '/opt/java/monitor/monitor-client.jar', '1', '2018-10-18 15:21:15');
INSERT INTO `dev_node` VALUES ('21', '21', null, 'Linux', '16', '31', '1938', '192.168.1.21', '22', 'root', '123456', '/opt/java/monitor/monitor-client.jar', '1', '2018-10-18 15:22:17');
INSERT INTO `dev_node` VALUES ('22', '22', null, 'Linux', '16', '31', '1938', '192.168.1.22', '22', 'root', '123456', '/opt/java/monitor/monitor-client.jar', '1', '2018-10-18 15:22:34');
INSERT INTO `dev_node` VALUES ('23', '26', null, 'Linux', '16', '63', '1921', '192.168.1.26', '22', 'root', '123456', '/opt/java/monitor/monitor-client.jar', '1', '2018-10-18 15:31:16');
INSERT INTO `dev_node` VALUES ('24', '27', null, 'Linux', '16', '63', '1925', '192.168.1.27', '22', 'root', '123456', '/opt/java/monitor/monitor-client.jar', '1', '2018-10-18 15:31:28');
INSERT INTO `dev_node` VALUES ('25', '23', null, 'Linux', '16', '31', '18880', '192.168.1.23', '22', 'root', '123456', '/opt/java/monitor/monitor-client.jar', '0', '2018-10-18 15:32:01');
INSERT INTO `dev_node` VALUES ('26', '24', null, 'Linux', '16', '31', '18880', '192.168.1.24', '22', 'root', '123456', '/opt/java/monitor/monitor-client.jar', '0', '2018-10-18 15:32:14');

-- ----------------------------
-- Table structure for dev_node_status
-- ----------------------------
DROP TABLE IF EXISTS `dev_node_status`;
CREATE TABLE `dev_node_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `node_id` int(50) DEFAULT NULL,
  `cpu_usage` int(11) DEFAULT NULL,
  `memory_usage` int(11) DEFAULT NULL,
  `process_info` longtext,
  `disk_info` longtext,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COMMENT='节点实时状态信息';

-- ----------------------------
-- Records of dev_node_status
-- ----------------------------
INSERT INTO `dev_node_status` VALUES ('15', '19', '1', '25', 'cn.com.tiza.tstar.importer.hadoop.HadoopImporter: 7.6%,org.apache.hadoop.hdfs.server.namenode.NameNode: 2.3%, java -jar /tstar/tmp/obd-quartz.jar -p prod: 1.4%', '/dev/mapper/centos-root: 7%,/dev/sda1: 17%', '1', '2018-10-18 15:49:57');
INSERT INTO `dev_node_status` VALUES ('16', '20', '2', '25', '9999: 3.1%,start: 2.5%,org.apache.hadoop.hdfs.server.namenode.NameNode: 2.0%', '/dev/mapper/centos-root: 9%,/dev/sda1: 17%', '1', '2018-10-18 15:49:59');
INSERT INTO `dev_node_status` VALUES ('17', '21', '1', '20', 'start: 5.7%,037bb6f5-c3c6-43d2-88f9-c2f4f1295bb7: 3.9%,/tstar/kafka/config/server.properties: 3.5%', '/dev/mapper/centos-root: 3%,/dev/sda1: 17%,/dev/sdb1: 2%', '1', '2018-10-18 15:35:52');
INSERT INTO `dev_node_status` VALUES ('18', '22', '2', '28', 'start: 8.0%,gateway-estar_gb32960.xml: 5.4%,/tstar/kafka/config/server.properties: 3.5%', '/dev/mapper/centos-root: 3%,/dev/sda1: 17%,/dev/sdb1: 2%', '1', '2018-10-18 15:50:00');
INSERT INTO `dev_node_status` VALUES ('19', '23', '0', '18', 'ora_dbw0_hyora: 4.7%,ora_dbw1_hyora: 4.7%,ora_dbw2_hyora: 4.6%', '/dev/sda3: 8%,/dev/sdb1: 3%,/dev/sda1: 16%', '1', '2018-10-18 15:50:03');
INSERT INTO `dev_node_status` VALUES ('20', '24', '1', '40', 'oraclehyora (LOCAL=NO): 25.1%,oraclehyora (LOCAL=NO): 25.0%,oraclehyora (LOCAL=NO): 24.7%', '/dev/mapper/centos-root: 9%,/dev/sdb1: 5%,/dev/sda1: 18%', '1', '2018-10-18 15:35:50');
INSERT INTO `dev_node_status` VALUES ('21', '25', '1', '21', 'start: 10.2%,316a7879-6265-4182-99a3-d2a7874e318b: 2.3%,org.apache.hadoop.hdfs.server.datanode.DataNode: 1.1%', '/dev/mapper/centos-root: 3%,/dev/sdi1: 9%,/dev/sdc1: 9%', '1', '2018-10-18 15:33:37');
INSERT INTO `dev_node_status` VALUES ('22', '26', '1', '23', 'start: 8.2%,152ec854-6806-4b2e-9149-488678456324: 3.1%,a0d9b9c2-4e70-4b0a-8a97-f1b4f2397f60: 3.0%', '/dev/mapper/centos-root: 13%,/dev/sdd1: 9%,/dev/sdb1: 10%', '1', '2018-10-18 15:33:51');

-- ----------------------------
-- Table structure for rel_user_role
-- ----------------------------
DROP TABLE IF EXISTS `rel_user_role`;
CREATE TABLE `rel_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rel_user_role
-- ----------------------------
INSERT INTO `rel_user_role` VALUES ('35', '1', '1');
INSERT INTO `rel_user_role` VALUES ('42', '27', '17');
INSERT INTO `rel_user_role` VALUES ('39', '27', '16');
INSERT INTO `rel_user_role` VALUES ('27', '25', '15');

-- ----------------------------
-- Table structure for sys_asset
-- ----------------------------
DROP TABLE IF EXISTS `sys_asset`;
CREATE TABLE `sys_asset` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '资源名称',
  `code` varchar(50) DEFAULT NULL COMMENT '资源代码',
  `pid` int(11) DEFAULT NULL COMMENT '父ID',
  `pids` varchar(80) DEFAULT NULL COMMENT '父节组ID',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  `controller` varchar(100) DEFAULT NULL COMMENT '控制器',
  `view` varchar(100) DEFAULT NULL COMMENT '视图',
  `icon_css` varchar(100) DEFAULT NULL COMMENT '图标css',
  `is_menu` int(11) DEFAULT NULL COMMENT '是否菜单',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 COMMENT='系统资源';

-- ----------------------------
-- Records of sys_asset
-- ----------------------------
INSERT INTO `sys_asset` VALUES ('1', '首页', 'index', '0', '0', 'menu', '', '', 'mdi-home', '1', '1');
INSERT INTO `sys_asset` VALUES ('34', '程序清单', 'manifest', '39', '0/39', 'menu', 'home/manifest', 'devops/manifest', '', '1', '30');
INSERT INTO `sys_asset` VALUES ('5', '系统管理', 'sys', '0', '0', 'node', '', '', 'mdi-settings', '1', '100');
INSERT INTO `sys_asset` VALUES ('6', '用户管理', 'user', '5', '0/5', 'menu', 'home/user', 'sys/user', null, '1', '5');
INSERT INTO `sys_asset` VALUES ('7', '角色管理', 'role', '5', '0/5', 'menu', 'home/role', 'sys/role', null, '1', '10');
INSERT INTO `sys_asset` VALUES ('8', '菜单管理', 'menu', '5', '0/5', 'menu', 'home/menu', 'sys/menu', null, '1', '15');
INSERT INTO `sys_asset` VALUES ('39', '运维管理', 'devops', '0', '0', 'node', '', '', 'mdi-lan-connect', '1', '30');
INSERT INTO `sys_asset` VALUES ('43', '集群管理', 'cluster', '0', '0', 'node', '', '', 'mdi-sitemap', '1', '50');
INSERT INTO `sys_asset` VALUES ('44', '节点管理', 'node', '43', '0/43', 'menu', 'home/node', 'devops/node', '', '1', '10');
INSERT INTO `sys_asset` VALUES ('45', '节点状态', 'monitor', '43', '0/43', 'menu', 'home/monitor', 'devops/monitor', '', '1', '20');
INSERT INTO `sys_asset` VALUES ('46', '接入管理', 'deploy', '0', '0', 'node', '', '', 'mdi-power-plug', '1', '20');
INSERT INTO `sys_asset` VALUES ('47', '常规任务', 'normal', '46', '0/46', 'menu', 'home/deploy.1', 'deploy/normal', '', '1', '5');

-- ----------------------------
-- Table structure for sys_privilege
-- ----------------------------
DROP TABLE IF EXISTS `sys_privilege`;
CREATE TABLE `sys_privilege` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `master` varchar(50) DEFAULT NULL,
  `master_value` varchar(100) DEFAULT NULL,
  `access` varchar(50) DEFAULT NULL,
  `access_value` varchar(200) DEFAULT NULL,
  `permission` varchar(50) DEFAULT NULL,
  `comment` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=318 DEFAULT CHARSET=utf8 COMMENT='权限';

-- ----------------------------
-- Records of sys_privilege
-- ----------------------------
INSERT INTO `sys_privilege` VALUES ('252', 'role', '17', 'menu', '33', 'home:deploy', null);
INSERT INTO `sys_privilege` VALUES ('317', 'role', '1', 'menu', '47', 'plug:normal', null);
INSERT INTO `sys_privilege` VALUES ('253', 'role', '17', 'menu', '34', 'home:manifest', null);
INSERT INTO `sys_privilege` VALUES ('314', 'role', '1', 'menu', '33', 'devops:deploy', null);
INSERT INTO `sys_privilege` VALUES ('316', 'role', '1', 'menu', '45', 'cluster:monitor', null);
INSERT INTO `sys_privilege` VALUES ('315', 'role', '1', 'menu', '44', 'cluster:node', null);
INSERT INTO `sys_privilege` VALUES ('313', 'role', '1', 'menu', '8', 'sys:menu', null);
INSERT INTO `sys_privilege` VALUES ('312', 'role', '1', 'menu', '7', 'sys:role', null);
INSERT INTO `sys_privilege` VALUES ('311', 'role', '1', 'menu', '6', 'sys:user', null);
INSERT INTO `sys_privilege` VALUES ('251', 'role', '17', 'menu', '1', 'home:index', null);
INSERT INTO `sys_privilege` VALUES ('310', 'role', '1', 'menu', '1', 'home:index', null);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) DEFAULT NULL COMMENT '父ID',
  `name` varchar(50) DEFAULT NULL COMMENT '角色名称',
  `code` varchar(30) DEFAULT NULL COMMENT '角色代码',
  `comment` varchar(100) DEFAULT NULL COMMENT '角色描述',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', null, '管理员角色', 'admin', '管理员角色', 'admin', '2018-09-16 00:24:55');
INSERT INTO `sys_role` VALUES ('17', null, '测试角色', 'test', '测试角色', 'admin', '2018-09-16 13:39:58');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` varchar(80) DEFAULT NULL COMMENT '登录密码',
  `salt` varchar(50) DEFAULT NULL COMMENT '盐',
  `name` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `tel` varchar(15) DEFAULT NULL COMMENT '联系电话',
  `org_id` int(11) DEFAULT NULL COMMENT '用户所属机构',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `status` int(11) DEFAULT NULL COMMENT '用户状态',
  `user_icon` varchar(100) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `login_count` int(11) DEFAULT NULL COMMENT '登陆次数',
  `last_login_ip` varchar(20) DEFAULT NULL COMMENT '最后登陆IP',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登陆时间',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'admin', 'b722097ee08ea4c20eae7958639c839e', 'b9f1d5d2b65231cd016e6d84979f7078', '管理员', '87166669@dyl.com', '18086776731', null, null, null, null, 'icon1006822817203197796.jpg', '积极进取，持之以恒。', '2020-05-20 00:00:00', '1152', '218.3.247.226', '2018-10-18 15:37:49');
INSERT INTO `sys_user` VALUES ('27', 'test', '8d1e533914f577ec50f478973fcd7245', 'dd06bb22476ddb60057daf36ba5ffe58', '测试', '88776699@qq.com', '88776699', null, '2018-09-13 14:11:29', 'admin', '1', 'icon8735320946401041351.jpg', null, '2019-09-12 14:11:19', '3', '0:0:0:0:0:0:0:1', '2018-10-18 13:30:10');
