CREATE TABLE question_correction
(
  `id_user`       VARCHAR(45)         NOT NULL,
  `id_question`   INT         NOT NULL,
  `question_text` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_user`, `id_question`),
  CONSTRAINT `id_question_correction_constraint`
    FOREIGN KEY (`id_question`)
      REFERENCES question (`id`)
      ON DELETE CASCADE
);

