-- MySQL dump 10.13  Distrib 8.0.23, for Linux (x86_64)
--
-- Host: localhost    Database: verbalizzazione_voti
-- ------------------------------------------------------
-- Server version	8.0.23-0ubuntu0.20.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appelli`
--

DROP TABLE IF EXISTS `appelli`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appelli` (
  `id_appello` int NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `id_corso` int NOT NULL,
  PRIMARY KEY (`id_appello`),
  KEY `id_corso_idx` (`id_corso`),
  CONSTRAINT `id_corso_appelli` FOREIGN KEY (`id_corso`) REFERENCES `corsi` (`id_corso`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appelli`
--

LOCK TABLES `appelli` WRITE;
/*!40000 ALTER TABLE `appelli` DISABLE KEYS */;
INSERT INTO `appelli` VALUES (1,'2021-06-01',1),(2,'2021-07-01',1),(3,'2021-08-01',1),(4,'2021-06-02',2),(5,'2021-07-02',2),(6,'2021-08-02',2),(7,'2021-06-03',3),(8,'2021-07-03',3),(9,'2021-08-03',3),(10,'2021-06-04',4),(11,'2021-07-04',4),(12,'2021-06-05',5),(13,'2021-07-05',5),(14,'2021-06-06',6),(15,'2021-07-06',6),(16,'2021-06-07',7),(17,'2021-07-07',7),(18,'2021-06-08',8),(19,'2021-07-08',8),(20,'2021-06-09',9),(21,'2021-07-09',9),(22,'2021-06-10',10),(23,'2021-06-11',11),(24,'2021-06-12',12),(25,'2021-06-13',13),(26,'2021-06-14',14),(27,'2021-06-15',15),(28,'2021-06-16',16),(29,'2021-06-17',17),(30,'2021-06-18',18),(31,'2021-06-19',19),(32,'2021-06-20',20);
/*!40000 ALTER TABLE `appelli` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `corsi`
--

DROP TABLE IF EXISTS `corsi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `corsi` (
  `id_corso` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) NOT NULL,
  `descrizione` text NOT NULL,
  `id_docente` int DEFAULT NULL,
  PRIMARY KEY (`id_corso`),
  KEY `id_docente_idx` (`id_docente`),
  CONSTRAINT `id_docente` FOREIGN KEY (`id_docente`) REFERENCES `docenti` (`id_docente`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `corsi`
--

LOCK TABLES `corsi` WRITE;
/*!40000 ALTER TABLE `corsi` DISABLE KEYS */;
INSERT INTO `corsi` VALUES (1,'diritto_privato','diritto privato ',5),(2,'fisica','fisica punto materiale',4),(3,'maglieria','come fare le magliette',1),(4,'storia_della_moda','dal 1900 in poi',1),(5,'calcolo_numerico','calcolo numerico per ingegneria meccanica',4),(6,'diritto_penale','diritto penale',5),(7,'microeconomia','economia interna alle aziende',6),(8,'macroeconomia','studio dei mercati finanziari',6),(9,'patologie_vegetali','studio delle patologie vegetali',3),(10,'entomologia','studio degli insetti',3),(11,'chimica_organica','la chimica del carbonio',3),(12,'fisica_tecnica','termodinamica applicata alle macchine',2),(13,'aereodinamica_del_veicolo','studio dell\'aereodinamica di un veicolo stradale',2),(14,'fonti_rinnovabili','studio dello stato dell\'arte delle fonti rinnovabili',7),(15,'macchine_nucleari','analisi del funzionamento di una centrale nucleare dal punto di vista energetico',7),(16,'colore','analisi della resa cromatica sui vari materiali utilizzati per la costruzione di capi di abbigliamento',1),(17,'diritto_commericale','diritto commerciale',5),(18,'alimentazione_animale','studio dell\'alimentazione dei suini',3),(19,'managment','tecniche di managment nelle grandi aziende',6),(20,'fluido_dinamica','studio della fluidodinamica',4);
/*!40000 ALTER TABLE `corsi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `docenti`
--

DROP TABLE IF EXISTS `docenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `docenti` (
  `id_docente` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) NOT NULL,
  `cognome` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`id_docente`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `docenti`
--

LOCK TABLES `docenti` WRITE;
/*!40000 ALTER TABLE `docenti` DISABLE KEYS */;
INSERT INTO `docenti` VALUES (1,'Nicola','Pochi','nicola.pochi','password'),(2,'Federico','Amadori','federico.amadori','password'),(3,'Pietro','Crivelli','pietro.crivelli','password'),(4,'Gianluca','Carossino','gianluca.carossino','password'),(5,'Giada','Teneggi','giada.teneggi','password'),(6,'Gloria','Arpino','gloria.arpino','password'),(7,'Lorenzo','Morando','lorenzo.morando','password');
/*!40000 ALTER TABLE `docenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iscritti_appello`
--

DROP TABLE IF EXISTS `iscritti_appello`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iscritti_appello` (
  `id_iscritti_appello` int NOT NULL AUTO_INCREMENT,
  `voto` int DEFAULT NULL,
  `stato` varchar(45) NOT NULL,
  `matricola` int NOT NULL,
  `id_appello` int NOT NULL,
  PRIMARY KEY (`id_iscritti_appello`),
  KEY `matricola_iscritto_idx` (`matricola`),
  KEY `appello_iscritto_idx` (`id_appello`),
  CONSTRAINT `appello_iscritto` FOREIGN KEY (`id_appello`) REFERENCES `appelli` (`id_appello`),
  CONSTRAINT `matricola_iscritto` FOREIGN KEY (`matricola`) REFERENCES `studenti` (`matricola`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iscritti_appello`
--

LOCK TABLES `iscritti_appello` WRITE;
/*!40000 ALTER TABLE `iscritti_appello` DISABLE KEYS */;
INSERT INTO `iscritti_appello` VALUES (1,-15,'non inserito',90000,7),(2,-15,'non inserito',90000,8),(3,-15,'non inserito',90000,9),(4,-15,'non inserito',90000,10),(5,-15,'non inserito',90000,28),(6,-15,'non inserito',90001,16),(7,-15,'non inserito',90001,17),(8,-15,'non inserito',90001,18),(9,-15,'non inserito',90001,19),(10,-15,'non inserito',90001,2),(11,-15,'non inserito',90002,2),(12,-15,'non inserito',90002,29),(13,-15,'non inserito',90002,14),(14,-15,'non inserito',90002,15),(15,-15,'non inserito',90002,16),(16,-15,'non inserito',90003,4),(17,-15,'non inserito',90003,5),(18,-15,'non inserito',90003,24),(19,-15,'non inserito',90003,25),(20,-15,'non inserito',90003,13),(21,-15,'non inserito',90004,10),(22,-15,'non inserito',90004,11),(23,-15,'non inserito',90004,28),(24,-15,'non inserito',90004,31),(25,-15,'non inserito',90004,17),(26,-15,'non inserito',90005,32),(27,-15,'non inserito',90005,26),(28,-15,'non inserito',90005,27),(29,-15,'non inserito',90005,4),(30,-15,'non inserito',90005,5),(31,-15,'non inserito',90006,20),(32,-15,'non inserito',90006,21),(33,-15,'non inserito',90006,22),(34,-15,'non inserito',90006,23),(35,-15,'non inserito',90007,1),(36,-15,'non inserito',90007,2),(37,-15,'non inserito',90007,3),(38,-15,'non inserito',90007,29),(39,-15,'non inserito',90007,31),(40,-15,'non inserito',90007,19),(41,-15,'non inserito',90008,20),(42,-15,'non inserito',90008,21),(43,-15,'non inserito',90008,22),(44,-15,'non inserito',90008,23),(45,-15,'non inserito',90009,4),(46,-15,'non inserito',90009,5),(47,-15,'non inserito',90009,12),(48,-15,'non inserito',90009,13),(49,-15,'non inserito',90009,24),(50,-15,'non inserito',90009,27),(51,-15,'non inserito',90010,14),(52,-15,'non inserito',90010,15),(53,-15,'non inserito',90010,29),(54,-15,'non inserito',90010,2),(55,-15,'non inserito',90010,3);
/*!40000 ALTER TABLE `iscritti_appello` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iscritti_corso`
--

DROP TABLE IF EXISTS `iscritti_corso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iscritti_corso` (
  `id_iscritti_corso` int NOT NULL AUTO_INCREMENT,
  `matricola` int NOT NULL,
  `id_corso` int NOT NULL,
  PRIMARY KEY (`id_iscritti_corso`),
  KEY `id_corso_idx` (`id_corso`),
  KEY `matricola_idx` (`matricola`),
  CONSTRAINT `id_corso` FOREIGN KEY (`id_corso`) REFERENCES `corsi` (`id_corso`),
  CONSTRAINT `matricola` FOREIGN KEY (`matricola`) REFERENCES `studenti` (`matricola`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iscritti_corso`
--

LOCK TABLES `iscritti_corso` WRITE;
/*!40000 ALTER TABLE `iscritti_corso` DISABLE KEYS */;
INSERT INTO `iscritti_corso` VALUES (1,90000,3),(2,90000,4),(3,90000,16),(4,90001,7),(5,90001,8),(6,90001,1),(7,90002,1),(8,90002,6),(9,90002,17),(10,90002,7),(11,90002,8),(12,90003,2),(13,90003,5),(14,90003,12),(15,90003,13),(16,90003,7),(17,90004,3),(18,90004,4),(19,90004,16),(20,90004,7),(21,90004,19),(22,90004,8),(23,90005,20),(24,90005,15),(25,90005,14),(26,90006,9),(27,90006,10),(28,90006,11),(29,90007,1),(30,90007,19),(31,90007,17),(32,90007,7),(33,90007,8),(34,90007,4),(35,90008,9),(36,90008,10),(37,90008,11),(38,90009,2),(39,90009,5),(40,90009,12),(41,90009,14),(42,90009,15),(43,90010,1),(44,90010,6),(45,90010,17),(46,90003,14),(47,90003,15),(48,90005,2),(49,90005,5),(50,90005,13);
/*!40000 ALTER TABLE `iscritti_corso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iscritti_verbalizzati`
--

DROP TABLE IF EXISTS `iscritti_verbalizzati`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iscritti_verbalizzati` (
  `id_iscritto_verbalizzato` int NOT NULL AUTO_INCREMENT,
  `id_verbale` int NOT NULL,
  `matricola` int NOT NULL,
  PRIMARY KEY (`id_iscritto_verbalizzato`),
  KEY `id_verbale_verbalizzati_idx` (`id_verbale`),
  KEY `matricola_verbalizzati_idx` (`matricola`),
  CONSTRAINT `id_verbale_verbalizzati` FOREIGN KEY (`id_verbale`) REFERENCES `verbali` (`id_verbale`),
  CONSTRAINT `matricola_verbalizzati` FOREIGN KEY (`matricola`) REFERENCES `studenti` (`matricola`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iscritti_verbalizzati`
--

LOCK TABLES `iscritti_verbalizzati` WRITE;
/*!40000 ALTER TABLE `iscritti_verbalizzati` DISABLE KEYS */;
/*!40000 ALTER TABLE `iscritti_verbalizzati` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studenti`
--

DROP TABLE IF EXISTS `studenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `studenti` (
  `matricola` int NOT NULL,
  `nome` varchar(45) NOT NULL,
  `cognome` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `corso_laurea` varchar(45) NOT NULL,
  PRIMARY KEY (`matricola`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studenti`
--

LOCK TABLES `studenti` WRITE;
/*!40000 ALTER TABLE `studenti` DISABLE KEYS */;
INSERT INTO `studenti` VALUES (90000,'margherita','musumeci','password','design_moda'),(90001,'bruno','morelli','password','economia'),(90002,'martina','magliani','password','giurisprudenza'),(90003,'matteo','nunziante','password','ingegneria_meccanica'),(90004,'ilaria','muratori','password','design_moda'),(90005,'sofia','martellozzo','password','ingegneria_energetica'),(90006,'alberto','mosconi','password','agraria'),(90007,'tommaso','lucarelli','password','economia'),(90008,'giacomo','lombardo','password','agraria'),(90009,'samuele','messineo','password','ingegneria_meccanica'),(90010,'elisa','motta','password','giurisprudenza');
/*!40000 ALTER TABLE `studenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verbali`
--

DROP TABLE IF EXISTS `verbali`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verbali` (
  `id_verbale` int NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `ora` time NOT NULL,
  `id_appello` int NOT NULL,
  PRIMARY KEY (`id_verbale`),
  KEY `id_appello_verbale_idx` (`id_appello`),
  CONSTRAINT `id_appello_verbale` FOREIGN KEY (`id_appello`) REFERENCES `appelli` (`id_appello`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verbali`
--

LOCK TABLES `verbali` WRITE;
/*!40000 ALTER TABLE `verbali` DISABLE KEYS */;
/*!40000 ALTER TABLE `verbali` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-04-16 14:23:02
