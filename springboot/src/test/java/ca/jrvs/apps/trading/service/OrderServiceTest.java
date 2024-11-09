package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dto.EODQuoteRequestDTO;
import ca.jrvs.apps.trading.dto.MarketOrderDTO;
import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.model.Position;
import ca.jrvs.apps.trading.model.SecurityOrder;
import ca.jrvs.apps.trading.model.Trader;
import ca.jrvs.apps.trading.repository.AccountDao;
import ca.jrvs.apps.trading.repository.PositionDao;
import ca.jrvs.apps.trading.repository.SecurityOrderDao;
import ca.jrvs.apps.trading.repository.TraderDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    private QuoteService quoteService;

    @Mock
    private TraderAccountService traderAccountService;

    @Mock
    private SecurityOrderDao securityOrderDao;

    @Mock
    private TraderDao traderDao;

    @Mock
    private AccountDao accountDao;

    @Mock
    private PositionDao positionDao;

    @InjectMocks
    private OrderService orderService;

    private Trader trader;
    private Account account;
    private SecurityOrder securityOrder;
    private MarketOrderDTO marketOrderDto;
    private EODQuoteRequestDTO eodQuote;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Setup a mock trader
        trader = new Trader();
        trader.setId(1);
        trader.setFirstName("John");
        trader.setLastName("Doe");

        // Setup a mock account
        account = new Account();
        account.setId(1);
        account.setTraderId(trader.getId());
        account.setAmount(1000.0);

        // Setup a mock security order
        securityOrder = new SecurityOrder();
        securityOrder.setAccountId(account.getId());

        // Setup market order DTO
        marketOrderDto = new MarketOrderDTO();
        marketOrderDto.setTraderId(trader.getId());
        marketOrderDto.setTicker("AAPL");
        marketOrderDto.setSize(10);
        marketOrderDto.setOption("BUY");

        // Setup EOD Quote
        eodQuote = new EODQuoteRequestDTO();
        eodQuote.setAskPrice(50.0F);
        eodQuote.setAskSize(100);
        eodQuote.setBidPrice(50.0F);
        eodQuote.setBidSize(100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeMarketOrder_Failure_InvalidTicker() {
        marketOrderDto.setTicker("");
        orderService.executeMarketOrder(marketOrderDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeMarketOrder_Failure_NegativeSize() {
        marketOrderDto.setSize(-5);
        orderService.executeMarketOrder(marketOrderDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeMarketOrder_Failure_TraderNotFound() {
        when(traderDao.findById(trader.getId())).thenReturn(Optional.empty());
        orderService.executeMarketOrder(marketOrderDto);
    }

    @Test(expected = RuntimeException.class)
    public void executeMarketOrder_Failure_InsufficientFunds() {
        when(traderDao.findById(trader.getId())).thenReturn(Optional.of(trader));
        when(accountDao.getAccountByTraderId(trader.getId())).thenReturn(account);
        when(quoteService.findEODQuoteByTicker("AAPL")).thenReturn(eodQuote);

        marketOrderDto.setSize(30);
        orderService.executeMarketOrder(marketOrderDto);
    }

    @Test
    public void executeMarketOrder_BuyOrder() {
        when(traderDao.findById(trader.getId())).thenReturn(Optional.of(trader));
        when(accountDao.getAccountByTraderId(trader.getId())).thenReturn(account);
        when(quoteService.findEODQuoteByTicker("AAPL")).thenReturn(eodQuote);

        SecurityOrder result = orderService.executeMarketOrder(marketOrderDto);

        assertNotNull(result);
        assertEquals("FILLED", result.getStatus());
        assertEquals((Double) 50.0, result.getPrice());
        assertEquals(Integer.valueOf(10), result.getSize());
        verify(traderAccountService).withdraw(trader.getId(), 500.0);
        verify(securityOrderDao).save(result);
    }

    @Test
    public void executeMarketOrder_SellOrder() {
        marketOrderDto.setOption("SELL");
        marketOrderDto.setSize(5);

        Position position = new Position();
        position.setTicker("AAPL");
        position.setPosition(10);

        when(traderDao.findById(trader.getId())).thenReturn(Optional.of(trader));
        when(accountDao.getAccountByTraderId(trader.getId())).thenReturn(account);
        when(positionDao.findByTicker("AAPL")).thenReturn(Optional.of(position));
        when(quoteService.findEODQuoteByTicker("AAPL")).thenReturn(eodQuote);

        SecurityOrder result = orderService.executeMarketOrder(marketOrderDto);

        assertNotNull(result);
        assertEquals("FILLED", result.getStatus());
        assertEquals((Double) 50.0, result.getPrice());
        assertEquals(Integer.valueOf( -5), result.getSize());
        verify(traderAccountService).deposit(trader.getId(), 250.0);
        verify(securityOrderDao).save(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeMarketOrder_Failure_InsufficientSharesToSell() {
        marketOrderDto.setOption("SELL");
        marketOrderDto.setSize(15);

        Position position = new Position();
        position.setTicker("AAPL");
        position.setPosition(10);

        when(traderDao.findById(trader.getId())).thenReturn(Optional.of(trader));
        when(accountDao.getAccountByTraderId(trader.getId())).thenReturn(account);
        when(positionDao.findByTicker("AAPL")).thenReturn(Optional.of(position));

        orderService.executeMarketOrder(marketOrderDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void executeMarketOrder_Failure_InvalidOption() {
        marketOrderDto.setOption("BUYSELL");
        orderService.executeMarketOrder(marketOrderDto);
    }
}
