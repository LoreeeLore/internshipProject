CREATE TABLE quiz_access
(
  `id_quiz` INT NOT NULL,
  `id_user` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_quiz`, `id_user`),
  CONSTRAINT `id_quiz_access`
    FOREIGN KEY (`id_quiz`)
      REFERENCES quiz (`id`)
      ON DELETE CASCADE
)
;

