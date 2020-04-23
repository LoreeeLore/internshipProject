CREATE TABLE tag
(
  `id`          INT         NOT NULL AUTO_INCREMENT,
  `id_question` INT         NOT NULL,
  `text`        VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `id_question_tag`
    FOREIGN KEY (`id_question`)
      REFERENCES question (`id`)
      ON DELETE CASCADE
);
