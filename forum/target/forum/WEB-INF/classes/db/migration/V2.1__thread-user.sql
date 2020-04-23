/*add user to thread*/
ALTER TABLE thread
ADD COLUMN `user` varchar(100) AFTER `title`;