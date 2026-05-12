USE campus_trade;

DELIMITER //
CREATE PROCEDURE add_column_if_missing(IN table_name_param VARCHAR(64), IN column_name_param VARCHAR(64), IN column_definition_param VARCHAR(255))
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = table_name_param
          AND column_name = column_name_param
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE ', table_name_param, ' ADD COLUMN ', column_name_param, ' ', column_definition_param);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

CALL add_column_if_missing('sys_user', 'gender', 'VARCHAR(20) NULL');
CALL add_column_if_missing('sys_user', 'bio', 'VARCHAR(500) NULL');
CALL add_column_if_missing('sys_user', 'school', 'VARCHAR(100) NULL');
CALL add_column_if_missing('sys_user', 'college', 'VARCHAR(100) NULL');
CALL add_column_if_missing('sys_user', 'major', 'VARCHAR(100) NULL');
CALL add_column_if_missing('sys_user', 'grade', 'VARCHAR(50) NULL');
CALL add_column_if_missing('sys_user', 'wechat', 'VARCHAR(50) NULL');
CALL add_column_if_missing('sys_user', 'qq', 'VARCHAR(30) NULL');
CALL add_column_if_missing('sys_user', 'email', 'VARCHAR(100) NULL');
CALL add_column_if_missing('sys_user', 'trade_location', 'VARCHAR(100) NULL');
CALL add_column_if_missing('sys_user', 'trade_methods', 'VARCHAR(100) NULL');
CALL add_column_if_missing('sys_user', 'accept_bargain', 'TINYINT NOT NULL DEFAULT 1');
CALL add_column_if_missing('sys_user', 'online_time', 'VARCHAR(100) NULL');
CALL add_column_if_missing('sys_user', 'phone_public', 'TINYINT NOT NULL DEFAULT 0');
CALL add_column_if_missing('sys_user', 'wechat_public', 'TINYINT NOT NULL DEFAULT 0');

DROP PROCEDURE add_column_if_missing;

CREATE TABLE IF NOT EXISTS favorite_goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_favorite_user_goods (user_id, goods_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_favorite_goods FOREIGN KEY (goods_id) REFERENCES goods(id)
);

CREATE TABLE IF NOT EXISTS goods_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    parent_id BIGINT,
    content VARCHAR(500) NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_goods_message_goods FOREIGN KEY (goods_id) REFERENCES goods(id),
    CONSTRAINT fk_goods_message_sender FOREIGN KEY (sender_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS user_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(255) NOT NULL,
    action_url VARCHAR(255),
    read_flag TINYINT NOT NULL DEFAULT 0,
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_notification_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS goods_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    reported_user_id BIGINT NOT NULL,
    report_type VARCHAR(30) NOT NULL,
    report_reason VARCHAR(100) NOT NULL,
    report_description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    handle_result VARCHAR(30),
    handle_remark VARCHAR(255),
    handler_id BIGINT,
    handle_time DATETIME,
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_goods_report_goods FOREIGN KEY (goods_id) REFERENCES goods(id),
    CONSTRAINT fk_goods_report_reporter FOREIGN KEY (reporter_id) REFERENCES sys_user(id),
    CONSTRAINT fk_goods_report_reported_user FOREIGN KEY (reported_user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS goods_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_goods_image_goods FOREIGN KEY (goods_id) REFERENCES goods(id)
);

CREATE TABLE IF NOT EXISTS goods_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    audit_status VARCHAR(20) NOT NULL,
    audit_remark VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_goods_audit_goods FOREIGN KEY (goods_id) REFERENCES goods(id),
    CONSTRAINT fk_goods_audit_admin FOREIGN KEY (admin_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS featured_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
    sort_order INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS goods_featured_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_id BIGINT NOT NULL,
    featured_category_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_goods_featured (goods_id, featured_category_id),
    CONSTRAINT fk_goods_featured_goods FOREIGN KEY (goods_id) REFERENCES goods(id),
    CONSTRAINT fk_goods_featured_category FOREIGN KEY (featured_category_id) REFERENCES featured_category(id)
);

UPDATE category SET name = '教材书籍', status = 'ENABLED' WHERE sort_order = 1;
UPDATE category SET name = '数码电子', status = 'ENABLED' WHERE sort_order = 2;
UPDATE category SET name = '生活用品', status = 'ENABLED' WHERE sort_order = 3;

INSERT INTO category (name, sort_order, status)
SELECT '教材书籍', 1, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 1);

INSERT INTO category (name, sort_order, status)
SELECT '数码电子', 2, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 2);

INSERT INTO category (name, sort_order, status)
SELECT '生活用品', 3, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 3);

INSERT INTO category (name, sort_order, status)
SELECT '学习办公', 4, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 4);

INSERT INTO category (name, sort_order, status)
SELECT '服饰鞋包', 5, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 5);

INSERT INTO category (name, sort_order, status)
SELECT '运动户外', 6, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 6);

INSERT INTO category (name, sort_order, status)
SELECT '美妆护肤', 7, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 7);

INSERT INTO category (name, sort_order, status)
SELECT '小家电', 8, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 8);

INSERT INTO category (name, sort_order, status)
SELECT '交通工具', 9, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 9);

INSERT INTO category (name, sort_order, status)
SELECT '虚拟物品', 10, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 10);

INSERT INTO category (name, sort_order, status)
SELECT '其他', 11, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE sort_order = 11);

INSERT INTO featured_category (name, sort_order, status)
SELECT '毕业甩卖', 1, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM featured_category WHERE sort_order = 1);

INSERT INTO featured_category (name, sort_order, status)
SELECT '新生必备', 2, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM featured_category WHERE sort_order = 2);

INSERT INTO featured_category (name, sort_order, status)
SELECT '考研专区', 3, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM featured_category WHERE sort_order = 3);

INSERT INTO featured_category (name, sort_order, status)
SELECT '宿舍神器', 4, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM featured_category WHERE sort_order = 4);

INSERT INTO featured_category (name, sort_order, status)
SELECT '代步工具', 5, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM featured_category WHERE sort_order = 5);

INSERT INTO featured_category (name, sort_order, status)
SELECT '社团闲置', 6, 'ENABLED'
WHERE NOT EXISTS (SELECT 1 FROM featured_category WHERE sort_order = 6);

UPDATE sys_user
SET password = '$2a$10$D6HRhfdLHr.agEFco5X4OeR5P9HhokbME5HO3Vxb380Ns5dGqSW1a'
WHERE username IN ('admin', 'student1');

SELECT 'category_count' AS check_name, COUNT(*) AS check_value FROM category
UNION ALL
SELECT 'featured_category_count', COUNT(*) FROM featured_category
UNION ALL
SELECT 'admin_user_count', COUNT(*) FROM sys_user WHERE username = 'admin';