-- Host: 127.0.0.1
-- Generation Time: Dec 15, 2016 at 07:17 AM

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cluster`
--

-- --------------------------------------------------------

--
-- Table structure for table `active_alarms`
--

CREATE TABLE `active_alarms` (
  `id` int(11) NOT NULL,
  `alarm_id` int(11) NOT NULL,
  `node_group_id` bigint(11) NOT NULL DEFAULT '0',
  `last_active` bigint(11) NOT NULL DEFAULT '0',
  `state` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `alarms`
--

CREATE TABLE `alarms` (
  `id` int(20) NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `alarm_conditions_id` int(11) NOT NULL DEFAULT '1',
  `trigger_type` int(11) NOT NULL DEFAULT '0',
  `target_type` bigint(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `alarms`
--

INSERT INTO `alarms` (`id`, `type`, `alarm_conditions_id`, `trigger_type`, `target_type`) VALUES
(1, 0, 1, 3, 1),
(2, 0, 3, 1, 2),
(3, 1, 4, 0, 2);

-- --------------------------------------------------------

--
-- Table structure for table `alarm_conditions`
--

CREATE TABLE `alarm_conditions` (
  `id` int(11) NOT NULL,
  `metric` int(11) NOT NULL DEFAULT '3',
  `unit` int(11) NOT NULL DEFAULT '0',
  `threshold` double NOT NULL DEFAULT '0',
  `comparator` int(11) NOT NULL DEFAULT '2'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `alarm_conditions`
--

INSERT INTO `alarm_conditions` (`id`, `metric`, `unit`, `threshold`, `comparator`) VALUES
(1, 3, 0, 60, 2),
(2, 3, 0, 0, 0),
(3, 3, 0, 60, 2),
(4, 3, 0, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `alarm_target`
--

CREATE TABLE `alarm_target` (
  `id` bigint(20) NOT NULL,
  `target_type` int(2) NOT NULL DEFAULT '1' COMMENT 'node or nodegroup',
  `target_sub_type` int(2) NOT NULL DEFAULT '0' COMMENT 'Node type / NodeGroup type (enum)',
  `aux_data` text COMMENT 'Additional data dump'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `alarm_target`
--

INSERT INTO `alarm_target` (`id`, `target_type`, `target_sub_type`, `aux_data`) VALUES
(1, 1, 3, NULL),
(2, 1, 2, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `autoscalelog`
--

CREATE TABLE `autoscalelog` (
  `id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  `alarm_trigger_type` int(11) DEFAULT '1',
  `scale_policy` varchar(64) DEFAULT NULL,
  `timestamp` bigint(20) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `crons`
--

CREATE TABLE `crons` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `host` varchar(100) DEFAULT NULL,
  `start_time` bigint(20) NOT NULL DEFAULT '0',
  `end_time` bigint(20) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `group_statistics`
--

