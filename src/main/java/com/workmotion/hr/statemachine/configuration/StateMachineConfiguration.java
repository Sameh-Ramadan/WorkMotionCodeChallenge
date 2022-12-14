package com.workmotion.hr.statemachine.configuration;

import com.workmotion.hr.statemachine.events.EmployeeEvents;
import com.workmotion.hr.statemachine.states.EmployeeStates;
import com.workmotion.hr.statemachine.states.SecurityCheckStates;
import com.workmotion.hr.statemachine.states.WorkPermitCheckStates;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfiguration extends StateMachineConfigurerAdapter<String,String>  {

    @Override
    public void configure(StateMachineTransitionConfigurer<String,String> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(EmployeeStates.ADDED)
                    .target(EmployeeStates.IN_CHECK)
                    .event(EmployeeEvents.BEGIN_CHECK)
                .and()
                .withExternal()
                    .source(SecurityCheckStates.SECURITY_CHECK_STARTED)
                    .target(SecurityCheckStates.SECURITY_CHECK_FINISHED)
                    .event(EmployeeEvents.FINISH_SECURITY_CHECK)
                .and()
                .withExternal()
                    .source(WorkPermitCheckStates.WORK_PERMIT_CHECK_STARTED)
                    .target(WorkPermitCheckStates.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
                    .event(EmployeeEvents.COMPLETE_INITIAL_WORK_PERMIT_CHECK)
                .and()
                .withExternal()
                    .source(WorkPermitCheckStates.WORK_PERMIT_CHECK_STARTED)
                    .target(WorkPermitCheckStates.WORK_PERMIT_CHECK_FINISHED)
                    .event(EmployeeEvents.FINISH_WORK_PERMIT_CHECK)
                .and()
                .withExternal()
                    .source(EmployeeStates.APPROVED)
                    .target(EmployeeStates.ACTIVE)
                    .event(EmployeeEvents.ACTIVATE);
    }


    @Override
    public void configure(StateMachineStateConfigurer<String,String> states) throws Exception {
                states
                    .withStates()
                    .initial(EmployeeStates.ADDED)
                    .state(EmployeeStates.IN_CHECK)
                .and()
                    .withStates()
                    .parent(EmployeeStates.IN_CHECK)
                    .initial(SecurityCheckStates.SECURITY_CHECK_STARTED)
                    .end(SecurityCheckStates.SECURITY_CHECK_FINISHED)
                .and()
                    .withStates()
                    .parent(EmployeeStates.IN_CHECK)
                    .initial(WorkPermitCheckStates.WORK_PERMIT_CHECK_STARTED)
                    .state(WorkPermitCheckStates.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
                    .end(WorkPermitCheckStates.WORK_PERMIT_CHECK_FINISHED)
                .and()
                    .withStates()
                    .state(EmployeeStates.APPROVED)
                    .end(EmployeeStates.ACTIVE);
    }

}
