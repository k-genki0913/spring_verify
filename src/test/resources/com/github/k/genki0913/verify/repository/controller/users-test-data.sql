-- ユーザーテーブルに存在するデータを削除する
DELETE FROM users;

-- ユーザーテーブルにテストデータを挿入
INSERT INTO users (id, name, email) VALUES (1, '山田太郎', 'taro@example.com');
INSERT INTO users (id, name, email) VALUES (2, '山田花子', 'hanako@example.com');
INSERT INTO users (id, name, email) VALUES (3, '佐藤次郎', 'jiro@example.com');