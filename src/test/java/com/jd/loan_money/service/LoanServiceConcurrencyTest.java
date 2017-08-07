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
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class LoanServiceConcurrencyTest {

    @InjectMocks
    LoanService loanService;

    @Mock
    LenderRepository lenderRepository;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAvailableLoanConcurrency() throws Exception {
        final int numberThreads = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(numberThreads);
        final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numberThreads);
        final CountDownLatch afterInitBlocker = new CountDownLatch(1);
        final CountDownLatch allDone = new CountDownLatch(numberThreads);
        for (int i = 0; i < numberThreads; i++) {
            executorService.submit(() -> {
                allExecutorThreadsReady.countDown();
                try {
                    afterInitBlocker.await();
                    getAvailableLoan();
                } catch (final Exception e) {
                } finally {
                    allDone.countDown();
                }
            });
        }
        // wait until all threads are ready
        allExecutorThreadsReady.await(numberThreads * 10, TimeUnit.MILLISECONDS);
        // start all test runners
        afterInitBlocker.countDown();
        allDone.await(30, TimeUnit.SECONDS);
        executorService.shutdownNow();
    }

    private void getAvailableLoan() throws Exception {
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
        assertEquals("0.07", String.valueOf(loan.getRate()));
    }
}
