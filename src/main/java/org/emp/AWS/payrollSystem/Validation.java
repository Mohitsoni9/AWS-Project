package org.emp.AWS.payrollSystem;

public class Validation {

    public static void checkEmployee(Employee employee) throws InvalidEmployeeDetails {

        checkEmployeeId(String .valueOf(employee.getEmployeeId()));
        checkName(employee.getEmployeeName());
        checkWorkType(String.valueOf(employee.getWorkType()));
        checkWage(employee.getWage(),employee.getWorkType());
    }

    /**
     * Employee name must have at least one space and cannot be less than 5 characters
     */
    public static void  checkName(String empName) throws InvalidNameException {

        if ((!empName.contains(" ")) && (!empName.matches("[a-zA-Z0-9\\s]6+"))){
            throw new InvalidNameException("Employee name must have at least one space and cannot be less than 5 characters");
        }
    }

    /**
     * Employee ID must be a positive integer
     */
    public static String checkEmployeeId(String empId) throws InvalidIdException {

        if (!empId.matches("[0-9]+")){
            throw new InvalidIdException("Employee ID must be a positive integer ");
        }
        return empId;
    }

    /**
     * To validate WorkType the following are considered :
        * 'T' is for Temporary employees.
        * 'F' is for Full-time  employees.
        * 'C' is for Contract  employees.
     */

    public static void checkWorkType(String workType) throws InvalidWorkType {
        if (!workType.matches("[T,C,F]+")){
            throw new InvalidWorkType("Employee wage must be 'T' , 'F', 'C' only");
        }
    }

    /**
     * To validate Wage the following are considered:
         * The hourly pay cannot exceed 90.00 but can be 0
         * The bi-weekly pay cannot be below 1000.00 or more than 3500.00
         * The monthly pay cannot be less than 3000.00
     */
    public static void checkWage(double wage, char workType) throws InvalidWageException {

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
    }
}

