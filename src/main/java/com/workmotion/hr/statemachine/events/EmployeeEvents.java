package com.workmotion.hr.statemachine.events;

public interface EmployeeEvents {
     String BEGIN_CHECK = "BEGIN_CHECK";
     String FINISH_SECURITY_CHECK = "FINISH_SECURITY_CHECK";
     String COMPLETE_INITIAL_WORK_PERMIT_CHECK = "COMPLETE_INITIAL_WORK_PERMIT_CHECK";
     String FINISH_WORK_PERMIT_CHECK = "FINISH_WORK_PERMIT_CHECK";
     String ACTIVATE = "ACTIVATE";
}
