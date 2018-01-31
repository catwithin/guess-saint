/* SAINTS table */


select * from saints;
select * from saints_default;
select * from saints_ru;
select * from saints_full;
select * from imgs;
select * from paintings;


/*
UPDATE imgs SET SaintId="25" WHERE Pic="magi3";
UPDATE saints SET icon="john" WHERE category="magi";

DELETE from saints WHERE 1=1;
*/

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

CREATE TABLE paintings (
  _id INTEGER PRIMARY KEY,
  saintId INTEGER,
  fileName TEXT NOT NULL,
  name TEXT,
  author TEXT,
  wikiLink TEXT,
  explanation TEXT
);

/* insert basic info to saints*/
INSERT INTO saints (_id, paintings, icon,  gender, category)
SELECT saints_full.Id, group_concat(imgs.Pic), saints_full.Ico_name, saints_full.Gender, saints_full.category
FROM
saints_full LEFT JOIN imgs on saints_full.Id = imgs.SaintId
GROUP BY Id
;

/* insert default=eng translation to table saints_default */
INSERT INTO saints_default (translation_id, name, description, attributes, wikiLink)
SELECT saints_full.Id, saints_full.Name, saints_full.Description || char(10)  || saints_full.Attributes_descr, saints_full.Attributes_list, saints_full.Wikilink
FROM
saints_full LEFT JOIN imgs on saints_full.Id = imgs.SaintId
GROUP BY Id
;

/* insert rus translation to table saints_ru */
INSERT INTO saints_ru (translation_id, name, description, attributes, wikiLink)
SELECT saints_full.Id, saints_full.Name_rus, saints_full.Description_rus || char(10)  || saints_full.Attributes_descr_rus, saints_full.Attributes_list_rus, saints_full.Wikilink_rus
FROM
saints_full LEFT JOIN imgs on saints_full.Id = imgs.SaintId
GROUP BY Id
;

DROP table saints_full;
DROP table imgs;

/*
DROP table saints;

SELECT group_concat(name)
FROM (SELECT name
FROM saints_default
ORDER BY name ASC)select * from saints;
select * from saints_default;
select * from saints_ru;
select * from saints_full;
select * from imgs;


/*
UPDATE imgs SET SaintId="25" WHERE Pic="magi3";
UPDATE saints SET icon="john" WHERE category="magi";

DELETE from saints WHERE 1=1;
*/

ALTER TABLE saints RENAME TO saints_full;

ALTER TABLE saints_full ADD COLUMN category TEXT;
UPDATE saints_full SET category="magi" WHERE Level_Achievement="Magi";
UPDATE saints_full SET category="ordinary" WHERE NOT Level_Achievement="Magi";

/*create tables for storing saints info*/
CREATE TABLE saints (
  _id INTEGER PRIMARY KEY,
  paintings TEXT NOT NULL,
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

/* insert basic info to saints*/
INSERT INTO saints (_id, paintings, icon,  gender, category)
SELECT saints_full.Id, group_concat(imgs.Pic), saints_full.Ico_name, saints_full.Gender, saints_full.category
FROM
saints_full LEFT JOIN imgs on saints_full.Id = imgs.SaintId
GROUP BY Id
;

/* insert default=eng translation to table saints_default */
INSERT INTO saints_default (translation_id, name, description, attributes, wikiLink)
SELECT saints_full.Id, saints_full.Name, saints_full.Description || char(10)  || saints_full.Attributes_descr, saints_full.Attributes_list, saints_full.Wikilink
FROM
saints_full LEFT JOIN imgs on saints_full.Id = imgs.SaintId
GROUP BY Id
;

/* insert rus translation to table saints_ru */
INSERT INTO saints_ru (translation_id, name, description, attributes, wikiLink)
SELECT saints_full.Id, saints_full.Name_rus, saints_full.Description_rus || char(10)  || saints_full.Attributes_descr_rus, saints_full.Attributes_list_rus, saints_full.Wikilink_rus
FROM
saints_full LEFT JOIN imselect * from saints;
select * from saints_default;
select * from saints_ru;
select * from saints_full;
select * from imgs;


/*
UPDATE imgs SET SaintId="25" WHERE Pic="magi3";
UPDATE saints SET icon="john" WHERE category="magi";

DELETE from saints WHERE 1=1;
*/

ALTER TABLE saints RENAME TO saints_full;

ALTER TABLE saints_full ADD COLUMN category TEXT;
UPDATE saints_full SET category="magi" WHERE Level_Achievement="Magi";
UPDATE saints_full SET category="ordinary" WHERE NOT Level_Achievement="Magi";

/*create tables for storing saints info*/
CREATE TABLE saints (
  _id INTEGER PRIMARY KEY,
  paintings TEXT NOT NULL,
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

/* insert basic info to saints*/
INSERT INTO saints (_id, paintings, icon,  gender, category)
SELECT saints_full.Id, group_concat(imgs.Pic), saints_full.Ico_name, saints_full.Gender, saints_full.category
FROM
saints_full LEFT JOIN imgs on saints_full.Id = imgs.SaintId
GROUP BY Id
;

/* insert default=eng translation to table saints_default */
INSERT INTO saints_default (translation_id, name, description, attributes, wikiLink)
SELECT saints_full.Id, saints_full.Name, saints_full.Description || char(10)  || saints_full.Attributes_descr, saints_full.Attributes_list, saints_full.Wikilink
FROM
saints_full LEFT JOIN imgs on saints_full.Id = imgs.SaintId
GROUP BY Id
;

/* insert rus translation to table saints_ru */
INSERT INTO saints_ru (translation_id, name, description, attributes, wikiLink)
SELECT saints_full.Id, saints_full.Name_rus, saints_full.Description_rus || char(10)  || saints_full.Attributes_descr_rus, saints_full.Attributes_list_rus, saints_full.Wikilink_rus
FROM
saints_full LEFT JOIN imgs ogs o
;

SELECT group_concat(name)
FROM (SELECT name
FROM saints_ru
ORDER BY name ASC)
;
*/
