package org.emp.AWS.payrollSystem;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class EmployeeEntryManagement {
//    List of employees
    private static ArrayList<Employee> employees = new ArrayList<>();
    private static int temporaryWorkers, contractWorkers, fullTimeWorkers = 0;
    private static double totalWageBeforeTax, totalWageAfterTax = 0;
    public static void getEmployeeDetails(){
        System.out.println("Employee name, employee ID, Work Type, Total Wage before tax, Total Wage after tax");
        for(int i = 0 ; i < employees.size() ; i++){
            Employee employee = employees.get(i);
            System.out.println((i+1)+ ". " + employee.getEmployeeName() + ", " +
                    employee.getEmployeeId() + ", " +
                    employee.getWorkType() + ", " +
                    "$" + employee.getWage()+ ", " +
                    "$" + employee.getWageAfterTax());

//          Counting the number of worker according to the workType.
            char workType = employee.getWorkType();
            switch (workType) {
                case ('T'):
//                    counting the Temporary workers
                        temporaryWorkers++;
                        break;
                case ('C') :
//                    counting Contract workers
                        contractWorkers++;
                        break;
                case ('F') :
//                    counting full-Time workers
                        fullTimeWorkers++;
                break;
            }
//          Adding wage to the total wage
            totalWageBeforeTax += employee.getWage();
            totalWageAfterTax += employee.getWageAfterTax();
        }

//      Final details of the Employees
        System.out.println("Total employees: " + employees.size());
        System.out.println("Work types: (" +
                temporaryWorkers + ") Temporary" + ", (" +
                contractWorkers + ") Contract" + ", (" +
                fullTimeWorkers + ") Full-time");
        System.out.println("Total wages before tax: $" + totalWageBeforeTax);
        System.out.println("Total wages after tax: $" + totalWageAfterTax);
    }
    public static Employee setEmployeeDetails(Scanner sc){
        Employee employee;
        String employeeName;
        int employeeID;
        char workType;
        double wage;

//      Taking Employee name and validating until the name have at least one space and cannot be less than 5 characters.
        System.out.println("Please enter the employee’s name?");
        while(true){
            try {
                employeeName = Validation.checkName(sc.nextLine());
                break;
            } catch (InvalidNameException e) {
                System.err.println(e);
            }
        }

//      Taking Employee's ID and validating that it must be positive value;
        System.out.println("Please enter the employee’s ID?");
        while(true){
            try {
                employeeID = Validation.checkEmployeeId(sc.nextInt());
                break;
            } catch (InvalidIdException e) {
                System.err.println(e);
            } catch (InputMismatchException e){
                System.err.println("Employee ID must be a positive integer");
            } finally {
//              clearing the input
                sc.nextLine();
            }
        }

//      WorkType can be T,C,F.
//      checking the user must input only (T, C, F);
        System.out.println("Please enter the employee’s work type (T,C,F)?");
        while(true){
            workType = sc.nextLine().toUpperCase().charAt(0);
            if (workType == 'T' || workType == 'C'|| workType == 'F'){
                break;
            }
            System.err.println("Enter only T,C,F");
        }

//      Taking Employee's wage according to the workType
        System.out.println("Please enter the employee’s wage?");
        while(true){
            try {
                wage = Validation.checkWage(sc.nextDouble(), workType);
                break;
            } catch (InvalidWageException e) {
                System.err.println(e);
            } catch (InputMismatchException e){
                System.err.println("Employee wage must be a positive integer");
            } finally {
//              for clearing the input.
                sc.nextLine();
            }
        }

        employee = new Employee(employeeName, employeeID, workType, wage);

        return employee;
    }
    public static void setEmployees(Employee employee) {
        employees.add(employee);
    }
}
