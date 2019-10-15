package edu.epam.izhevsk.junit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class PaymentControllerTest {

    @Mock
    AccountService accountService;
    @Mock
    DepositService depositService;

    @InjectMocks
    PaymentController paymentController;

    @BeforeEach
    void init() throws InsufficientFundsException {
        MockitoAnnotations.initMocks(this);
        when(accountService.isUserAuthenticated(100L)).thenReturn(true);
        when(accountService.isUserAuthenticated(AdditionalMatchers.not(eq(100L)))).thenReturn(false);
        Mockito.when(depositService.deposit(AdditionalMatchers.gt(100L), ArgumentMatchers.any()))
                .thenThrow(InsufficientFundsException.class);
    }


    @Test
    public void testDepositSuccessful() throws InsufficientFundsException {
        paymentController.deposit(50L, 100L);
        verify(accountService, Mockito.times(1)).isUserAuthenticated(100L);
        verify(depositService, Mockito.times(1)).deposit(50L, 100L);
    }

    @Test
    public void testDepositInvalidUser() {
        assertThrows(SecurityException.class, (() -> paymentController.deposit(50L, 200L)));
    }

    @Test
    public void testDepositInvalidAmount() {
        assertThrows(InsufficientFundsException.class, (() -> paymentController.deposit(150L, 100L)));
    }


}
