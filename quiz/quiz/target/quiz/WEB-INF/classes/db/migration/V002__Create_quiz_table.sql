CREATE TABLE quiz
(
  id         INT         NOT NULL AUTO_INCREMENT,
  category   VARCHAR(45) NULL,
  difficulty VARCHAR(45) NULL,
  is_public  TINYINT     NULL,
  rate       DOUBLE      NULL,
  is_random  TINYINT     NULL,
  time_in_minutes BIGINT(20)  NULL,
  PRIMARY KEY (id)
);
