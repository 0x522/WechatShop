alter table shopping_cart
    add column (shop_id bigint);

delete
from shopping_cart
where user_id = 1;

INSERT INTO shopping_cart(USER_ID, GOODS_ID, SHOP_ID, NUMBER, STATUS)
VALUES (1, 1, 1, 100, 'ok');
INSERT INTO shopping_cart(USER_ID, GOODS_ID, SHOP_ID, NUMBER, STATUS)
VALUES (1, 4, 2, 200, 'ok');
INSERT INTO shopping_cart(USER_ID, GOODS_ID, SHOP_ID, NUMBER, STATUS)
VALUES (1, 5, 2, 300, 'ok');