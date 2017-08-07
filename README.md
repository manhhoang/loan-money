## Loan Money

Run

```
java -jar loan-money-1.0.jar Market_Data.csv 1000

Output:

Requested amount: £1000
Rate: 7.0%
Monthly repayment: £30.78
Total repayment: £1108.08
```

Spring ApplicationContext

```
private static LoanService getService() {
    final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    final LoanService loanService = (LoanService) ctx.getBean(LOAN_SERVICE);
    ctx.close();
    return loanService;
}
```

Asynchronous by CompletableFuture

```
public CompletableFuture<Loan> getAvailableLoan(String marketFile, double loanAmount) {
    return CompletableFuture.supplyAsync(() -> lenderRepository.findAllLendersSortedByRate(marketFile))
            .thenApply(lenders -> {
                double total = 0;
                Map<Double, Double> lenderMap = getLenderMap(lenders, loanAmount);
                for (Map.Entry entry : lenderMap.entrySet()) {
                    total += (double) entry.getKey() * (double) entry.getValue();
                }
                Loan loan = new Loan();
                loan.setRate(Utils.roundOne(total / loanAmount * 100));
                loan.setRequestedAmount(loanAmount);
                final double totalRepay = loanAmount + calculateRepayInterest(loanAmount, loan.getRate());
                loan.setMonthlyRepayment(Utils.roundTwo(totalRepay / 36));
                loan.setTotalRepayment(Utils.roundTwo(loan.getMonthlyRepayment() * 36));
                return loan;
            }).exceptionally(e -> {
                logger.error(ERROR_MESSAGE);
                throw new AppException("100", ERROR_MESSAGE);
            });
}
```

Junit test by Mockito

```
@Test
public void testGetAvailableLoan() throws Exception {
    String marketFile = "lender_data.csv";
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
```

Concurrency test

```
@Test
public void testGetAvailableLoanConcurrency() throws Exception {
    final int numberThreads = 100;
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
```
