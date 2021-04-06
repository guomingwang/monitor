CREATE TABLE `gamma_monitor_setting` (
    `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `device_num` varchar(50) NOT NULL unique DEFAULT '' COMMENT '设备序列号',
    `device_ip` varchar(50) NOT NULL DEFAULT '' COMMENT '设备ip',
    `device_name` varchar(50) NOT NULL DEFAULT '' COMMENT '设备编号',
    `system` varchar(50) NOT NULL DEFAULT '' COMMENT '设备所属系统',
    `alarm_threshold` float NOT NULL DEFAULT 0.0 COMMENT '报警阈值',
    `voltage_module_status` tinyint(1) default 0 not null COMMENT '高压模块启停状态',
    `gm_module_status` tinyint(1) default 0 not null COMMENT 'GM模块启停状态',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

