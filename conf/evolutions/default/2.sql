-- !Ups

ALTER TABLE character_slots
RENAME TO hero_slots;

-- !Downs

ALTER TABLE hero_slots
RENAME TO character_slots;