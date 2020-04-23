
INSERT INTO thread (`id`, `category`, `access`, `title`, `date`) VALUES ('2', 'prog', 'public', 'Hello guys', '2018-03-29 13:31:00');
INSERT INTO thread (`id`, `category`, `access`, `title`, `date`) VALUES ('1', 'prog', 'public', 'Hello guys', '2018-03-29 13:30:00');
INSERT INTO thread (`id`, `category`, `access`, `title`, `date`) VALUES ('3', 'food', 'private', 'I am a test', '2018-03-29 13:33:00');
INSERT INTO thread (`id`, `category`, `access`, `title`, `date`) VALUES ('4', 'IT', 'public', 'I am a test', '2018-03-29 13:32:00');
INSERT INTO thread (`id`, `category`, `access`, `title`, `date`) VALUES ('40', 'IT', 'public', 'I am a test', '2018-03-29 13:35:00');
INSERT INTO thread (`id`, `category`, `access`, `title`, `date`) VALUES ('41', 'IT', 'public', 'I am a test', '2018-03-29 13:34:00');
INSERT INTO thread (`id`, `category`, `access`, `title`, `date`) VALUES ('5', 'IT', 'public', 'I am a test', '2018-03-29 13:34:00');

INSERT INTO message (`id`, `thread_id`, `user`, `text`, `date`) VALUES ('1', '2', 'u', 'Nimic', '2018-03-29 13:31:00');
INSERT INTO message (`id`, `thread_id`, `user`, `text`, `date`) VALUES ('100', '2', 'u', 'Nimic', '2018-03-29 13:32:00');
INSERT INTO message (`id`, `thread_id`, `user`, `text`, `date`) VALUES ('2', '2', 'u', 'Nimic', '2018-03-29 13:33:00');
INSERT INTO message (`id`, `thread_id`, `user`, `text`, `date`) VALUES ('3', '40', 'u', 'Nimic', '2018-03-29 13:30:00');
INSERT INTO message (`id`, `thread_id`, `user`, `text`, `date`) VALUES ('4', '41', 'u', 'Nimic', '2019-03-29 13:30:00');
INSERT INTO message (`id`, `thread_id`, `user`, `text`, `date`) VALUES ('101', '4', 'u', 'Nimic', '2018-03-29 13:34:00');
INSERT INTO message (`id`, `thread_id`, `user`, `text`, `date`) VALUES ('102', '5', 'u', 'Nimic', '2018-03-29 13:34:00');
INSERT INTO message (`id`, `thread_id`, `user`, `text`, `date`) VALUES ('103', '4', 'username', 'a', '2018-03-29 13:35:00');
INSERT INTO message (`id`, `thread_id`, `user`, `text`, `date`) VALUES ('104', '4', 'username', 'z', '2018-03-29 13:36:00');
INSERT INTO image (`id`, `message_id`, `img_blob`) VALUES ('1', '1', '');
INSERT INTO image (`id`, `message_id`, `img_blob`) VALUES ('100', '1', '');
INSERT INTO image (`id`, `message_id`, `img_blob`) VALUES ('101', '1', '');
INSERT INTO tag(`id`, `tag_name`) VALUES ('1', 'fruits');
INSERT INTO tag(`id`, `tag_name`) VALUES ('2', 'computer');
INSERT INTO tag(`id`, `tag_name`) VALUES ('3', 'book');
INSERT INTO tag(`id`, `tag_name`) VALUES ('4', 'school');
INSERT INTO thread_tag(`thread_id`, `tag_id`) VALUES ('2', '3');
INSERT INTO thread_tag(`thread_id`, `tag_id`) VALUES ('2', '4');
INSERT INTO thread_tag(`thread_id`, `tag_id`) VALUES ('3', '3');
INSERT INTO thread_tag(`thread_id`, `tag_id`) VALUES ('3', '2');
INSERT INTO thread_tag(`thread_id`, `tag_id`) VALUES ('3', '4');
INSERT INTO thread_tag(`thread_id`, `tag_id`) VALUES ('4', '2');
INSERT INTO thread_tag(`thread_id`, `tag_id`) VALUES ('4', '4');

INSERT INTO rating (`message_id`, `user`, `type`) VALUES ('101', 'u', 'upVote');
INSERT INTO rating (`message_id`, `user`, `type`) VALUES ('101', 'username', 'upVote');
INSERT INTO rating (`message_id`, `user`, `type`) VALUES ('103', 'u', 'upVote');
INSERT INTO rating (`message_id`, `user`, `type`) VALUES ('103', 'username', 'downVote');
INSERT INTO rating (`message_id`, `user`, `type`) VALUES ('104', 'u', 'downVote');
INSERT INTO rating (`message_id`, `user`, `type`) VALUES ('104', 'username', 'downVote');





INSERT INTO rating(`message_id`, `user`, `type`) VALUES ('1', 'u','upVote');
INSERT INTO rating(`message_id`, `user`, `type`) VALUES ('2', 'u','upVote');