CREATE TABLE play_quiz
(
  id         INT          NOT NULL AUTO_INCREMENT,
  id_user    VARCHAR(45)          NOT NULL,
  id_quiz    INT          NOT NULL,
  start_time TIMESTAMP(6) NULL,
  end_time   TIMESTAMP(6) NULL,
  rate       DOUBLE       NULL,
  status VARCHAR(45) NULL,
  PRIMARY KEY (id),
  CONSTRAINT `quiz_constraint`
    FOREIGN KEY (`id_quiz`)
      REFERENCES quiz (`id`)
      ON DELETE CASCADE
);
