-- phpMyAdmin SQL Dump
-- version 4.2.12deb2+deb8u2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Ott 19, 2016 alle 18:43
-- Versione del server: 5.5.52-0+deb8u1
-- PHP Version: 5.6.24-0+deb8u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `bechini`
--
CREATE DATABASE IF NOT EXISTS `bechini` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `bechini`;

-- --------------------------------------------------------

--
-- Struttura della tabella `character`
--

DROP TABLE IF EXISTS `character`;
CREATE TABLE IF NOT EXISTS `character` (
  `paragraph` int(11) NOT NULL,
`id` int(11) NOT NULL,
  `idx` decimal(65,30) NOT NULL,
  `value` varchar(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `files`
--

DROP TABLE IF EXISTS `files`;
CREATE TABLE IF NOT EXISTS `files` (
`id` int(11) NOT NULL,
  `project` int(11) NOT NULL,
  `name` varchar(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `files`
--

INSERT INTO `files` (`id`, `project`, `name`) VALUES
(1, 1, '0'),
(4, 1, '0'),
(5, 1, 'demo');

-- --------------------------------------------------------

--
-- Struttura della tabella `paragraph`
--

DROP TABLE IF EXISTS `paragraph`;
CREATE TABLE IF NOT EXISTS `paragraph` (
`id` int(11) NOT NULL,
  `file` int(11) NOT NULL,
  `idx` double NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `paragraph`
--

INSERT INTO `paragraph` (`id`, `file`, `idx`) VALUES
(40, 1, 0),
(27, 5, 0),
(33, 5, 2),
(34, 5, 3),
(35, 5, 4),
(36, 5, 3.5),
(39, 5, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `projects`
--

DROP TABLE IF EXISTS `projects`;
CREATE TABLE IF NOT EXISTS `projects` (
`id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `projects`
--

INSERT INTO `projects` (`id`, `name`) VALUES
(1, 'demo');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `character`
--
ALTER TABLE `character`
 ADD PRIMARY KEY (`paragraph`,`id`), ADD KEY `id` (`id`), ADD KEY `idx` (`idx`);

--
-- Indexes for table `files`
--
ALTER TABLE `files`
 ADD PRIMARY KEY (`id`,`project`), ADD UNIQUE KEY `id_2` (`id`), ADD KEY `name` (`name`), ADD KEY `project` (`project`);

--
-- Indexes for table `paragraph`
--
ALTER TABLE `paragraph`
 ADD PRIMARY KEY (`id`,`idx`), ADD UNIQUE KEY `id` (`id`), ADD KEY `subindex` (`idx`), ADD KEY `file` (`file`);

--
-- Indexes for table `projects`
--
ALTER TABLE `projects`
 ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `character`
--
ALTER TABLE `character`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `files`
--
ALTER TABLE `files`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `paragraph`
--
ALTER TABLE `paragraph`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=41;
--
-- AUTO_INCREMENT for table `projects`
--
ALTER TABLE `projects`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `character`
--
ALTER TABLE `character`
ADD CONSTRAINT `character_ibfk_1` FOREIGN KEY (`paragraph`) REFERENCES `paragraph` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `files`
--
ALTER TABLE `files`
ADD CONSTRAINT `files_ibfk_1` FOREIGN KEY (`project`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `paragraph`
--
ALTER TABLE `paragraph`
ADD CONSTRAINT `paragraph_ibfk_1` FOREIGN KEY (`file`) REFERENCES `files` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
