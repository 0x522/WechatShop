ALTER TABLE order.ORDER_TABLE
    ADD COLUMN (SHOP_ID BIGINT);

UPDATE order.ORDER_TABLE
set SHOP_ID = 1
WHERE ID = 1