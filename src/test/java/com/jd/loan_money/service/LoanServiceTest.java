package com.jd.loan_money.service;

import com.jd.loan_money.model.Lender;
import com.jd.loan_money.model.Loan;
import com.jd.loan_money.repository.LenderRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class LoanServiceTest {

    @InjectMocks
    LoanService loanService;

    @Mock
    LenderRepository lenderRepository;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAvailableLoan() throws Exception {
        String marketFile = "Market_Data.csv";
        double loanAmount = 1000;
        List<Lender> lenders = new ArrayList<>();
        Lender lender = new Lender();
        lender.setRate(0.069);
        lender.setAvailable(480);
        lenders.add(lender);

        lender = new Lender();
        lender.setRate(0.071);
        lender.setAvailable(520);
        lenders.add(lender);

        when(lenderRepository.findAllLendersSortedByRate(marketFile)).thenReturn(lenders);
        CompletableFuture<Loan> loanFuture = loanService.getAvailableLoan(marketFile, loanAmount);
        Loan loan = loanFuture.get();
        assertEquals("7.0", String.valueOf(loan.getRate()));
    }

    @Test(expected=ExecutionException.class)
    public void testGetAvailableLoanOverAmount() throws Exception {
        String marketFile = "Market_Data.csv";
        double loanAmount = 15000;
        List<Lender> lenders = new ArrayList<>();
        Lender lender = new Lender();
        lender.setRate(0.069);
        lender.setAvailable(480);
        lenders.add(lender);

        lender = new Lender();
        lender.setRate(0.071);
        lender.setAvailable(520);
        lenders.add(lender);

        when(lenderRepository.findAllLendersSortedByRate(marketFile)).thenReturn(lenders);
        CompletableFuture<Loan> loanFuture = loanService.getAvailableLoan(marketFile, loanAmount);
        loanFuture.get();
    }
}
