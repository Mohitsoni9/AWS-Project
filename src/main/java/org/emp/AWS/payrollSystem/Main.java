package org.emp.AWS.payrollSystem;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        char option;

        while(true){

            System.out.println("Do you want to register another employee (Y/N)?");
            option = sc.nextLine().toUpperCase().charAt(0);

            if (option == 'Y'){

                //Taking Employee details
                Employee employee = EmployeeEntryManagement.setEmployeeDetails(sc);
                EmployeeEntryManagement.setEmployees(employee);
                System.out.println("Employee’s wage after tax:" + employee.getWageAfterTax());
                System.out.println("***************************************");
            }
            else if (option == 'N') {

                //Printing the Employee's details
                EmployeeEntryManagement.getEmployeeDetails();
                break;
            }
            else{
                System.out.println("Wrong Input: Enter again");
            }
        }
    }
}