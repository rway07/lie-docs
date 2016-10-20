-- phpMyAdmin SQL Dump
-- version 4.2.12deb2+deb8u2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Ott 20, 2016 alle 01:15
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
  `idx` double NOT NULL,
  `value` varchar(1) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1159 DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `character`
--

INSERT INTO `character` (`paragraph`, `id`, `idx`, `value`) VALUES
(92, 911, 1, '#'),
(92, 912, 2, 'i'),
(92, 913, 3, 'n'),
(92, 914, 4, 'c'),
(92, 915, 5, 'l'),
(92, 916, 6, 'u'),
(92, 917, 7, 'd'),
(92, 918, 8, 'e'),
(92, 919, 9, ' '),
(92, 920, 10, '"'),
(92, 921, 18, '"'),
(92, 922, 11, 's'),
(92, 923, 12, 'o'),
(92, 924, 13, 'm'),
(92, 925, 14, 'm'),
(92, 926, 15, 'a'),
(92, 927, 16, '.'),
(92, 928, 17, 'h'),
(93, 930, 1, '#'),
(93, 931, 2, 'i'),
(93, 932, 3, 'n'),
(93, 933, 4, 'c'),
(93, 934, 5, 'l'),
(93, 935, 6, 'u'),
(93, 936, 7, 'd'),
(93, 937, 8, 'e'),
(93, 938, 9, ' '),
(93, 939, 10, '"'),
(93, 940, 23, '"'),
(93, 941, 11, 'm'),
(93, 942, 12, 'o'),
(93, 943, 13, 'l'),
(93, 944, 14, 't'),
(93, 945, 15, 'i'),
(93, 946, 16, 'p'),
(93, 947, 17, 'l'),
(93, 948, 18, 'i'),
(93, 949, 19, 'c'),
(93, 950, 20, 'a'),
(93, 951, 21, '.'),
(93, 952, 22, 'h'),
(94, 953, 1, '#'),
(94, 954, 2, 'i'),
(94, 955, 3, 'n'),
(94, 956, 4, 'c'),
(94, 957, 5, 'l'),
(94, 958, 6, 'u'),
(94, 959, 7, 'd'),
(94, 960, 8, 'e'),
(94, 961, 9, ' '),
(94, 964, 10, '<'),
(94, 965, 18, '>'),
(94, 966, 11, 's'),
(94, 967, 12, 't'),
(94, 968, 13, 'd'),
(94, 969, 14, 'i'),
(94, 970, 15, 'o'),
(94, 971, 16, '.'),
(94, 972, 17, 'h'),
(95, 1089, 1, '#'),
(95, 1090, 2, 'i'),
(95, 1091, 3, 'n'),
(95, 1092, 4, 'c'),
(95, 1093, 5, 'l'),
(95, 1094, 6, 'u'),
(95, 1095, 7, 'd'),
(95, 1096, 8, 'e'),
(95, 1097, 9, ' '),
(95, 1098, 10, '<'),
(95, 1099, 11, 'u'),
(95, 1100, 12, 'n'),
(95, 1101, 13, 'i'),
(95, 1102, 14, 's'),
(95, 1103, 15, 't'),
(95, 1104, 16, 'd'),
(95, 1105, 17, '.'),
(95, 1106, 18, 'h'),
(95, 1107, 19, '>'),
(96, 973, 1, 'v'),
(96, 974, 2, 'o'),
(96, 975, 3, 'i'),
(96, 976, 4, 'd'),
(96, 977, 5, ' '),
(96, 978, 6, 'm'),
(96, 979, 7, 'a'),
(96, 980, 8, 'i'),
(96, 981, 9, 'n'),
(96, 982, 10, '('),
(96, 983, 11, ')'),
(96, 984, 12, '{'),
(97, 986, 1, ' '),
(97, 987, 2, ' '),
(97, 1003, 3, 'i'),
(97, 1004, 4, 'n'),
(97, 1005, 5, 't'),
(97, 1006, 6, ' '),
(97, 1007, 7, 'c'),
(97, 1009, 8, ' '),
(97, 1010, 9, '='),
(97, 1011, 10, ' '),
(97, 1012, 11, '0'),
(97, 1013, 12, ';'),
(98, 985, 13, '}'),
(99, 998, 3, '}'),
(99, 999, 1, ' '),
(99, 1000, 2, ' '),
(100, 988, 3, 'w'),
(100, 989, 4, 'h'),
(100, 990, 5, 'i'),
(100, 991, 6, 'l'),
(100, 992, 7, 'e'),
(100, 993, 8, '('),
(100, 994, 10, ')'),
(100, 995, 9, '1'),
(100, 997, 11, '{'),
(100, 1001, 1, ' '),
(100, 1002, 2, ' '),
(101, 1014, 1, ' '),
(101, 1015, 2, ' '),
(101, 1016, 3, ' '),
(101, 1017, 4, ' '),
(101, 1018, 5, ' '),
(101, 1020, 6, 'p'),
(101, 1021, 7, 'r'),
(101, 1022, 8, 'i'),
(101, 1023, 9, 'n'),
(101, 1024, 10, 't'),
(101, 1025, 11, 'f'),
(101, 1026, 12, '('),
(101, 1027, 42, ')'),
(101, 1028, 43, ';'),
(101, 1029, 13, '"'),
(101, 1030, 18, '"'),
(101, 1031, 14, '%'),
(101, 1032, 15, 'i'),
(101, 1033, 16, '\\'),
(101, 1034, 17, 'n'),
(101, 1035, 19, ','),
(101, 1036, 20, 's'),
(101, 1037, 21, 'o'),
(101, 1038, 22, 'm'),
(101, 1039, 23, 'm'),
(101, 1040, 24, 'a'),
(101, 1041, 25, '('),
(101, 1042, 41, ')'),
(101, 1043, 26, 'm'),
(101, 1044, 27, 'o'),
(101, 1045, 28, 'l'),
(101, 1046, 29, 't'),
(101, 1047, 30, 'i'),
(101, 1048, 31, 'p'),
(101, 1049, 32, 'l'),
(101, 1050, 33, 'i'),
(101, 1051, 34, 'c'),
(101, 1052, 35, 'a'),
(101, 1053, 36, '('),
(101, 1054, 40, ')'),
(101, 1055, 37, 'c'),
(101, 1056, 38, '+'),
(101, 1057, 39, '+'),
(102, 1058, 1, ' '),
(102, 1059, 2, ' '),
(102, 1060, 3, ' '),
(102, 1061, 4, ' '),
(102, 1062, 5, ' '),
(102, 1064, 6, 'f'),
(102, 1065, 7, 'f'),
(102, 1066, 8, 'l'),
(102, 1067, 9, 'u'),
(102, 1068, 10, 's'),
(102, 1069, 11, 'h'),
(102, 1070, 12, '('),
(102, 1071, 13, ')'),
(102, 1072, 14, ';'),
(103, 1073, 1, ' '),
(103, 1074, 2, ' '),
(103, 1075, 3, ' '),
(103, 1076, 4, ' '),
(103, 1077, 5, ' '),
(103, 1078, 6, 's'),
(103, 1079, 7, 'l'),
(103, 1080, 8, 'e'),
(103, 1081, 9, 'e'),
(103, 1082, 10, 'p'),
(103, 1084, 15, ')'),
(103, 1085, 11, '('),
(103, 1086, 13, ')'),
(103, 1087, 12, '1'),
(103, 1088, 14, ';'),
(105, 1108, 1, 'i'),
(105, 1109, 2, 'n'),
(105, 1110, 3, 't'),
(105, 1111, 4, ' '),
(105, 1112, 5, 's'),
(105, 1113, 6, 'o'),
(105, 1114, 7, 'm'),
(105, 1115, 8, 'm'),
(105, 1116, 9, 'a'),
(105, 1117, 10, '('),
(105, 1119, 11, 'i'),
(105, 1120, 12, 'n'),
(105, 1121, 13, 't'),
(105, 1122, 14, ' '),
(105, 1123, 16, 'c'),
(105, 1124, 15, 'x'),
(105, 1125, 17, ';'),
(106, 1126, 1, 'i'),
(106, 1127, 2, 'n'),
(106, 1128, 3, 't'),
(106, 1129, 4, ' '),
(106, 1130, 5, 's'),
(106, 1131, 6, 'o'),
(106, 1132, 7, 'm'),
(106, 1133, 8, 'm'),
(106, 1134, 9, 'a'),
(106, 1135, 10, '('),
(106, 1136, 16, ')'),
(106, 1137, 11, 'i'),
(106, 1138, 12, 'n'),
(106, 1139, 13, 't'),
(106, 1140, 14, ' '),
(106, 1141, 15, 'x'),
(106, 1142, 17, '{'),
(107, 1144, 1, ' '),
(107, 1145, 2, ' '),
(107, 1146, 3, 'r'),
(107, 1147, 4, 'e'),
(107, 1148, 5, 't'),
(107, 1149, 6, 'u'),
(107, 1150, 7, 'r'),
(107, 1151, 8, 'n'),
(107, 1152, 9, ' '),
(107, 1153, 10, 'x'),
(107, 1154, 11, '+'),
(107, 1157, 12, '1'),
(107, 1158, 13, ';'),
(108, 1143, 18, '}');

