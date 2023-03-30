package org.emp.AWS.payrollSystem;

import com.google.gson.Gson;

public class Employee {
    private String employeeName;
    private int employeeId;
    private char workType;
    private double wage, wageAfterTax;

    public Employee(String employeeName, int employeeId, char workType, double wage){
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.workType = workType;
        this.wage = wage;
    }
    public Employee(String json){
        Gson gson = new Gson();
        Employee tempEmployee = gson.fromJson(json, Employee.class);
        this.employeeName = tempEmployee.employeeName;
        this.employeeId = tempEmployee.employeeId;
        this.workType = tempEmployee.workType;
        this.wage = tempEmployee.wage;
    }

    //Setter methods are for manipulation of the values if needed;
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setWorkType(char workType) {
        this.workType = workType;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    //Getters for accessing the employee details
    public String getEmployeeName() {
        return employeeName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public char getWorkType() {
        return workType;
    }

    public double getWage() {
        return wage;
    }

    public double getWageAfterTax() {
        wageAfterTax = payrollSystem.Payroll.calculateWageAfterTax(wage, workType);
        return wageAfterTax;
    }
}
