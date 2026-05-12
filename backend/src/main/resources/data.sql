INSERT INTO sys_user (username, password, nickname, phone, role, status)
SELECT 'admin', '$2a$10$0w0sD0jEOJdD0a8mN8LJAeTq0zD2R4aW9S8J1Q4M8XH0vGxZt2Y1e', '管理员', '13800000000', 'ADMIN', 'NORMAL'
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

INSERT INTO sys_user (username, password, nickname, phone, role, status)
SELECT 'student1', '$2a$10$0w0sD0jEOJdD0a8mN8LJAeTq0zD2R4aW9S8J1Q4M8XH0vGxZt2Y1e', '学生用户', '13900000000', 'USER', 'NORMAL'
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'student1');

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

INSERT INTO goods (seller_id, category_id, title, price, description, cover_image, status, stock)
SELECT 2, 1, '高数教材', 25.00, '九成新，适合大一学生', '/uploads/sample-book.png', 'ON_SALE', 1
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE title = '高数教材');

INSERT INTO goods (seller_id, category_id, title, price, description, cover_image, status, stock)
SELECT 2, 2, '二手耳机', 68.00, '功能正常，成色较好', '/uploads/sample-headphone.png', 'ON_SALE', 1
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE title = '二手耳机');