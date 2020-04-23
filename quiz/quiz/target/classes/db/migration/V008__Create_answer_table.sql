CREATE TABLE answer
(
  `id`          INT         NOT NULL AUTO_INCREMENT,
  `id_question` INT         NOT NULL,
  `is_correct`  TINYINT     NULL,
  `text`        VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `id_question_answer`
    FOREIGN KEY (`id_question`)
      REFERENCES question (`id`)
      ON DELETE CASCADE
);
