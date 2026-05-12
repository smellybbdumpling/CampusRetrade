USE campus_trade;

UPDATE sys_user
SET password = '$2a$10$D6HRhfdLHr.agEFco5X4OeR5P9HhokbME5HO3Vxb380Ns5dGqSW1a'
WHERE username IN ('admin', 'student1');

SELECT id, username, role, status
FROM sys_user
WHERE username IN ('admin', 'student1');