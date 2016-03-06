# --- !Ups

create table "CAT" ("NAME" VARCHAR NOT NULL PRIMARY KEY,"COLOR" VARCHAR NOT NULL);

INSERT INTO "CAT" ("NAME","COLOR") VALUES ('1','1');
INSERT INTO "CAT" ("NAME","COLOR") VALUES ('2','2');

# --- !Downs

drop table "CAT";
