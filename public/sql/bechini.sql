-- phpMyAdmin SQL Dump
-- version 4.2.12deb2+deb8u2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Ott 13, 2016 alle 19:32
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
  `idx` int(11) NOT NULL,
  `subindex` double NOT NULL,
  `value` varchar(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `character`
--

INSERT INTO `character` (`paragraph`, `idx`, `subindex`, `value`) VALUES
(1, 1, 1, 'A'),
(2, 0, 1, 'B'),
(2, 0, 3, 'C');

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
(5, 1, 'fffff');

-- --------------------------------------------------------

--
-- Struttura della tabella `paragraph`
--

DROP TABLE IF EXISTS `paragraph`;
CREATE TABLE IF NOT EXISTS `paragraph` (
`id` int(11) NOT NULL,
  `file` int(11) NOT NULL,
  `idx` int(11) NOT NULL,
  `subindex` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `paragraph`
--

INSERT INTO `paragraph` (`id`, `file`, `idx`, `subindex`) VALUES
(1, 1, 1, 1),
(2, 5, 0, 1),
(4, 5, 0, 7);

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
 ADD PRIMARY KEY (`paragraph`,`idx`,`subindex`), ADD KEY `idx` (`idx`), ADD KEY `subindex` (`subindex`);

--
-- Indexes for table `files`
--
ALTER TABLE `files`
 ADD PRIMARY KEY (`id`,`project`), ADD UNIQUE KEY `id_2` (`id`), ADD KEY `name` (`name`), ADD KEY `project` (`project`);

--
-- Indexes for table `paragraph`
--
ALTER TABLE `paragraph`
 ADD PRIMARY KEY (`id`,`idx`,`subindex`), ADD UNIQUE KEY `id` (`id`), ADD KEY `idx` (`idx`), ADD KEY `subindex` (`subindex`), ADD KEY `file` (`file`);

--
-- Indexes for table `projects`
--
ALTER TABLE `projects`
 ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `files`
--
ALTER TABLE `files`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `paragraph`
--
ALTER TABLE `paragraph`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
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
