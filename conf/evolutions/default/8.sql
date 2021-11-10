-- !Ups

CREATE TABLE merchants (
    ID SERIAL PRIMARY KEY
);

CREATE TABLE merchant_items (
    ID SERIAL PRIMARY KEY,
    merchant_id INT REFERENCES merchants,
    item_id INT REFERENCES items,
    price INT NOT NULL
);

-- !Downs

DROP TABLE merchants;
DROP TABLE merchant_items;