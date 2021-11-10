-- !Ups

CREATE TABLE items (
    ID SERIAL PRIMARY KEY,
    name varchar(40) NOT NULL,
    description varchar(200) NOT NULL,
    item_type varchar(20) NOT NULL,
    healing_effect INT,
    damage INT,
    defense INT
);

CREATE TABLE inventories (
    ID SERIAL PRIMARY KEY,
    hero_id INT REFERENCES heroes ON DELETE CASCADE,
    item_id INT REFERENCES items ON DELETE CASCADE
);

-- !Downs

DROP TABLE items;
DROP TABLE inventories;