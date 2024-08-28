package ca.jrvs.apps.jdbc.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.http.QuoteHttpHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class QuoteServiceTest {

  private QuoteDao quoteDao;
  private QuoteHttpHelper quoteHttpHelper;
  private QuoteService quoteService;

  @Before
  public void setUp() {
    quoteDao = mock(QuoteDao.class);
    quoteHttpHelper = mock(QuoteHttpHelper.class);
    quoteService = new QuoteService(quoteDao, quoteHttpHelper);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTicker() {
    quoteService.fetchQuoteDataFromAPI(null);
  }

  @Test
  public void testFetchQuoteDataFromAPI() {
    Quote quote = new Quote();
    quote.setTicker("STOCK1");
    when(quoteHttpHelper.fetchQuoteInfo("STOCK1")).thenReturn(quote);

    Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("STOCK1");

    assertTrue(result.isPresent());
    assertEquals("STOCK1", result.get().getTicker());
  }

  @Test
  public void testFetchQuoteDataFromAPIFailure() {
    when(quoteHttpHelper.fetchQuoteInfo("AAPL")).thenThrow(new RuntimeException("API Error"));

    Optional<Quote> result = quoteService.fetchQuoteDataFromAPI("AAPL");

    assertFalse(result.isPresent());
  }
}