-- --------------------------------------------------------

--
-- Struttura della tabella `files`
--

DROP TABLE IF EXISTS `files`;
CREATE TABLE IF NOT EXISTS `files` (
`id` int(11) NOT NULL,
  `project` int(11) NOT NULL,
  `name` varchar(256) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `files`
--

INSERT INTO `files` (`id`, `project`, `name`) VALUES
(7, 2, 'main.c'),
(9, 2, 'somma.c'),
(8, 2, 'somma.h');

-- --------------------------------------------------------

--
-- Struttura della tabella `paragraph`
--

DROP TABLE IF EXISTS `paragraph`;
CREATE TABLE IF NOT EXISTS `paragraph` (
`id` int(11) NOT NULL,
  `file` int(11) NOT NULL,
  `idx` double NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `paragraph`
--

INSERT INTO `paragraph` (`id`, `file`, `idx`) VALUES
(92, 7, 0),
(93, 7, 1),
(94, 7, 2),
(95, 7, 3),
(96, 7, 4),
(97, 7, 5),
(98, 7, 6),
(99, 7, 5.5),
(100, 7, 5.25),
(101, 7, 5.375),
(102, 7, 5.4375),
(103, 7, 5.46875),
(104, 7, 3.5),
(105, 8, 0),
(106, 9, 0),
(107, 9, 1),
(108, 9, 2);

-- --------------------------------------------------------

--
-- Struttura della tabella `projects`
--

DROP TABLE IF EXISTS `projects`;
CREATE TABLE IF NOT EXISTS `projects` (
`id` int(11) NOT NULL,
  `name` varchar(256) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `projects`
--

INSERT INTO `projects` (`id`, `name`) VALUES
(2, 'demo');

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
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1159;
--
-- AUTO_INCREMENT for table `files`
--
ALTER TABLE `files`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT for table `paragraph`
--
ALTER TABLE `paragraph`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=109;
--
-- AUTO_INCREMENT for table `projects`
--
ALTER TABLE `projects`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
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
