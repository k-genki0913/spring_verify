-- ユーザーテーブルに存在するデータを削除する
DELETE FROM accounts;

-- アカウントテーブルにテストデータを挿入
INSERT INTO accounts
(id, account_locked, created_at, created_by, last_modified_by, updated_at, email, failed_attempts, lock_time, "name", "password", reset_token, reset_token_expiry, "role", enabled)
VALUES(1, false, '2026-08-16 13:39:06.516', 'system', 'system', '2026-08-16 13:39:06.516', 'test@example.com', 0, NULL, 'テストユーザー', '$2a$10$S4eUOjLAD9VHuVnf80hxnuFXasl8uzg5tDJPe3V5.KrClbOCXVrF.', NULL, NULL, 'ROLE_USER', true);