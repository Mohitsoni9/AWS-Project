package payrollSystem;

public class Payroll {

    public static double calculateWageAfterTax(double wage, char workType) {

        double wageAfterTax;

        //According to the Temporary employee, Contract employee, Full-time employee
        // Total wage after tax will be calculated.
        if (workType == 'T'){
            //tax is in %
            int tax = 15;
            wageAfterTax = wage - ((wage * tax)/100);
        }else if(workType == 'C'){
            //tax is in %
            int tax = 18;
            wageAfterTax = wage - ((wage * tax)/100);
        } else {
            //tax is in %
            //
            int tax = 30;
            wageAfterTax = wage - ((wage * tax)/100);
        }
        return wageAfterTax;
    }
}
