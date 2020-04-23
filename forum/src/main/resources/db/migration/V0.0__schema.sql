
DROP TABLE IF EXISTS thread;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE thread (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  category varchar(150) NOT NULL,
  access varchar(50) NOT NULL,
  title varchar(150) NOT NULL,
  PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS message;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE message (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  thread_id int(11) NOT NULL,
  user varchar(100) NOT NULL,
  `text` varchar(1000) NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT t_id FOREIGN KEY (thread_id) REFERENCES thread (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);


DROP TABLE IF EXISTS image;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE image (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  img_blob mediumblob NOT NULL,
  message_id int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT mmss_id FOREIGN KEY (message_id) REFERENCES message (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--


--
-- Table structure for table `message`
--


/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--


--
-- Table structure for table `message_tag_user`
--

DROP TABLE IF EXISTS message_tag_user;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE message_tag_user (
  message_id int(11) NOT NULL,
  user varchar(100) NOT NULL,
  PRIMARY KEY (message_id,user),
  CONSTRAINT message_id FOREIGN KEY (message_id) REFERENCES message (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message_tag_user`
--


--
-- Table structure for table `rating`
--

DROP TABLE IF EXISTS rating;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE rating (
  message_id int(11) NOT NULL,
  user varchar(100) NOT NULL,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (message_id,user),
  CONSTRAINT msg_id FOREIGN KEY (message_id) REFERENCES message (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rating`
--


--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS tag;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE tag (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  tag_name varchar(150) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY tag_name_UNIQUE (tag_name)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--


--
-- Table structure for table `thread`
--


/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thread`
--


--
-- Table structure for table `thread_privacy_user`
--

DROP TABLE IF EXISTS thread_privacy_user;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE thread_privacy_user (
  thread_id int(11) NOT NULL,
  user varchar(100) NOT NULL,
  PRIMARY KEY (thread_id,user),
  CONSTRAINT th_id FOREIGN KEY (thread_id) REFERENCES thread (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thread_privacy_user`
--


--
-- Table structure for table `thread_tag`
--

DROP TABLE IF EXISTS thread_tag;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE thread_tag (
  thread_id int(11) NOT NULL,
  tag_id int(11) NOT NULL,
  PRIMARY KEY (thread_id,tag_id),
  CONSTRAINT tag_id FOREIGN KEY (tag_id) REFERENCES tag (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT thread_id FOREIGN KEY (thread_id) REFERENCES thread (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);
/*!40101 SET character_set_client = @saved_cs_client */;




