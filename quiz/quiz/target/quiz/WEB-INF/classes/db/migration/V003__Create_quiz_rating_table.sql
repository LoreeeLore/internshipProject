CREATE TABLE quiz_rating
(
  `id_quiz` INT     NOT NULL,
  `id_user` VARCHAR(45)     NOT NULL,
  `rate`    TINYINT NOT NULL,
  PRIMARY KEY (`id_quiz`, `id_user`),
  CONSTRAINT `id_quiz_rating`
    FOREIGN KEY (`id_quiz`)
      REFERENCES quiz (`id`)
      ON DELETE CASCADE
);
