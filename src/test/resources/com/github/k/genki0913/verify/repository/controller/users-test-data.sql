-- ユーザーテーブルに存在するデータを削除する
DELETE FROM users;

-- ユーザーテーブルにテストデータを挿入
INSERT INTO users (name, email) VALUES ('山田太郎', 'taro@example.com');
INSERT INTO users (name, email) VALUES ('山田花子', 'hanako@example.com');
INSERT INTO users (name, email) VALUES ('佐藤次郎', 'jiro@example.com');