create table shopping_cart
(
    id         bigint primary key auto_increment,
    user_id    bigint,
    goods_id   bigint,
    number     int,
    status     varchar(16),
    created_at datetime not null default now(),
    updated_at datetime not null default now()
) ENGINE = InnoDB
  DEFAULT CHAR SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
INSERT INTO shopping_cart(USER_ID, GOODS_ID, NUMBER, STATUS)
VALUES (1, 1, 100, 'ok');
INSERT INTO shopping_cart(USER_ID, GOODS_ID, NUMBER, STATUS)
VALUES (1, 4, 200, 'ok');
INSERT INTO shopping_cart(USER_ID, GOODS_ID, NUMBER, STATUS)
VALUES (1, 5, 300, 'ok');