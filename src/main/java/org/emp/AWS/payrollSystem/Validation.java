package org.emp.AWS.payrollSystem;

public class Validation {
    public static String checkName(String empName) throws InvalidNameException {
//      "[a-zA-Z0-9\\s]+"
//        • Employee name must have at least one space and cannot be less than 5 characters
//        (!empName.contains(" "))
            if ((!empName.contains(" ")) || (!empName.matches("[a-zA-Z0-9\\s]6+"))){
            throw new InvalidNameException("Employee name must have at least one space and cannot be less than 5 characters");
        }
        return empName;
    }
    public static int checkEmployeeId(int empId) throws InvalidIdException {

//        • Employee ID must be a positive integer
        if (empId < 0){
            throw new InvalidIdException("Employee ID must be a positive integer");
        }
        return empId;
    }
    public static double checkWage(double wage, char workType) throws InvalidWageException {
//     To validate Wage the following are considered:
//            • The hourly pay cannot exceed 90.00 but can be 0
//            • The bi-weekly pay cannot be below 1000.00 or more than 3500.00
//            • The monthly pay cannot be less than 3000.00

        if (wage < 0){
            throw new InvalidWageException("Employee wage must be a positive integer");
        }
        else if(workType == 'T' && (wage > 90)){
            throw new InvalidWageException("The hourly pay cannot exceed 90.00 but can be 0!");
        }
        else if(workType == 'C' && (wage < 1000 || wage > 3500)){
            throw new InvalidWageException("The bi-weekly pay cannot be below 1000.00 or more than 3500.00!");
        }
        else if(workType == 'F' && wage < 3000){
            throw new InvalidWageException("The monthly pay cannot be less than 3000.00!");
        }
        return wage;
    }
}