CREATE TABLE `group_statistics` (
  `id` bigint(20) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  `target_type` int(11) NOT NULL,
  `net_capacity_count` bigint(20) NOT NULL,
  `net_load_count` bigint(20) NOT NULL,
  `created` bigint(20) NOT NULL DEFAULT '0',
  `updated` bigint(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `managers`
--

CREATE TABLE `managers` (
  `id` int(11) NOT NULL,
  `host` varchar(50) NOT NULL,
  `last_ping` bigint(20) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `node`
--

CREATE TABLE `node` (
  `id` bigint(20) NOT NULL,
  `host` varchar(150) NOT NULL,
  `state` int(11) DEFAULT '0',
  `type` int(11) DEFAULT '0',
  `name` varchar(150) DEFAULT NULL,
  `node_platform_id` varchar(150) DEFAULT NULL,
  `region` varchar(64) DEFAULT NULL,
  `launchTime` bigint(20) NOT NULL DEFAULT '0',
  `group_id` bigint(20) NOT NULL,
  `created` bigint(20) NOT NULL DEFAULT '-1',
  `updated` bigint(20) NOT NULL DEFAULT '-1',
  `state_updated` bigint(20) NOT NULL DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `node_group`
--

CREATE TABLE `node_group` (
  `id` bigint(20) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `originConnections` int(8) NOT NULL,
  `regions` text,
  `launch_config` varchar(64) DEFAULT NULL,
  `scale_policy` varchar(64) DEFAULT NULL,
  `type` int(11) DEFAULT '0',
  `state` int(11) DEFAULT '0',
  `created` bigint(20) NOT NULL DEFAULT '-1',
  `updated` bigint(20) NOT NULL DEFAULT '-1',
  `state_updated` bigint(20) NOT NULL DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `node_info`
--

CREATE TABLE `node_info` (
  `info_id` bigint(20) NOT NULL,
  `node_id` bigint(20) DEFAULT NULL,
  `client_count` int(11) DEFAULT '0',
  `publisher_count` int(11) NOT NULL DEFAULT '0',
  `origins` text,
  `edges` text,
  `connection_capacity` int(11) DEFAULT NULL,
  `extended_client_count` int(11) NOT NULL DEFAULT '0' COMMENT 'origin specific',
  `last_traffic_time` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Node dirty flag',
  `last_ping` bigint(20) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `stream_info`
--

CREATE TABLE `stream_info` (
  `id` bigint(20) NOT NULL,
  `node_id` bigint(20) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `description` text,
  `scope` varchar(64) DEFAULT NULL,
  `current_subscribers` int(11) DEFAULT '0',
  `start_time` bigint(20) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `vod_stream_info`
--

CREATE TABLE `vod_stream_info` (
  `id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `type` varchar(5) NOT NULL,
  `scope` varchar(64) NOT NULL,
  `current_subscribers` int(11) NOT NULL,
  `last_update` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `active_alarms`
--
ALTER TABLE `active_alarms`
  ADD PRIMARY KEY (`id`),
  ADD KEY `alarm_id` (`alarm_id`),
  ADD KEY `node_group_id` (`node_group_id`);

--
-- Indexes for table `alarms`
--
ALTER TABLE `alarms`
  ADD PRIMARY KEY (`id`),
  ADD KEY `alarm_conditions_id` (`alarm_conditions_id`),
  ADD KEY `target_type` (`target_type`);

--
-- Indexes for table `alarm_conditions`
--
ALTER TABLE `alarm_conditions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `alarm_target`
--
ALTER TABLE `alarm_target`
  ADD PRIMARY KEY (`id`),
  ADD KEY `target_type` (`target_type`);

--
-- Indexes for table `autoscalelog`
--
ALTER TABLE `autoscalelog`
  ADD PRIMARY KEY (`id`),
  ADD KEY `group_id_index` (`group_id`);

--
-- Indexes for table `crons`
--
ALTER TABLE `crons`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `group_statistics`
--
ALTER TABLE `group_statistics`
  ADD PRIMARY KEY (`id`),
  ADD KEY `group_id` (`group_id`);

--
-- Indexes for table `managers`
--
ALTER TABLE `managers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `host` (`host`);

--
-- Indexes for table `node`
--
ALTER TABLE `node`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `host_UNIQUE` (`host`),
  ADD UNIQUE KEY `name_UNIQUE` (`name`),
  ADD KEY `group_id_INDEX` (`group_id`);

--
-- Indexes for table `node_group`
--
ALTER TABLE `node_group`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name_UNIQUE` (`name`);

--
-- Indexes for table `node_info`
--
ALTER TABLE `node_info`
  ADD PRIMARY KEY (`info_id`),
  ADD UNIQUE KEY `node_id_UNIQUE` (`node_id`),
  ADD KEY `node_id` (`node_id`);

--
-- Indexes for table `stream_info`
--
ALTER TABLE `stream_info`
  ADD PRIMARY KEY (`id`),
  ADD KEY `node_id` (`node_id`);

--
-- Indexes for table `vod_stream_info`
--
ALTER TABLE `vod_stream_info`
  ADD PRIMARY KEY (`id`),
  ADD KEY `scope` (`scope`),
  ADD KEY `type` (`type`),
  ADD KEY `name` (`name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `active_alarms`
--
ALTER TABLE `active_alarms`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `alarms`
--
ALTER TABLE `alarms`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `alarm_conditions`
--
ALTER TABLE `alarm_conditions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `alarm_target`
--
ALTER TABLE `alarm_target`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `autoscalelog`
--
ALTER TABLE `autoscalelog`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `crons`
--
ALTER TABLE `crons`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `group_statistics`
--
ALTER TABLE `group_statistics`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `managers`
--
ALTER TABLE `managers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `node`
--
ALTER TABLE `node`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `node_group`
--
ALTER TABLE `node_group`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `node_info`
--
ALTER TABLE `node_info`
  MODIFY `info_id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `stream_info`
--
ALTER TABLE `stream_info`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `vod_stream_info`
--
ALTER TABLE `vod_stream_info`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `active_alarms`
--
ALTER TABLE `active_alarms`
  ADD CONSTRAINT `active_alarms_ibfk_1` FOREIGN KEY (`alarm_id`) REFERENCES `alarms` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `active_alarms_ibfk_2` FOREIGN KEY (`node_group_id`) REFERENCES `node_group` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `alarms`
--
ALTER TABLE `alarms`
  ADD CONSTRAINT `alarms_ibfk_1` FOREIGN KEY (`alarm_conditions_id`) REFERENCES `alarm_conditions` (`id`),
  ADD CONSTRAINT `alarms_ibfk_2` FOREIGN KEY (`target_type`) REFERENCES `alarm_target` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `autoscalelog`
--
ALTER TABLE `autoscalelog`
  ADD CONSTRAINT `autoscalelog_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `node_group` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `group_statistics`
--
ALTER TABLE `group_statistics`
  ADD CONSTRAINT `group_statistics_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `node_group` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `node`
--
ALTER TABLE `node`
  ADD CONSTRAINT `node_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `node_group` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `node_info`
--
ALTER TABLE `node_info`
  ADD CONSTRAINT `node_info_ibfk_1` FOREIGN KEY (`node_id`) REFERENCES `node` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `stream_info`
--
ALTER TABLE `stream_info`
  ADD CONSTRAINT `stream_info_ibfk_1` FOREIGN KEY (`node_id`) REFERENCES `node` (`id`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
