package com.workmotion.hr.service;

import com.workmotion.hr.entity.Employee;
import com.workmotion.hr.repository.EmployeeRepository;
import com.workmotion.hr.statemachine.events.EmployeeEvents;
import com.workmotion.hr.statemachine.states.EmployeeStates;
import com.workmotion.hr.statemachine.states.SecurityCheckStates;
import com.workmotion.hr.statemachine.states.WorkPermitCheckStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StateMachineFactory<String, String> factory;

    private static final String EMPLOYEE_ID_HEADER = "employeeId";
    private static final String SECURITY_CHECK_PREFIX = "SECURITY_CHECK";
    private static final String WORK_PERMIT_CHECK_PREFIX = "WORK_PERMIT_CHECK";

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
        sm.sendEvent(EmployeeEvents.BEGIN_CHECK);
        return sm;
    }

    public StateMachine<String, String> inCheck(Integer employeeId,
                                                String securityCheckEvent,
                                                String workPermitCheckEvent) {

        StateMachine<String, String> sm = this.build(employeeId);

        if(securityCheckEvent != null) {
            sm.sendEvent(securityCheckEvent);
        }

        if(workPermitCheckEvent != null) {
            sm.sendEvent(workPermitCheckEvent);
        }

        return sm;

    }

    public StateMachine<String, String> activate(Integer employeeId) {
        StateMachine<String, String> sm = this.build(employeeId);
        sm.sendEvent(EmployeeEvents.ACTIVATE);

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

                                    if(state.startsWith(SECURITY_CHECK_PREFIX)) {
                                        employee.setSecurityCheckState(state);

                                        if(state.equals(SecurityCheckStates.SECURITY_CHECK_FINISHED)) {
                                            allChecksFinished++;
                                        }

                                    } else if(state.startsWith(WORK_PERMIT_CHECK_PREFIX)) {
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

                            return stateContext;
                        }

                    });
                    sma.resetStateMachine(new DefaultStateMachineContext<>(employee.getState(), null, null, null));
                });
        sm.start();
        return sm;
    }
}
