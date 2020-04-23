/*add date to thread*/
ALTER TABLE thread
ADD COLUMN `date` DATETIME NOT NULL AFTER `title`;