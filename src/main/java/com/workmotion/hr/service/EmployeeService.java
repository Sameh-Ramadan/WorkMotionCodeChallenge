package com.workmotion.hr.service;

import com.workmotion.hr.entity.Employee;
import com.workmotion.hr.repository.EmployeeRepository;
import com.workmotion.hr.statemachine.events.EmployeeEvents;
import com.workmotion.hr.statemachine.states.EmployeeStates;
import com.workmotion.hr.statemachine.states.SecurityCheckStates;
import com.workmotion.hr.statemachine.states.WorkPermitCheckStates;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StateMachineFactory<String, String> factory;

    private static final String EMPLOYEE_ID_HEADER = "employeeId";

    public EmployeeService(EmployeeRepository employeeRepository, StateMachineFactory<String, String> factory) {
        this.employeeRepository = employeeRepository;
        this.factory = factory;
    }

    public Employee findEmployeeById(int id) {
        return this.employeeRepository.findById(id).get();
    }

    public Employee addEmployee(String name) {
        Employee newEmployee = new Employee(EmployeeStates.ADDED);
        newEmployee.setName(name);
        return this.employeeRepository.save(newEmployee);
    }

    public Employee addEmployee(Employee newEmployee) {
        newEmployee.setState(EmployeeStates.ADDED);
        return this.employeeRepository.save(newEmployee);
    }

    public StateMachine<String, String> beginCheck(Integer employeeId) {
        StateMachine<String, String> sm = this.build(employeeId);

        Message<String> message = MessageBuilder.withPayload(EmployeeEvents.BEGIN_CHECK)
                .setHeader(EMPLOYEE_ID_HEADER, employeeId)
                .build();

        sm.sendEvent(message);

//        List stateIds = (List) sm.getState().getIds();
//        if(stateIds.size() > 1) {
//            Employee employee = this.employeeRepository.findById(employeeId).get();
//            for(int i = 1; i<stateIds.size() ; i++) {
//                String subState = (String) stateIds.get(i);
//                if(subState.equals(SecurityCheckStates.SECURITY_CHECK_STARTED)) {
//                    employee.setSecurityCheckState(subState);
//                } else if(subState.equals(WorkPermitCheckStates.WORK_PERMIT_CHECK_STARTED)) {
//                    employee.setWorkPermitState(subState);
//                }
//            }
//            this.employeeRepository.save(employee);
//        }

        return sm;
    }

    public StateMachine<String, String> inCheck(Integer employeeId,
                                                String securityCheckEvent,
                                                String workPermitCheckEvent) {

        StateMachine<String, String> sm = this.build(employeeId);

        if(securityCheckEvent != null) {
//            Message<String> message = MessageBuilder.withPayload(securityCheckEvent)
//                    .setHeader(EMPLOYEE_ID_HEADER, employeeId)
//                    .build();

            sm.sendEvent(securityCheckEvent);
        }

        // StateMachine<String, String> sm2 = this.build(employeeId);

        if(workPermitCheckEvent != null) {
//            Message<String> message2 = MessageBuilder.withPayload(workPermitCheckEvent)
//                    .setHeader(EMPLOYEE_ID_HEADER, employeeId)
//                    .build();

            sm.sendEvent(workPermitCheckEvent);
        }

        return sm;

    }

    public StateMachine<String, String> activate(Integer employeeId) {
        StateMachine<String, String> sm = this.build(employeeId);

        Message<String> message = MessageBuilder.withPayload(EmployeeEvents.ACTIVATE)
                .setHeader(EMPLOYEE_ID_HEADER, employeeId)
                .build();

        sm.sendEvent(message);

        Employee employee = this.employeeRepository.findById(employeeId).get();
        employee.setState(EmployeeStates.ACTIVE);
        employeeRepository.save(employee);

        return sm;
    }

    private StateMachine<String, String> build(int employeeId) {
        Employee employee = this.employeeRepository.findById(employeeId).get();
        String employeeIdKey = Integer.toString(employee.getId());

        StateMachine<String, String> sm = this.factory.getStateMachine(employeeIdKey);
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {

                    sma.addStateMachineInterceptor(new StateMachineInterceptorAdapter<String, String>() {


                        public StateContext<String, String> postTransition(StateContext<String, String> stateContext) {

                            List<String> stateIds = (List<String>) stateContext.getStateMachine().getState().getIds();
                            System.out.println("postTransition stateContext.stateMachine.getState().getIds() " + stateContext.getStateMachine().getState().getIds().toString());

                            if(stateIds!= null && stateIds.size() > 1) {
                                Employee employee = employeeRepository.findById(employeeId).get();

                                int allChecksFinished = 0;

                                for(int i = 0; i<stateIds.size() ; i++) {
                                    String state = stateIds.get(i);

                                    if(state.startsWith("SECURITY_CHECK")) {
                                        employee.setSecurityCheckState(state);

                                        if(state.equals(SecurityCheckStates.SECURITY_CHECK_FINISHED)) {
                                            allChecksFinished++;
                                        }

                                    } else if(state.startsWith("WORK_PERMIT_CHECK")) {
                                        employee.setWorkPermitState(state);

                                        if(state.equals(WorkPermitCheckStates.WORK_PERMIT_CHECK_FINISHED)) {
                                            allChecksFinished++;
                                        }

                                    }else {
                                        employee.setState(state);
                                    }
                                }
                                if(allChecksFinished == 2){
                                    employee.setState(EmployeeStates.APPROVED);
                                }
                                employeeRepository.save(employee);
                            } else {
                                employee.setState(stateIds.get(0));
                                employeeRepository.save(employee);
                            }

                            System.out.println("======================================================");

                            return stateContext;
                        }

                    });
                    sma.resetStateMachine(new DefaultStateMachineContext<>(employee.getState(), null, null, null));
                });
        sm.start();
        return sm;
    }
}
