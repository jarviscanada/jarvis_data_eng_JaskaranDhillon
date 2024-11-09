package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dto.TraderAccountViewDto;
import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.model.Position;
import ca.jrvs.apps.trading.model.Trader;
import ca.jrvs.apps.trading.repository.AccountDao;
import ca.jrvs.apps.trading.repository.PositionDao;
import ca.jrvs.apps.trading.repository.SecurityOrderDao;
import ca.jrvs.apps.trading.repository.TraderDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TraderAccountServiceTest {

    @Mock
    private TraderDao traderDao;

    @Mock
    private AccountDao accountDao;

    @Mock
    private PositionDao positionDao;

    @Mock
    private SecurityOrderDao securityOrderDao;

    @InjectMocks
    private TraderAccountService traderAccountService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTraderAndAccount_Failure_NullTrader() {
        traderAccountService.createTraderAndAccount(null);
    }

    @Test
    public void createTraderAndAccount_Success() {
        Trader trader = new Trader();
        trader.setId(1);
        trader.setFirstName("John");
        trader.setLastName("Dopper");
        trader.setDob(LocalDate.parse("1990-01-01"));
        trader.setCountry("USA");
        trader.setEmail("john.dopper@gmail.com");

        when(traderDao.save(any(Trader.class))).thenReturn(trader);

        TraderAccountViewDto accountViewDto = traderAccountService.createTraderAndAccount(trader);

        assertNotNull(accountViewDto);
        assertEquals(1, accountViewDto.getId());
        assertEquals("John", accountViewDto.getFirstName());
        assertEquals("Dopper", accountViewDto.getLastName());
        assertEquals("1990-01-01", accountViewDto.getDob().toString());
        assertEquals("USA", accountViewDto.getCountry());
        assertEquals("john.dopper@gmail.com", accountViewDto.getEmail());
        verify(accountDao, times(1)).save(any(Account.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteTraderById_Failure_NullTraderId() {
        traderAccountService.deleteTraderById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteTraderById_Failure_AccountNotFound() {
        when(accountDao.getAccountByTraderId(1)).thenReturn(null);
        traderAccountService.deleteTraderById(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteTraderById_Failure_AccountHasBalance() {
        Account account = new Account();
        account.setTraderId(1);
        account.setAmount(100.0);

        when(accountDao.getAccountByTraderId(1)).thenReturn(account);
        traderAccountService.deleteTraderById(1);
    }

    @Test
    public void deleteTraderById_Success() {
        Account account = new Account();
        account.setTraderId(1);
        account.setId(1);
        account.setAmount(0.0);

        when(accountDao.getAccountByTraderId(1)).thenReturn(account);
        when(positionDao.findAllByAccountId(account.getId())).thenReturn(Collections.emptyList());

        traderAccountService.deleteTraderById(1);

        verify(securityOrderDao, times(1)).deleteByAccountId((long) account.getId());
        verify(accountDao, times(1)).deleteById(account.getId());
        verify(traderDao, times(1)).deleteById(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deposit_Failure_NullTraderId() {
        traderAccountService.deposit(null, 100.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deposit_Failure_NegativeAmount() {
        traderAccountService.deposit(1, -100.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deposit_Failure_AccountNotFound() {
        when(accountDao.getAccountByTraderId(1)).thenReturn(null);
        traderAccountService.deposit(1, 100.0);
    }

    @Test
    public void deposit_Success() {
        Account account = new Account();
        account.setTraderId(1);
        account.setAmount(100.0);

        when(accountDao.getAccountByTraderId(1)).thenReturn(account);
        when(accountDao.save(any(Account.class))).thenReturn(account);

        Account updatedAccount = traderAccountService.deposit(1, 50.0);

        assertNotNull(updatedAccount);
        assertEquals(150.0, updatedAccount.getAmount(), 0.001);
        verify(accountDao, times(1)).save(account);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithdraw_Failure_NegativeAmount() {
        traderAccountService.withdraw(1, -100.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withdraw_Failure_AccountNotFound() {
        when(accountDao.getAccountByTraderId(1)).thenReturn(null);
        traderAccountService.withdraw(1, 100.0);
    }

    @Test
    public void withdraw_Success() {
        Account account = new Account();
        account.setTraderId(1);
        account.setAmount(150.0);

        when(accountDao.getAccountByTraderId(1)).thenReturn(account);
        when(accountDao.save(any(Account.class))).thenReturn(account);

        Account updatedAccount = traderAccountService.withdraw(1, 50.0);

        assertNotNull(updatedAccount);
        assertEquals((Double) 100.0, updatedAccount.getAmount(), 0.001);
        verify(accountDao, times(1)).save(account);
    }
}
