package com.workmotion.hr.entity;

import javax.persistence.*;

@Entity
@Table(name = "EMPLOYEES")
public class Employee {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "contract_information")
    private String contractInformation;

    @Column(name = "title")
    private String title;

    @Column(name = "state")
    private String state;

    @Column(name = "security_check_state")
    private String securityCheckState;

    @Column(name = "work_permit_state")
    private String workPermitState;

    public Employee() {
    }

    public Employee(String state) {
        this.setState(state);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContractInformation() {
        return contractInformation;
    }

    public void setContractInformation(String contractInformation) {
        this.contractInformation = contractInformation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSecurityCheckState() {
        return securityCheckState;
    }

    public void setSecurityCheckState(String securityCheckState) {
        this.securityCheckState = securityCheckState;
    }

    public String getWorkPermitState() {
        return workPermitState;
    }

    public void setWorkPermitState(String workPermitState) {
        this.workPermitState = workPermitState;
    }
}
