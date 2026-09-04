-- Initial schema captured from the entity-validated development database.
SET FOREIGN_KEY_CHECKS = 0;
CREATE TABLE `biometrics` (
  `diastolic_bp` int NOT NULL,
  `heart_rate` int NOT NULL,
  `systolic_bp` int NOT NULL,
  `temperature` float DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `employee_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `measured_at` datetime(6) NOT NULL,
  `risk_level` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb9dc3kp5moeulqhf3fts6kdpb` (`employee_id`),
  CONSTRAINT `FKb9dc3kp5moeulqhf3fts6kdpb` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `checkup_reminder_settings` (
  `checkup_reminder_setting_is_active` bit(1) NOT NULL,
  `checkup_reminder_setting_id` bigint NOT NULL AUTO_INCREMENT,
  `checkup_reminder_setting_cron_schedule` varchar(30) NOT NULL,
  `checkup_reminder_setting_type` varchar(30) NOT NULL,
  `checkup_reminder_setting_message_template` text NOT NULL,
  PRIMARY KEY (`checkup_reminder_setting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `checkup_reminders` (
  `checkup_reminder_is_manual` bit(1) NOT NULL,
  `checkup_id` bigint NOT NULL,
  `checkup_reminder_id` bigint NOT NULL AUTO_INCREMENT,
  `checkup_reminder_sent_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `checkup_reminder_status` varchar(20) NOT NULL,
  `checkup_reminder_content` text NOT NULL,
  `checkup_reminder_channel` enum('EMAIL','SMS') NOT NULL,
  PRIMARY KEY (`checkup_reminder_id`),
  KEY `FKg4030fvj1q0yk50o3ou91q0di` (`checkup_id`),
  CONSTRAINT `FKg4030fvj1q0yk50o3ou91q0di` FOREIGN KEY (`checkup_id`) REFERENCES `checkups` (`checkup_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `checkups` (
  `checkup_date` date DEFAULT NULL,
  `checkup_year` smallint NOT NULL,
  `checkup_created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `checkup_id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL,
  `checkup_summary` text,
  PRIMARY KEY (`checkup_id`),
  KEY `FKbkmx6lgk3t3jmgjno26jhfmbp` (`employee_id`),
  CONSTRAINT `FKbkmx6lgk3t3jmgjno26jhfmbp` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `consultations` (
  `scheduled_date` date NOT NULL,
  `consultated_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `employee_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `manager_id` bigint DEFAULT NULL,
  `reason` varchar(100) NOT NULL,
  `content` text,
  `scheduled_turn` char(2) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'RESERVED',
  PRIMARY KEY (`id`),
  KEY `FK4li1vuweh4r10daiq7pkvpsuf` (`employee_id`),
  KEY `FKlokjl3qwb971d7gkl67kk3s9i` (`manager_id`),
  CONSTRAINT `FK4li1vuweh4r10daiq7pkvpsuf` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`),
  CONSTRAINT `FKlokjl3qwb971d7gkl67kk3s9i` FOREIGN KEY (`manager_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `departments` (
  `id` bigint NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `employees` (
  `hire_date` date DEFAULT NULL,
  `status` varchar(1) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `department_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `position_id` bigint DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `phone` varchar(13) DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `employee_number` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('EMPLOYEE','HEALTH_ADMIN','HR_ADMIN') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9aw59jtyajbpyr0q02h20ahql` (`employee_number`),
  KEY `FKgy4qe3dnqrm3ktd76sxp7n4c2` (`department_id`),
  KEY `FKngcpgx7fx5kednw3m7u0u8of3` (`position_id`),
  CONSTRAINT `FKgy4qe3dnqrm3ktd76sxp7n4c2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `FKngcpgx7fx5kednw3m7u0u8of3` FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `hospitals` (
  `is_colon_cancer_exam_available` bit(1) NOT NULL,
  `is_general_exam_available` bit(1) NOT NULL,
  `is_liver_cancer_exam_available` bit(1) NOT NULL,
  `is_lung_cancer_exam_available` bit(1) NOT NULL,
  `is_stomach_cancer_exam_available` bit(1) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `hospital_id` bigint NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(500) NOT NULL,
  `description` text,
  `name` varchar(255) NOT NULL,
  `status` char(1) DEFAULT 'Y',
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`hospital_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `notice_files` (
  `notice_file_id` bigint NOT NULL AUTO_INCREMENT,
  `notice_id` bigint NOT NULL,
  `extension` varchar(255) NOT NULL,
  `origin_name` varchar(255) NOT NULL,
  `saved_name` varchar(255) NOT NULL,
  `saved_path` varchar(255) NOT NULL,
  PRIMARY KEY (`notice_file_id`),
  KEY `FKip57mr5h9bo198ifujjdmxp68` (`notice_id`),
  CONSTRAINT `FKip57mr5h9bo198ifujjdmxp68` FOREIGN KEY (`notice_id`) REFERENCES `notices` (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `notices` (
  `count` int DEFAULT '0',
  `author_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `notice_id` bigint NOT NULL AUTO_INCREMENT,
  `update_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `content` text NOT NULL,
  `status` char(1) DEFAULT 'Y',
  `title` varchar(255) NOT NULL,
  PRIMARY KEY (`notice_id`),
  KEY `FK7yc204btaq6vdy1cksnyy75qa` (`author_id`),
  CONSTRAINT `FK7yc204btaq6vdy1cksnyy75qa` FOREIGN KEY (`author_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `positions` (
  `id` bigint NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `risk_threshold_settings` (
  `threshold_value` float NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `risk_level` varchar(20) NOT NULL,
  `metric_name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `safety_briefings` (
  `briefing_date` date NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `context_fingerprint` varchar(64) NOT NULL,
  `content` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_safety_briefings_date_context` (`briefing_date`,`context_fingerprint`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `safety_documents` (
  `created_at` datetime(6) NOT NULL,
  `created_by_employee_id` bigint NOT NULL,
  `file_size` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) NOT NULL,
  `updated_by_employee_id` bigint NOT NULL,
  `content_type` varchar(100) NOT NULL,
  `content_checksum` varchar(128) NOT NULL,
  `title` varchar(200) NOT NULL,
  `original_filename` varchar(512) NOT NULL,
  `storage_key` varchar(512) NOT NULL,
  `description` text,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbofjvnmwj5qrqbsghhu23exe3` (`content_checksum`),
  UNIQUE KEY `UKqoqhnqa7siwb1di3h1jc0lfui` (`storage_key`),
  KEY `FK7b5g5u99f7ilyl3vclxvixyhj` (`created_by_employee_id`),
  KEY `FKggwpmwsdo3eqqnnd60mvf6bbt` (`updated_by_employee_id`),
  CONSTRAINT `FK7b5g5u99f7ilyl3vclxvixyhj` FOREIGN KEY (`created_by_employee_id`) REFERENCES `employees` (`id`),
  CONSTRAINT `FKggwpmwsdo3eqqnnd60mvf6bbt` FOREIGN KEY (`updated_by_employee_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `timecards` (
  `clock_in_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `employee_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4nh8ro3igph4o4xn8frp298pn` (`employee_id`),
  CONSTRAINT `FK4nh8ro3igph4o4xn8frp298pn` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `vector_index_manifests` (
  `chunk_count` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `content_checksum` varchar(128) NOT NULL,
  `fingerprint` varchar(128) NOT NULL,
  `failure_message` varchar(1000) DEFAULT NULL,
  `status` enum('COMPLETED','FAILED','INDEXING','PENDING','PURGE_FAILED','PURGING') NOT NULL,
  PRIMARY KEY (`fingerprint`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `weather_forecasts` (
  `humidity` decimal(38,2) DEFAULT NULL,
  `precipitation_probability` decimal(38,2) DEFAULT NULL,
  `temperature` decimal(38,2) DEFAULT NULL,
  `wind_speed` decimal(38,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `forecast_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `precipitation` varchar(255) DEFAULT NULL,
  `snowfall` varchar(255) DEFAULT NULL,
  `location` enum('APGUJEONG','CHEONGDAM','DAECHI1','DAECHI2','DAECHI4','DOGOK1','DOGOK2','GAEPO1','GAEPO2','GAEPO3','GAEPO4','ILWON1','ILWONBON','NONHYEON1','NONHYEON2','SAMSEONG1','SAMSEONG2','SEGOK','SINSA','SUSEO','YEOKSAM1','YEOKSAM2') DEFAULT NULL,
  `precipitation_type` enum('NONE','RAIN','RAIN_SNOW','SHOWER','SNOW') DEFAULT NULL,
  `sky_condition` enum('CLEAR','CLOUDY','PARTLY_CLOUDY') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_weather_forecast_at_location` (`forecast_at`,`location`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
SET FOREIGN_KEY_CHECKS = 1;
