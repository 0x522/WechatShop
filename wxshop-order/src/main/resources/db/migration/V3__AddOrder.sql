INSERT INTO `order`.`ORDER_TABLE` (ID, USER_ID, TOTAL_PRICE, ADDRESS, EXPRESS_COMPANY, EXPRESS_ID, STATUS, SHOP_ID)
VALUES (2, 1, 700, '火星', null, null, 'pending', 1);

INSERT INTO `order`.ORDER_GOODS(GOODS_ID, ORDER_ID, NUMBER)
VALUES (1, 2, 3),
       (2, 2, 4);