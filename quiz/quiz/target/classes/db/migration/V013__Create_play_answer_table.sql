CREATE TABLE play_answer
(
  id               INT         NOT NULL AUTO_INCREMENT,
  id_play_question INT         NOT NULL,
  id_answer        INT         NOT NULL,
  text             VARCHAR(45) NULL,
  PRIMARY KEY (id),
  CONSTRAINT `answer_constraint`
    FOREIGN KEY (`id_answer`)
      REFERENCES answer (`id`)
      ON DELETE CASCADE,
  CONSTRAINT `play_question_constraint`
    FOREIGN KEY (`id_play_question`)
      REFERENCES play_question (`id`)
      ON DELETE CASCADE
);
