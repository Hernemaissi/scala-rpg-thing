-- !Ups

CREATE TABLE spells (
    ID SERIAL PRIMARY KEY,
    name varchar(40) NOT NULL,
    description varchar(40) NOT NULL,
    mana_cost INT NOT NULL
);

CREATE TABLE damaging_effects (
    ID SERIAL PRIMARY KEY,
    target_type varchar(20) NOT NULL,
    damage INT NOT NULL
);

CREATE TABLE spells_damaging_effects (
    ID SERIAL PRIMARY KEY,
    spell_id INT REFERENCES spells ON DELETE CASCADE,
    damaging_effects_id INT REFERENCES damaging_effects ON DELETE CASCADE
);

CREATE TABLE healing_effects (
    id SERIAL PRIMARY KEY,
    healing INT NOT NULL
);

CREATE TABLE spells_healing_effects (
    ID SERIAL PRIMARY KEY,
    spell_id INT REFERENCES spells ON DELETE CASCADE,
    healing_effects_id INT REFERENCES healing_effects ON DELETE CASCADE
);

CREATE TABLE known_spells (
    ID SERIAL PRIMARY KEY,
    active BOOLEAN DEFAULT false,
    hero_id INT REFERENCES heroes on DELETE CASCADE,
    spell_id INT REFERENCES spells ON DELETE CASCADE
)

-- !Downs

DROP TABLE spells;
DROP TABLE damaging_effects;
DROP TABLE spells_damaging_effects;
DROP TABLE healing_effects;
DROP TABLE spells_healing_effects;
DROP TABLE known_spells;