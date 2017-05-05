SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `spring_test`
--

-- --------------------------------------------------------

--
-- Table structure for table `mydata`
--

DROP TABLE IF EXISTS `mydata`;
CREATE TABLE IF NOT EXISTS `mydata` (
`id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `sample_table`
--

DROP TABLE IF EXISTS `sample_table`;
CREATE TABLE IF NOT EXISTS `sample_table` (
`id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `mydata`
--
ALTER TABLE `mydata`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `sample_table`
--
ALTER TABLE `sample_table`
 ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `mydata`
--
ALTER TABLE `mydata`
MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `sample_table`
--
ALTER TABLE `sample_table`
MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;