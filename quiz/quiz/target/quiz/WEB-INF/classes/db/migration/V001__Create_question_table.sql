CREATE TABLE question
(
  id              INT         NOT NULL AUTO_INCREMENT,
  description     VARCHAR(500) NULL,
  image           BLOB        NULL,
  category        VARCHAR(45) NULL,
  difficulty      VARCHAR(45) NULL,
  is_deprecated   TINYINT     NULL,
  PRIMARY KEY (id)
);
