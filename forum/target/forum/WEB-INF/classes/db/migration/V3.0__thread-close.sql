/*add closed/open to thread*/
ALTER TABLE thread
ADD COLUMN `state` varchar(100) AFTER `title`;
