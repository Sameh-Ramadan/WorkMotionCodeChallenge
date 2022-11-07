package com.workmotion.hr.controller;

import com.workmotion.hr.service.EmployeeService;
import com.workmotion.hr.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable("id") int id) {
        Employee employee = employeeService.findEmployeeById(id);
        return employee;
    }

    @PostMapping("/add/{name}")
    public Employee addEmployee(@PathVariable("name") String name) {
        Employee employee = employeeService.addEmployee(name);
        return employee;
    }

    @PostMapping("/add")
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @PutMapping("/begin-check/{id}")
    public Employee beginCheck(@PathVariable("id") int id) {
        Employee employee = employeeService.findEmployeeById(id);
        StateMachine<String, String> stateMachine = employeeService.beginCheck(employee.getId());

        // System.out.println("after calling beginCheck(): " + stateMachine.getState().getIds().toString());

        // System.out.println("employee: " + employeeService.findEmployeeById(employee.getId()).getId());
        return employee;
    }

    @PutMapping("/in-check/{id}")
    public Employee inCheck(@PathVariable("id") int id,
                            @RequestParam(name = "securityCheck") String securityCheck,
                            @RequestParam(name = "workPermitCheck") String workPermitCheck) {
        Employee employee = employeeService.findEmployeeById(id);
        StateMachine<String, String> stateMachine = employeeService.inCheck(employee.getId(), securityCheck, workPermitCheck);

        // System.out.println("after calling inCheck(): " + stateMachine.getState().getIds().toString());

        // System.out.println("employee: " + employeeService.findEmployeeById(employee.getId()).getId());
        return employee;
    }


    @PutMapping("/activate/{id}")
    public Employee activate(@PathVariable("id") int id) {
        Employee employee = employeeService.findEmployeeById(id);
        StateMachine<String, String> stateMachine = employeeService.activate(employee.getId());
        System.out.println("after calling activate(): " + stateMachine.getState().getId().toString());
        System.out.println("employee: " + employeeService.findEmployeeById(employee.getId()).getId());
        return employee;
    }
}
