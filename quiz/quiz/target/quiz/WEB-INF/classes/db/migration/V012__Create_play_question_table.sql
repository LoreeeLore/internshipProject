CREATE TABLE play_question
(
  id           INT         NOT NULL AUTO_INCREMENT,
  id_question  INT         NOT NULL,
  id_play_quiz INT         NOT NULL,
  start_time   TIMESTAMP(6)     NULL,
  end_time     TIMESTAMP(6)     NULL,
  is_correct   TINYINT     NULL,
  PRIMARY KEY (id),
  CONSTRAINT `play_quiz_constraint`
    FOREIGN KEY (`id_play_quiz`)
      REFERENCES play_quiz (`id`)
      ON DELETE CASCADE,
  CONSTRAINT `question_constraint`
    FOREIGN KEY (`id_question`)
      REFERENCES question (`id`)
      ON DELETE CASCADE
);
