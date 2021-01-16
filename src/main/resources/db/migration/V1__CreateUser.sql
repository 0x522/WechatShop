create table user
(
    id         bigint primary key auto_increment,
    name       varchar(100),
    tel        varchar(20) unique,
    avatar_url varchar(1024),
    address    varchar(1024),
    created_at datetime not null default now(),
    updated_at datetime not null default now()
) ENGINE = InnoDB
  DEFAULT CHAR SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

insert into user(id, name, tel, avatar_url, address)
values (1, 'user1', '13800000000', 'http://url', '火星')