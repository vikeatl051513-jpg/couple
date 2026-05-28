CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT PRIMARY KEY,
  `openid` VARCHAR(64) NOT NULL UNIQUE,
  `unionid` VARCHAR(64) NULL,
  `nickname` VARCHAR(64) NOT NULL,
  `title` VARCHAR(64) NULL,
  `avatar_url` VARCHAR(255) NULL,
  `gender` TINYINT DEFAULT 0,
  `birthday` DATE NULL,
  `points` INT NOT NULL DEFAULT 0,
  `contribution` INT NOT NULL DEFAULT 0,
  `status` VARCHAR(32) NOT NULL DEFAULT 'active',
  `last_login_at` DATETIME NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS `space` (
  `id` BIGINT PRIMARY KEY,
  `name` VARCHAR(64) NOT NULL,
  `type` VARCHAR(32) NOT NULL DEFAULT 'couple',
  `owner_user_id` BIGINT NOT NULL,
  `announcement` VARCHAR(255) NULL,
  `cover_url` VARCHAR(255) NULL,
  `current_dress_id` BIGINT NULL,
  `level` INT NOT NULL DEFAULT 1,
  `status` VARCHAR(32) NOT NULL DEFAULT 'active',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  INDEX `idx_space_owner` (`owner_user_id`)
);

CREATE TABLE IF NOT EXISTS `space_member` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `role` VARCHAR(32) NOT NULL DEFAULT 'member',
  `display_name` VARCHAR(64) NULL,
  `status` VARCHAR(32) NOT NULL DEFAULT 'active',
  `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY `uk_space_user` (`space_id`, `user_id`),
  INDEX `idx_member_user` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `space_invite` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `invite_code` VARCHAR(32) NOT NULL UNIQUE,
  `created_by` BIGINT NOT NULL,
  `expire_at` DATETIME NULL,
  `max_use_count` INT NOT NULL DEFAULT 1,
  `used_count` INT NOT NULL DEFAULT 0,
  `status` VARCHAR(32) NOT NULL DEFAULT 'active',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `service_category` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `name` VARCHAR(64) NOT NULL,
  `description` VARCHAR(255) NULL,
  `icon_url` VARCHAR(255) NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `visible` TINYINT NOT NULL DEFAULT 1,
  `created_by` BIGINT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  INDEX `idx_category_space` (`space_id`, `sort_order`)
);

CREATE TABLE IF NOT EXISTS `service_item` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `category_id` BIGINT NOT NULL,
  `name` VARCHAR(64) NOT NULL,
  `description` VARCHAR(255) NULL,
  `image_url` VARCHAR(255) NULL,
  `tag` VARCHAR(32) NULL,
  `point_cost` INT NOT NULL DEFAULT 0,
  `require_remark` TINYINT NOT NULL DEFAULT 0,
  `require_appoint_time` TINYINT NOT NULL DEFAULT 0,
  `daily_limit` INT NOT NULL DEFAULT 0,
  `cooldown_minutes` INT NOT NULL DEFAULT 0,
  `monthly_sales` INT NOT NULL DEFAULT 0,
  `default_assignee_id` BIGINT NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `status` VARCHAR(32) NOT NULL DEFAULT 'active',
  `created_by` BIGINT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  INDEX `idx_service_space_category` (`space_id`, `category_id`, `sort_order`)
);

CREATE TABLE IF NOT EXISTS `service_order` (
  `id` BIGINT PRIMARY KEY,
  `order_no` VARCHAR(64) NOT NULL UNIQUE,
  `space_id` BIGINT NOT NULL,
  `service_item_id` BIGINT NOT NULL,
  `requester_id` BIGINT NOT NULL,
  `assignee_id` BIGINT NOT NULL,
  `status` VARCHAR(32) NOT NULL DEFAULT 'pending',
  `remark` VARCHAR(500) NULL,
  `appoint_time` DATETIME NULL,
  `accepted_at` DATETIME NULL,
  `completed_at` DATETIME NULL,
  `canceled_at` DATETIME NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  INDEX `idx_order_space_status` (`space_id`, `status`, `created_at`),
  INDEX `idx_order_assignee` (`assignee_id`, `status`)
);

CREATE TABLE IF NOT EXISTS `todo_item` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `description` VARCHAR(500) NULL,
  `creator_id` BIGINT NOT NULL,
  `assignee_id` BIGINT NOT NULL,
  `priority` TINYINT NOT NULL DEFAULT 1,
  `due_time` DATETIME NULL,
  `status` VARCHAR(32) NOT NULL DEFAULT 'pending',
  `completed_at` DATETIME NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  INDEX `idx_todo_space_status` (`space_id`, `status`, `due_time`)
);

CREATE TABLE IF NOT EXISTS `mood_status` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `mood_key` VARCHAR(32) NOT NULL,
  `mood_label` VARCHAR(32) NOT NULL,
  `note` VARCHAR(255) NULL,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_mood_space_user` (`space_id`, `user_id`)
);

