CREATE TABLE `question_suggestion`
(
  `id`              int(11)     NOT NULL AUTO_INCREMENT,
  `id_user`         VARCHAR(45)     NOT NULL,
  `description`     varchar(45) NOT NULL,
  `image`           blob        DEFAULT NULL,
  `category`        varchar(45) DEFAULT NULL,
  `type`            varchar(45) DEFAULT NULL,
  `difficulty`      varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
