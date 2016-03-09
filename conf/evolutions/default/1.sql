# --- !Ups

CREATE TABLE "CAT" (
  "ID"    INT PRIMARY KEY AUTO_INCREMENT,
  "COLOR" VARCHAR NOT NULL
);

CREATE TABLE "DOG" (
  "ID"          INT PRIMARY KEY AUTO_INCREMENT,
  "NAME"        VARCHAR NOT NULL,
  "AGE"         INT     NOT NULL,
  "HATE_CAT_ID" INT     NOT NULL,

  FOREIGN KEY ("HATE_CAT_ID") REFERENCES "CAT" ("ID") ON DELETE RESTRICT ON UPDATE RESTRICT
);

INSERT INTO "CAT" ("COLOR") VALUES ('1');
INSERT INTO "CAT" ("COLOR") VALUES ('2');

INSERT INTO "DOG" ("NAME", "AGE", "HATE_CAT_ID") VALUES ('Dog 1', 10, 1);

# --- !Downs
DROP TABLE "CAT";
DROP TABLE "DOG";
