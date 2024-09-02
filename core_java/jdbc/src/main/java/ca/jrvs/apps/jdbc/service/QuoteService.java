package ca.jrvs.apps.jdbc.service;

import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.http.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteService {
  private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);
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
      logger.error("Attempted to fetch quote data of invalid symbol provided");
      throw new IllegalArgumentException("Invalid symbol provided.");
    }
    logger.info("Attempting to fetch quote data for symbol = {}", ticker);
    try {
      Quote quote = httpHelper.fetchQuoteInfo(ticker);
      dao.save(quote);
      logger.info("Returning quote data: {}", quote.toString());
      return Optional.of(quote);
    } catch (Exception e) {
      logger.info("Failed to fetch quote data, returning empty quote");
      logger.error("Failed to fetch quote data", e);
    }

    return Optional.empty();
  }
}