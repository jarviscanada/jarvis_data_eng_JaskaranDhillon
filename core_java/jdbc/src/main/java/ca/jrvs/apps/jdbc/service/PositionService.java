package ca.jrvs.apps.jdbc.service;

import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import java.util.Optional;

public class PositionService {

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
      throw new IllegalArgumentException("Invalid symbol, number of shares, or price provided.");
    }

    Optional<Quote> foundQuote = quoteDao.findById(ticker);

    if (!foundQuote.isPresent()) {
      throw new IllegalArgumentException("Symbol data not found.");
    }

    Quote quote = foundQuote.get();
    if (numberOfShares > quote.getVolume()) {
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

    return position;
  }

  /**
   * Sells all shares of the given ticker symbol
   *
   * @param ticker
   */
  public void sell(String ticker) {
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
      throw new IllegalArgumentException("Invalid symbol provided.");
    }

    Optional<Quote> optionalQuote = quoteDao.findById(ticker);

    if (!optionalQuote.isPresent()) {
      throw new IllegalArgumentException("No quote found for given symbol: " + ticker);
    }

    Quote quote = optionalQuote.get();
    Optional<Position> optionalPosition = positionDao.findById(ticker);

    if (!optionalPosition.isPresent()) {
      throw new IllegalArgumentException(
          "You don't own any stocks for the given symbol: " + ticker + "!");
    }

    Position position = optionalPosition.get();

    double averagePrice = position.getValuePaid() / position.getNumOfShares();
    double currentPrice = quote.getPrice();

    return (currentPrice - averagePrice) * position.getNumOfShares();
  }
}