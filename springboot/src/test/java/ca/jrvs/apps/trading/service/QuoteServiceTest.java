package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dto.EODQuoteRequestDTO;
import ca.jrvs.apps.trading.exception.ResourceNotFoundException;
import ca.jrvs.apps.trading.model.Quote;
import ca.jrvs.apps.trading.repository.QuoteDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuoteServiceTest {
    @Mock
    private MarketDataDao marketDataDao;
    @Mock
    private QuoteDao quoteDao;
    @InjectMocks
    private QuoteService quoteService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findEODQuoteByTicker_Success() {
        String ticker = "AAPL";
        EODQuoteRequestDTO eodQuoteRequestDTO = new EODQuoteRequestDTO();
        eodQuoteRequestDTO.setTicker(ticker);

        when(marketDataDao.findById(ticker)).thenReturn(Optional.of(eodQuoteRequestDTO));

        EODQuoteRequestDTO result = quoteService.findEODQuoteByTicker(ticker);

        assertNotNull(result);
        verify(quoteDao, times(1)).save(any(Quote.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void findEODQuoteByTicker_NotFound() {
        String ticker = "AAPL";

        when(marketDataDao.findById(ticker)).thenReturn(Optional.empty());

        quoteService.findEODQuoteByTicker(ticker);
    }

    @Test
    public void updateMarketData_Success() {
        String ticker = "AAPL";
        List<Quote> quotes = new ArrayList<>();
        Quote quote = new Quote();
        quote.setTicker(ticker);
        quotes.add(quote);

        when(quoteDao.findAll()).thenReturn(quotes);
        when(marketDataDao.findById(anyString())).thenReturn(Optional.of(new EODQuoteRequestDTO()));

        quoteService.updateMarketData();

        verify(quoteDao, times(2)).save(any(Quote.class));
    }

    @Test(expected = RuntimeException.class)
    public void updateMarketData_NoQuotes() {
        when(quoteDao.findAll()).thenReturn(new ArrayList<>());

        quoteService.updateMarketData();
    }

    @Test
    public void saveQuotes_Success() {
        List<String> tickers = List.of("AAPL", "GOOG");
        List<Quote> quotes = new ArrayList<>();

        for (String ticker : tickers) {
            EODQuoteRequestDTO eodQuoteRequestDTO = new EODQuoteRequestDTO();
            eodQuoteRequestDTO.setTicker(ticker);
            when(marketDataDao.findById(ticker)).thenReturn(Optional.of(eodQuoteRequestDTO));
        }

        List<Quote> result = quoteService.saveQuotes(tickers);

        assertEquals(2, result.size());
        verify(quoteDao, times(4)).save(any(Quote.class));
    }

    @Test
    public void findAllQuotes_Success() {
        List<Quote> quotes = new ArrayList<>();
        quotes.add(new Quote());
        when(quoteDao.findAll()).thenReturn(quotes);

        List<Quote> result = quoteService.findAllQuotes();

        assertEquals(1, result.size());
    }

    @Test(expected = DataRetrievalFailureException.class)
    public void findAllQuotes_NoQuotesFound() {
        when(quoteDao.findAll()).thenReturn(new ArrayList<>());

        quoteService.findAllQuotes();
    }
}
