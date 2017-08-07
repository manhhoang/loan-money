package com.jd.loan_money;

import com.jd.loan_money.config.AppConfig;
import com.jd.loan_money.model.Loan;
import com.jd.loan_money.service.LoanService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.jd.loan_money.utils.Constants.ERROR_MESSAGE;
import static com.jd.loan_money.utils.Constants.LOAN_SERVICE;

public class Application {

    public static void main(String[] args) throws Exception {
        if(args.length < 2) {
            System.out.println("Missing market file and loan amount arguments.");
        }
        final String marketFile = args[0];
        final double loanAmount = Double.parseDouble(args[1]);
        printLoan(getAvailableLoan(marketFile, loanAmount));
    }

    private static Loan getAvailableLoan(final String marketFile, final double loanAmount) {
        Loan loan = new Loan();
        final LoanService loanService = getService();
        try {
            if(loanAmount >= 1000 && loanAmount <= 15000 && (loanAmount % 100 == 0)) {
                loan = loanService.getAvailableLoan(marketFile, loanAmount).get();
            } else {
                loan.setValid(false);
            }
        } catch (Exception ex) {
            loan.setRequestedAmount(0);
        }
        return loan;
    }

    private static LoanService getService() {
        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        final LoanService loanService = (LoanService) ctx.getBean(LOAN_SERVICE);
        ctx.close();
        return loanService;
    }

    private static void printLoan(final Loan loan) {
        if(!loan.isValid()) {
            System.out.println("Loan has to be of any £100 increment between £1000 and £15000 inclusive.");
            return;
        }
        if (loan.getRequestedAmount() == 0) {
            System.out.println(ERROR_MESSAGE);
        } else {
            System.out.println("Requested amount: £" + String.format("%.0f", loan.getRequestedAmount()));
            System.out.println("Rate: " + String.format("%.01f", loan.getRate()) + "%");
            System.out.println("Monthly repayment: £" + String.format("%.02f", loan.getMonthlyRepayment()));
            System.out.println("Total repayment: £" + String.format("%.02f", loan.getTotalRepayment()));
        }
    }
}