-- !Ups

CREATE TABLE heroes (
    ID SERIAL PRIMARY KEY,
    owner   varchar(40) NOT NULL,
    hero_slot_id INT REFERENCES hero_slots ON DELETE CASCADE,
    name varchar(40) NOT NULL,
    hero_class varchar(40) NOT NULL,
    level INT NOT NULL DEFAULT 1,
    exp INT NOT NULL DEFAULT 0,
    gold INT NOT NULL DEFAULT 0,
    max_hp INT NOT NULL,
    max_mana INT NOT NULL,
    strength INT NOT NULL,
    intelligence INT NOT NULL,
    constitution INT NOT NULL,
    mind INT NOT NULL,
    max_active_spells INT NOT NULL

);

-- !Downs

DROP TABLE heroes;