/* SAINTS table */


--select * from saints;
--select * from saints_default;
--select * from saints_ru;
--select * from saints_full;
--select * from imgs;
--select * from paintings;

--DELETE from saints WHERE 1=1;
--DROP table saints;
--DROP table paintings;
--
--UPDATE imgs SET SaintId="25" WHERE Pic="magi3";
--UPDATE saints SET icon="john" WHERE category="magi";
--
--SELECT group_concat(name)
--FROM (SELECT name
--FROM saints_default
--ORDER BY name ASC)select * from saints;


ALTER TABLE saints RENAME TO saints_full;

ALTER TABLE saints_full ADD COLUMN category TEXT;
UPDATE saints_full SET category="magi" WHERE Level_Achievement="Magi";
UPDATE saints_full SET category="ordinary" WHERE NOT Level_Achievement="Magi";

/*create tables for storing saints info*/
CREATE TABLE saints (
  _id INTEGER PRIMARY KEY,
  icon TEXT NOT NULL,
  gender INTEGER,
  category TEXT
);

CREATE TABLE saints_default (
  translation_id INTEGER PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  attributes TEXT NOT NULL,
  wikiLink TEXT NOT NULL
);

CREATE TABLE saints_ru (
  translation_id INTEGER PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  attributes TEXT NOT NULL,
  wikiLink TEXT NOT NULL
);

/* create paintings table */
CREATE TABLE paintings (
  _id INTEGER PRIMARY KEY,
  saintId INTEGER,
  fileName TEXT NOT NULL,
  name TEXT NOT NULL,
  correctCount INTEGER,
  wikiLink TEXT
);

/* insert basic info to saints*/
INSERT INTO saints (_id, icon, gender, category)
SELECT Id, Ico_name, Gender, category
FROM
saints_full
;

/* insert default=eng translation to table saints_default */
INSERT INTO saints_default (translation_id, name, description, attributes, wikiLink)
SELECT Id, Name, Description || char(10)  || Attributes_descr, Attributes_list, Wikilink
FROM
saints_full
;

/* insert rus translation to table saints_ru */
INSERT INTO saints_ru (translation_id, name, description, attributes, wikiLink)
SELECT Id, Name_rus, Description_rus || char(10)  || Attributes_descr_rus, Attributes_list_rus, Wikilink_rus
FROM
saints_full
;

/* insert info about paintings */
INSERT INTO paintings (saintId, fileName, name, Wikilink, correctCount)
SELECT SaintId, Pic, Name_of_pic, Wikilink_to_aboutpainting, 0
FROM
imgs
;

DROP table saints_full;
DROP table imgs;
