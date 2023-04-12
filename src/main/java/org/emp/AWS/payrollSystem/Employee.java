package org.emp.AWS.payrollSystem;
import com.google.gson.Gson;

public class Employee {
    private String employeeName;
    private int employeeId;
    private char workType;
    private double wage, wageAfterTax;

    public Employee(String json){
        Gson gson = new Gson();
        Employee tempEmployee = gson.fromJson(json, Employee.class);
        this.employeeName = tempEmployee.employeeName;
        this.employeeId = tempEmployee.employeeId;
        this.workType = tempEmployee.workType;
        this.wage = tempEmployee.wage;
        calculateWageAfterTax();
    }

    //for returning employee details into string
    public String toString (){
        return new Gson().toJson(this);
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
        return wageAfterTax;
    }

    //Calculating wage according to worktype
    public void calculateWageAfterTax() {
        int tax;

        //For the Temporary employees tac is 15%
        if (workType == 'T'){

            //tax is in %
            tax = 15;
        }
        //For the Contract employees tac is 15%
        else if(workType == 'C'){

            //tax is in %
            tax = 18;
        }
        //For the FullTime employees tac is 15%
        else{

            //tax is in %
            tax = 30;
        }

        wageAfterTax = wage - ((wage * tax)/100);
//        (w * (100 - t) / 100);


    }

    
}
