CREATE TABLE question_quiz
(
  `id_quiz`     INT NOT NULL,
  `id_question` INT NOT NULL,
  PRIMARY KEY (`id_quiz`, `id_question`),
  CONSTRAINT `id_question_constraint`
    FOREIGN KEY (`id_question`)
      REFERENCES question (`id`)
      ON DELETE CASCADE,
  CONSTRAINT `id_quiz_constraint`
    FOREIGN KEY (`id_quiz`)
      REFERENCES quiz (`id`)
      ON DELETE CASCADE
);

