package ca.jrvs.apps.jdbc.service;

import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionService {
  private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);
  private PositionDao positionDao;
  private QuoteDao quoteDao;

  public PositionService(PositionDao positionDao, QuoteDao quoteDao) {
    this.positionDao = positionDao;
    this.quoteDao = quoteDao;
  }

  /**
   * Processes a buy order and updates the database accordingly
   *
   * @param ticker
   * @param numberOfShares
   * @param price
   * @return The position in our database after processing the buy
   */
  public Position buy(String ticker, int numberOfShares, double price) {
    if (ticker == null || ticker.trim().length() == 0 || numberOfShares <= 0 || price <= 0) {
      logger.error("Attempting to buy share with invalid symbol, number of shares, or price provided.");
      throw new IllegalArgumentException("Invalid symbol, number of shares, or price provided.");
    }

    logger.error("Attempting to purchase {} shares of {} at ${]", numberOfShares, ticker, price);
    Optional<Quote> foundQuote = quoteDao.findById(ticker);

    if (!foundQuote.isPresent()) {
      logger.error("Symbol data not found.");
      throw new IllegalArgumentException("Symbol data not found.");
    }

    Quote quote = foundQuote.get();
    if (numberOfShares > quote.getVolume()) {
      logger.error("Insufficient volume available for purchase.");
      throw new IllegalArgumentException("Insufficient volume available for purchase.");
    }

    Optional<Position> existingPosition = positionDao.findById(ticker);
    Position position = new Position();

    if (existingPosition.isPresent()) {
      position = existingPosition.get();
      position.setNumOfShares(position.getNumOfShares() + numberOfShares);
      position.setValuePaid(position.getValuePaid() + (price * numberOfShares));
    } else {
      position.setTicker(ticker);
      position.setNumOfShares(numberOfShares);
      position.setValuePaid(price * numberOfShares);
    }

    positionDao.save(position);
    logger.info("Returning position {} ", position.toString());
    return position;
  }

  /**
   * Sells all shares of the given ticker symbol
   *
   * @param ticker
   */
  public void sell(String ticker) {
    logger.info("Attempting to sell all shares of {}", ticker);
    positionDao.deleteById(ticker);
  }

  /**
   * Calculates if selling a certain stock will generate a profit or loss.
   *
   * @param ticker
   * @return The position in our database after processing the buy
   */
  public double calculateProfit(String ticker) {

    if (ticker == null || ticker.trim().length() == 0) {
      logger.error("Attempted to calculate profit of invalid symbol");
      throw new IllegalArgumentException("Invalid symbol provided.");
    }
    logger.info("Attempting to calculate profits of {}", ticker);
    Optional<Quote> optionalQuote = quoteDao.findById(ticker);

    if (!optionalQuote.isPresent()) {
      logger.error("No quote found for given symbol");
      throw new IllegalArgumentException("No quote found for given symbol: " + ticker);
    }

    Quote quote = optionalQuote.get();
    Optional<Position> optionalPosition = positionDao.findById(ticker);

    if (!optionalPosition.isPresent()) {
      logger.error("No shares owned of the given symbol");
      throw new IllegalArgumentException(
          "You don't own any shares for the given symbol: " + ticker + "!");
    }

    Position position = optionalPosition.get();

    double averagePrice = position.getValuePaid() / position.getNumOfShares();
    double currentPrice = quote.getPrice();

    logger.info("Returning profits calculated");
    return (currentPrice - averagePrice) * position.getNumOfShares();
  }
}