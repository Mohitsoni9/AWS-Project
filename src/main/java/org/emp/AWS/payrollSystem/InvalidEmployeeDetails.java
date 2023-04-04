package org.emp.AWS.payrollSystem;

public class InvalidEmployeeDetails extends Exception {
    public InvalidEmployeeDetails(String str) {
        super(str);
    }
}
class InvalidNameException extends InvalidEmployeeDetails {
    public InvalidNameException(String str) {
        super(str);
    }
}
class InvalidIdException extends InvalidEmployeeDetails {
    public InvalidIdException(String str) {
        super(str);
    }
}
class InvalidWageException extends InvalidEmployeeDetails {
    public InvalidWageException(String str) {
        super(str);
    }
}
class InvalidWorkType extends InvalidEmployeeDetails {
    public InvalidWorkType(String str) {
        super(str);
    }
}
