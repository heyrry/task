/******************************************/
/*   DatabaseName = icbu_scm_0000   */
/*   TableName = t_task   */
/******************************************/
CREATE TABLE `t_task` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `deleted` bigint(20) unsigned DEFAULT NULL COMMENT '逻辑删除',
  `unit` varchar(8) DEFAULT NULL COMMENT '单元',
  `job_id` bigint(20) unsigned DEFAULT NULL COMMENT '作业Id',
  `status` varchar(16) DEFAULT NULL COMMENT '任务状态',
  `job_biz_type` varchar(32) DEFAULT NULL COMMENT '作业业务类型',
  `job_biz_key` varchar(32) DEFAULT NULL COMMENT '作业业务Key',
  `task_biz_type` varchar(32) DEFAULT NULL COMMENT '任务业务类型',
  `task_biz_key` varchar(32) DEFAULT NULL COMMENT '任务业务Key',
  `expect_execute_time` datetime DEFAULT NULL COMMENT '预期执行时间',
  `context` text COMMENT '任务上下文',
  `result` text COMMENT '任务结果',
  `retry_times` tinyint(4) DEFAULT NULL COMMENT '重试次数',
  `pre_task_ids` varchar(128) DEFAULT NULL COMMENT '前驱任务',
  `next_task_ids` varchar(128) DEFAULT NULL COMMENT '后继任务',
  `hash_index` bigint(20) DEFAULT NULL COMMENT '哈希索引',
  `version` bigint(20) unsigned DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  KEY `idx_job_id` (`job_id`),
  KEY `idx_key` (`job_biz_key`,`job_biz_type`,`unit`),
  KEY `idx_hash` (`hash_index`,`job_biz_key`,`job_biz_type`,`unit`),
  KEY `idx_status` (`status`,`job_biz_type`,`unit`,`expect_execute_time`,`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=82002 DEFAULT CHARSET=utf8mb4 COMMENT='任务表'
;
