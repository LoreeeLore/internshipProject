CREATE TABLE question_rating
(
  `id_question` INT     NOT NULL,
  `id_user`     VARCHAR(45)     NOT NULL,
  `rate`        TINYINT NOT NULL,
  PRIMARY KEY (`id_question`, `id_user`),
  CONSTRAINT `id_question_rating`
    FOREIGN KEY (`id_question`)
      REFERENCES question (`id`)
      ON DELETE CASCADE
);
