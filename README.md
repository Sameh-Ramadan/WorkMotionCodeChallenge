# WorkMotionCodeChallenge

1) Create the database table using the following Script

CREATE TABLE WORKMOTION.	`EMPLOYEES` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `contract_information` varchar(45) DEFAULT NULL,
  `title` varchar(45) DEFAULT NULL,
  `state` varchar(45) DEFAULT NULL,
  `security_check_state` varchar(45) DEFAULT NULL,
  `work_permit_state` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
