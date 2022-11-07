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

2) Run the application locally using
      mvn clean install

3) Call the APIs as following

3.1)  Add new employee
     POST http://localhost:9191/employees/add/{employeeName}

3.2) Begin Check
     PUT http://localhost:9191/employees/begin-check/{employeeId}
     
3.3) In-Check
     PUT http://localhost:9191/employees/in-check/{employeeId}?securityCheck=FINISH_SECURITY_CHECK&workPermitCheck=COMPLETE_INITIAL_WORK_PERMIT_CHECK
     PUT http://localhost:9191/employees/in-check/{employeeId}?securityCheck=FINISH_SECURITY_CHECK&workPermitCheck=FINISH_WORK_PERMIT_CHECK
     
3.4) Activate Employee
     PUT http://localhost:9191/employees/activate/{employeeId}
     
3.5) Fetch Employee
    GET http://localhost:9191/employees/{employeeId}