CREATE TABLE IF NOT EXISTS `wish_item` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `creator_id` BIGINT NOT NULL,
  `claimed_by` BIGINT NULL,
  `title` VARCHAR(100) NOT NULL,
  `description` VARCHAR(500) NULL,
  `type` VARCHAR(32) NOT NULL DEFAULT 'life',
  `status` VARCHAR(32) NOT NULL DEFAULT 'open',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  INDEX `idx_wish_space_status` (`space_id`, `status`)
);

CREATE TABLE IF NOT EXISTS `anniversary` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `name` VARCHAR(64) NOT NULL,
  `date` DATE NOT NULL,
  `important` TINYINT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS `dress_item` (
  `id` BIGINT PRIMARY KEY,
  `name` VARCHAR(64) NOT NULL,
  `type` VARCHAR(32) NOT NULL DEFAULT 'theme',
  `obtain_type` VARCHAR(32) NOT NULL DEFAULT 'free',
  `image_url` VARCHAR(255) NULL,
  `config_json` JSON NULL,
  `price_cent` BIGINT NOT NULL DEFAULT 0,
  `point_price` INT NOT NULL DEFAULT 0,
  `rarity` VARCHAR(32) NOT NULL DEFAULT 'R',
  `status` VARCHAR(32) NOT NULL DEFAULT 'active',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS `space_dress` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `dress_item_id` BIGINT NOT NULL,
  `obtained_by` BIGINT NOT NULL,
  `obtain_source` VARCHAR(32) NOT NULL,
  `active` TINYINT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_space_dress` (`space_id`, `dress_item_id`)
);

CREATE TABLE IF NOT EXISTS `activity` (
  `id` BIGINT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `type` VARCHAR(32) NOT NULL,
  `description` VARCHAR(500) NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `reward_point` INT NOT NULL DEFAULT 0,
  `reward_dress_id` BIGINT NULL,
  `status` VARCHAR(32) NOT NULL DEFAULT 'active',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `checkin_record` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `checkin_date` DATE NOT NULL,
  `continuous_days` INT NOT NULL DEFAULT 1,
  `reward_point` INT NOT NULL DEFAULT 0,
  `reward_dress_id` BIGINT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_checkin_day` (`space_id`, `user_id`, `checkin_date`)
);

CREATE TABLE IF NOT EXISTS `point_record` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `change_value` INT NOT NULL,
  `balance_after` INT NOT NULL,
  `biz_type` VARCHAR(32) NOT NULL,
  `biz_id` BIGINT NULL,
  `remark` VARCHAR(255) NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_point_user` (`user_id`, `created_at`)
);

CREATE TABLE IF NOT EXISTS `product` (
  `id` BIGINT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `type` VARCHAR(32) NOT NULL,
  `target_id` BIGINT NULL,
  `amount_cent` BIGINT NOT NULL DEFAULT 0,
  `status` VARCHAR(32) NOT NULL DEFAULT 'active',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `payment_order` (
  `id` BIGINT PRIMARY KEY,
  `pay_no` VARCHAR(64) NOT NULL UNIQUE,
  `user_id` BIGINT NOT NULL,
  `space_id` BIGINT NOT NULL,
  `product_type` VARCHAR(32) NOT NULL,
  `product_id` BIGINT NOT NULL,
  `amount_cent` BIGINT NOT NULL,
  `status` VARCHAR(32) NOT NULL DEFAULT 'pending',
  `wx_transaction_id` VARCHAR(128) NULL,
  `paid_at` DATETIME NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `message` (
  `id` BIGINT PRIMARY KEY,
  `space_id` BIGINT NOT NULL,
  `receiver_id` BIGINT NOT NULL,
  `sender_id` BIGINT NOT NULL,
  `type` VARCHAR(32) NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `content` VARCHAR(500) NULL,
  `biz_id` BIGINT NULL,
  `read_status` TINYINT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_message_receiver` (`receiver_id`, `read_status`, `created_at`)
);

CREATE TABLE IF NOT EXISTS `audit_record` (
  `id` BIGINT PRIMARY KEY,
  `target_type` VARCHAR(32) NOT NULL,
  `target_id` BIGINT NOT NULL,
  `status` VARCHAR(32) NOT NULL,
  `remark` VARCHAR(255) NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `service_template` (
  `id` BIGINT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(255) NULL,
  `scene` VARCHAR(32) NOT NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `status` VARCHAR(32) NOT NULL DEFAULT 'active',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `service_template_item` (
  `id` BIGINT PRIMARY KEY,
  `template_id` BIGINT NOT NULL,
  `category_name` VARCHAR(64) NOT NULL,
  `service_name` VARCHAR(64) NOT NULL,
  `description` VARCHAR(255) NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_template_item_template` (`template_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `ui_option` (
  `id` BIGINT PRIMARY KEY,
  `group_key` VARCHAR(64) NOT NULL,
  `option_key` VARCHAR(64) NOT NULL,
  `label` VARCHAR(64) NOT NULL,
  `icon` VARCHAR(32) NULL,
  `description` VARCHAR(255) NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `status` VARCHAR(32) NOT NULL DEFAULT 'active',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_ui_option_group_key` (`group_key`, `option_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
