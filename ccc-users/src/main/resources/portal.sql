-- MySQL dump 10.13  Distrib 5.5.15, for Linux (x86_64)
--
-- Host: localhost    Database: ccc-portal
-- ------------------------------------------------------
-- Server version	5.5.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `country_id` varchar(255) NOT NULL,
  PRIMARY KEY (`country_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES ('Afghanistan'),('Albania'),('Algeria'),('Andorra'),('Angola'),('Antigua & Deps'),('Argentina'),('Armenia'),('Australia'),('Austria'),('Azerbaijan'),('Bahamas'),('Bahrain'),('Bangladesh'),('Barbados'),('Belarus'),('Belgium'),('Belize'),('Benin'),('Bhutan'),('Bolivia'),('Bosnia Herzegovina'),('Botswana'),('Brazil'),('Brunei'),('Bulgaria'),('Burkina'),('Burundi'),('Cambodia'),('Cameroon'),('Canada'),('Cape Verde'),('Central African Rep'),('Chad'),('Chile'),('China'),('Colombia'),('Comoros'),('Congo'),('Congo {Democratic Rep}'),('Costa Rica'),('Croatia'),('Cuba'),('Cyprus'),('Czech Republic'),('Denmark'),('Djibouti'),('Dominica'),('Dominican Republic'),('East Timor'),('Ecuador'),('Egypt'),('El Salvador'),('Equatorial Guinea'),('Eritrea'),('Estonia'),('Ethiopia'),('Fiji'),('Finland'),('France'),('Gabon'),('Gambia'),('Georgia'),('Germany'),('Ghana'),('Greece'),('Grenada'),('Guatemala'),('Guinea'),('Guinea-Bissau'),('Guyana'),('Haiti'),('Honduras'),('Hungary'),('Iceland'),('India'),('Indonesia'),('Iran'),('Iraq'),('Ireland {Republic}'),('Israel'),('Italy'),('Ivory Coast'),('Jamaica'),('Japan'),('Jordan'),('Kazakhstan'),('Kenya'),('Kiribati'),('Korea North'),('Korea South'),('Kosovo'),('Kuwait'),('Kyrgyzstan'),('Laos'),('Latvia'),('Lebanon'),('Lesotho'),('Liberia'),('Libya'),('Liechtenstein'),('Lithuania'),('Luxembourg'),('Macedonia'),('Madagascar'),('Malawi'),('Malaysia'),('Maldives'),('Mali'),('Malta'),('Marshall Islands'),('Mauritania'),('Mauritius'),('Mexico'),('Micronesia'),('Moldova'),('Monaco'),('Mongolia'),('Montenegro'),('Morocco'),('Mozambique'),('Myanmar, {Burma}'),('Namibia'),('Nauru'),('Nepal'),('Netherlands'),('New Zealand'),('Nicaragua'),('Niger'),('Nigeria'),('Norway'),('Oman'),('Pakistan'),('Palau'),('Panama'),('Papua New Guinea'),('Paraguay'),('Peru'),('Philippines'),('Poland'),('Portugal'),('Qatar'),('Romania'),('Russian Federation'),('Rwanda'),('Saint Vincent & the Grenadines'),('Samoa'),('San Marino'),('Sao Tome & Principe'),('Saudi Arabia'),('Senegal'),('Serbia'),('Seychelles'),('Sierra Leone'),('Singapore'),('Slovakia'),('Slovenia'),('Solomon Islands'),('Somalia'),('South Africa'),('Spain'),('Sri Lanka'),('St Kitts & Nevis'),('St Lucia'),('Sudan'),('Suriname'),('Swaziland'),('Sweden'),('Switzerland'),('Syria'),('Taiwan'),('Tajikistan'),('Tanzania'),('Thailand'),('Togo'),('Tonga'),('Trinidad & Tobago'),('Tunisia'),('Turkey'),('Turkmenistan'),('Tuvalu'),('Uganda'),('Ukraine'),('United Arab Emirates'),('United Kingdom'),('United States'),('Uruguay'),('Uzbekistan'),('Vanuatu'),('Vatican City'),('Venezuela'),('Vietnam'),('Yemen'),('Zambia'),('Zimbabwe');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `group_name` varchar(255) NOT NULL,
  PRIMARY KEY (`group_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
INSERT INTO `groups` VALUES ('test');
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persistent_logins`
--

DROP TABLE IF EXISTS `persistent_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persistent_logins` (
  `username` varchar(255) DEFAULT NULL,
  `series` varchar(500) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persistent_logins`
--

LOCK TABLES `persistent_logins` WRITE;
/*!40000 ALTER TABLE `persistent_logins` DISABLE KEYS */;
/*!40000 ALTER TABLE `persistent_logins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question` (
  `user_name` varchar(60) DEFAULT NULL,
  `question` varchar(300) DEFAULT NULL,
  `answer` varchar(30) DEFAULT NULL,
  KEY `user_name` (`user_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `server`
--

DROP TABLE IF EXISTS `server`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server` (
  `server_id` int(11) NOT NULL AUTO_INCREMENT,
  `server_name` varchar(255) NOT NULL,
  `server_type` varchar(255) NOT NULL,
  `server_address` varchar(255) NOT NULL,
  `port` int(11) NOT NULL,
  `ip_address` varchar(45) NOT NULL,
  PRIMARY KEY (`server_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `server`
--

LOCK TABLES `server` WRITE;
/*!40000 ALTER TABLE `server` DISABLE KEYS */;
/*!40000 ALTER TABLE `server` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `state`
--

DROP TABLE IF EXISTS `state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `state` (
  `state_id` varchar(255) NOT NULL,
  `country_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`state_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `state`
--

LOCK TABLES `state` WRITE;
/*!40000 ALTER TABLE `state` DISABLE KEYS */;
INSERT INTO `state` VALUES ('Alabama','United States'),('Alaska','United States'),('Arizona','United States'),('Arkansas','United States'),('California','United States'),('Colorado','United States'),('Connecticut','United States'),('Delaware','United States'),('Florida','United States'),('Georgia','United States'),('Hawaii','United States'),('Idaho','United States'),('Illinois','United States'),('Indiana','United States'),('Iowa','United States'),('Kansas','United States'),('Kentucky','United States'),('Louisiana','United States'),('Maine','United States'),('Maryland','United States'),('Massachusetts','United States'),('Michigan','United States'),('Minnesota','United States'),('Mississippi','United States'),('Missouri','United States'),('Montana','United States'),('Nebraska','United States'),('Nevada','United States'),('New Hampshire','United States'),('New Jersey','United States'),('New Mexico','United States'),('New York','United States'),('North Carolina','United States'),('North Dakota','United States'),('Ohio','United States'),('Oklahoma','United States'),('Oregon','United States'),('Pennsylvania','United States'),('Rhode Island','United States'),('South Carolina','United States'),('South Dakota','United States'),('Tennessee','United States'),('Texas','United States'),('Utah','United States'),('Vermont','United States'),('Virginia','United States'),('Washington','United States'),('West Virginia','United States'),('Wisconsin','United States'),('Wyoming','United States'),('Aguascalientes','Mexico'),('Baja California Norte','Mexico'),('Baja California Sur','Mexico'),('Campeche','Mexico'),('Chiapas','Mexico'),('Chihuahua','Mexico'),('Coahuila','Mexico'),('Colima','Mexico'),('Distrito Federal','Mexico'),('Durango','Mexico'),('Guanajuato','Mexico'),('Guerrero','Mexico'),('Hidalgo','Mexico'),('Jalisco','Mexico'),('Mexico','Mexico'),('Michoacan','Mexico'),('Morelos','Mexico'),('Nayarit','Mexico'),('Nuevo Leon','Mexico'),('Oaxaca','Mexico'),('Puebla and Vera Cruz','Mexico'),('Queretaro','Mexico'),('Quintana Roo','Mexico'),('San Luis Potosi','Mexico'),('Sinaloa','Mexico'),('Sonora','Mexico'),('Tabasco','Mexico'),('Tamaulipas','Mexico'),('Tlaxcala','Mexico'),('Yucatan','Mexico'),('Zacatecas ','Mexico'),('Alberta','Canada'),('British Columbia','Canada'),('Manitoba','Canada'),('New Brunswick','Canada'),('Newfoundland and Labrador','Canada'),('Nova Scotia','Canada'),('Northwest Territories','Canada'),('Nunavut','Canada'),('Ontario','Canada'),('Prince Edward Island','Canada'),('QuÃƒÂ©bec','Canada'),('Saskatchewan','Canada'),('Yukon','Canada');
/*!40000 ALTER TABLE `state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_name` varchar(255) NOT NULL DEFAULT '',
  `email` varchar(80) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `credentials_expired` bit(1) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `first_name` varchar(60) DEFAULT NULL,
  `last_name` varchar(60) DEFAULT NULL,
  `birthday` varchar(10) DEFAULT NULL,
  `gender` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`user_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('agibson','agibson@clevercloudcomputing.com','destrotroll%5','','\0','906-231-1820','Adam','Gibson',NULL,'m'),('','test@clevercloudcomputing.com','aabcb987e4b425751e210413562e78f776de6285','','\0','906-231-1820','test','test','11/07/89','M');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_alternate_account`
--

DROP TABLE IF EXISTS `user_alternate_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_alternate_account` (
  `alt_user_name` varchar(255) NOT NULL,
  `alt_password` varchar(45) NOT NULL,
  `associated_server` int(11) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password_saved` bit(1) DEFAULT NULL,
  PRIMARY KEY (`alt_user_name`),
  KEY `user_name` (`user_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_alternate_account`
--

LOCK TABLES `user_alternate_account` WRITE;
/*!40000 ALTER TABLE `user_alternate_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_alternate_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_contact`
--

DROP TABLE IF EXISTS `user_contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_contact` (
  `email` varchar(100) NOT NULL DEFAULT '',
  `user_contact_for` varchar(255) NOT NULL DEFAULT '',
  `user_name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`user_contact_for`,`email`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_contact`
--

LOCK TABLES `user_contact` WRITE;
/*!40000 ALTER TABLE `user_contact` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group` (
  `user_name` varchar(255) DEFAULT NULL,
  `group_name` varchar(255) DEFAULT NULL,
  KEY `user_name` (`user_name`),
  KEY `group_name` (`group_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group`
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
INSERT INTO `user_group` VALUES ('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test'),('test','test');
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_settings`
--

DROP TABLE IF EXISTS `user_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_settings` (
  `setting_name` varchar(50) DEFAULT NULL,
  `setting_val` varchar(60) DEFAULT NULL,
  `user_name` varchar(50) DEFAULT NULL,
  KEY `user_name` (`user_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_settings`
--

LOCK TABLES `user_settings` WRITE;
/*!40000 ALTER TABLE `user_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_settings` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-08-03  0:44:13
