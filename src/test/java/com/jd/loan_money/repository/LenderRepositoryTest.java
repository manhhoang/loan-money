package com.jd.loan_money.repository;

import com.jd.loan_money.exception.AppException;
import com.jd.loan_money.model.Lender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LenderRepositoryTest {

    @InjectMocks
    LenderRepository lenderRepository;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllLender() throws IOException{
        List<Lender> lenders = lenderRepository.findAllLendersSortedByRate("Market_Data.csv");
        assertEquals(7, lenders.size());
    }

    @Test(expected=AppException.class)
    public void testFindAllLenderFail() throws IOException{
        List<Lender> lenders = lenderRepository.findAllLendersSortedByRate("Market_Data_wrong.csv");
        assertEquals(7, lenders.size());
    }
}
