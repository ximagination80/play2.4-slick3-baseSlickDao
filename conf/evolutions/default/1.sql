# --- !Ups

create table "CAT" ("ID" INT PRIMARY KEY AUTO_INCREMENT,"COLOR" VARCHAR NOT NULL);

INSERT INTO "CAT" ("COLOR") VALUES ('1');
INSERT INTO "CAT" ("COLOR") VALUES ('2');

# --- !Downs

drop table "CAT";
