create table user
(
    id         bigint primary key auto_increment,
    name       varchar(100),
    tel        varchar(20) unique,
    avatar_url varchar(1024),
    created_at datetime,
    updated_at datetime
)