package ca.jrvs.apps.jdbc.service;

import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.http.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import java.util.Optional;

public class QuoteService {

  private QuoteDao dao;
  private QuoteHttpHelper httpHelper;

  /**
   * Fetches latest quote data from endpoint
   *
   * @param ticker
   * @return Latest quote information or empty optional if ticker symbol not found
   */

  public QuoteService(QuoteDao quoteDao, QuoteHttpHelper quoteHttpHelper) {
    this.dao = quoteDao;
    this.httpHelper = quoteHttpHelper;
  }

  public Optional<Quote> fetchQuoteDataFromAPI(String ticker) {
    if (ticker == null || ticker.trim().length() == 0) {
      throw new IllegalArgumentException("Invalid symbol provided.");
    }
    try {
      Quote quote = httpHelper.fetchQuoteInfo(ticker);
      dao.save(quote);
      return Optional.of(quote);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return Optional.empty();
  }
}