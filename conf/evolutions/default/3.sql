-- !Ups

ALTER TABLE hero_slots
ADD COLUMN ID SERIAL PRIMARY KEY

-- !Downs

ALTER TABLE hero_slots
DROP COLUMN ID