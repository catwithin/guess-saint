
/* generate update statements (!CHANGE ids) */
/* statements to insert new saints */
SELECT 'INSERT INTO saints (_id, icon, gender, category) VALUES (' ||
                _id ||
                ', "' ||
				icon ||
                '", ' ||
                gender ||
                ', "' ||
                category ||
                '");'
FROM saints
WHERE _id > 26;

/* statements to insert new saints_default */
SELECT 'INSERT INTO saints_default (translation_id, name, description, attributes, wikiLink) VALUES (' ||
                translation_id ||
                ', "' ||
				name ||
                '", "' ||
                description ||
                '", "' ||
                attributes ||
				'", "' ||
                wikiLink ||
                '");'
FROM saints_default
WHERE translation_id > 26;

/* statements to insert new saints_ru */
SELECT 'INSERT INTO saints_ru (translation_id, name, description, attributes, wikiLink) VALUES (' ||
                translation_id ||
                ', "' ||
				name ||
                '", "' ||
                description ||
                '", "' ||
                attributes ||
				'", "' ||
                wikiLink ||
                '");'
FROM saints_ru
WHERE translation_id > 26;

/* statements to insert new paintings */
SELECT 'INSERT INTO paintings (_id, saintId, fileName, name, correctCount, wikiLink) VALUES (' ||
                _id ||
                ', ' ||
				saintId ||
                ', "' ||
                fileName ||
                '", "' ||
                name ||
				'", ' ||
                correctCount ||
				', "' ||
                wikiLink ||
                '");'
FROM paintings
WHERE _id > 187;
