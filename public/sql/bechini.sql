-- phpMyAdmin SQL Dump
-- version 4.2.12deb2+deb8u2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Ott 18, 2016 alle 16:35
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
  `idx` double NOT NULL,
  `value` varchar(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `character`
--

INSERT INTO `character` (`paragraph`, `idx`, `value`) VALUES
(33, 0, ' '),
(33, 1, ' '),
(33, 2, ' '),
(33, 3, ' '),
(33, 4, 'p'),
(33, 5, 'r'),
(33, 6, 'i'),
(33, 7, 'n'),
(33, 8, 't'),
(33, 9, 'f'),
(33, 10, '('),
(33, 11, '"'),
(33, 12, 'H'),
(33, 13, 'E'),
(33, 14, 'L'),
(33, 15, 'L'),
(33, 16, 'O'),
(33, 17, ' '),
(33, 18, 'S'),
(33, 19, 'T'),
(33, 20, 'E'),
(33, 21, 'A'),
(33, 22, '\\'),
(33, 23, 'n'),
(33, 24, '"'),
(33, 25, ')'),
(33, 26, ';'),
(36, 0, ' '),
(36, 1, ' '),
(36, 2, ' '),
(36, 3, ' '),
(36, 4, 'r'),
(36, 5, 'e'),
(36, 6, 't'),
(36, 7, 'u'),
(36, 8, 'r'),
(36, 9, 'n'),
(36, 10, ' '),
(36, 11, '0'),
(36, 12, ';'),
(39, 0, '{'),
(39, 1, 'o'),
(39, 2, 'i'),
(39, 3, 'd'),
(39, 4, ' '),
(39, 5, 'm'),
(39, 6, 'a'),
(39, 7, 'i'),
(39, 8, 'n'),
(39, 9, '('),
(39, 10, 'v'),
(39, 11, 'o'),
(39, 12, 'i'),
(39, 13, 'd'),
(39, 14, ')'),
(39, 15, ' ');

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
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `paragraph`
--

INSERT INTO `paragraph` (`id`, `file`, `idx`) VALUES
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
 ADD PRIMARY KEY (`paragraph`,`idx`), ADD KEY `idx` (`idx`);

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
-- AUTO_INCREMENT for table `files`
--
ALTER TABLE `files`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `paragraph`
--
ALTER TABLE `paragraph`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=40;
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
