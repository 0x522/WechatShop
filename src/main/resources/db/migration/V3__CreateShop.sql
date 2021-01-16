create table shop
(
    id            bigint primary key auto_increment,
    name          varchar(100),
    description   varchar(1024),
    img_url       varchar(1024),
    owner_user_id bigint,/*管理员id*/
    status        varchar(16),
    created_at    datetime not null default now(),
    updated_at    datetime not null default now()
) ENGINE = InnoDB
  DEFAULT CHAR SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
INSERT INTO shop (ID, NAME, DESCRIPTION, IMG_URL, OWNER_USER_ID, STATUS)
VALUES (1, 'shop1', 'desc1', 'url1', 1, 'ok');
INSERT INTO shop (ID, NAME, DESCRIPTION, IMG_URL, OWNER_USER_ID, STATUS)
VALUES (2, 'shop2', 'desc2', 'url2', 1, 'ok');