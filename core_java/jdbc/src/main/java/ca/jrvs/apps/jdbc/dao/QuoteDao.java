package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.dto.Quote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteDao implements CrudDao<Quote, String> {
  private static final Logger logger = LoggerFactory.getLogger(QuoteDao.class);
  private Connection connection;
  private static final String insert =
      "INSERT INTO quote (open, high, low, price, volume, latest_trading_day, previous_close, change, "
          + "change_percent, timestamp, symbol) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String update =
      "UPDATE quote SET open = ?, high = ?, low = ?, price = ?, volume = ?, latest_trading_day = ?, "
          + "previous_close = ?, change = ?, change_percent = ?, timestamp = ? WHERE symbol = ?";
  private static final String findById = "SELECT * FROM quote WHERE symbol = ?";
  private static final String findAll = "SELECT * FROM quote";
  private static final String deleteAll = "DELETE FROM quote";
  private static final String deleteById = "DELETE FROM quote WHERE symbol = ?";

  public QuoteDao(Connection connection) {
    this.connection = connection;
  }

  @Override
  public Quote save(Quote quote) throws IllegalArgumentException {
    if (quote == null) {
      logger.error("Attempted to save null quote");
      throw new IllegalArgumentException("Quote must not be null.");
    }
    logger.info("Saving quote: {}", quote.toString());
    Optional<Quote> existingQuote = findById(quote.getTicker());

    String query;
    if (existingQuote.isPresent()) {
      query = update;
    } else {
      query = insert;
    }

    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setDouble(1, quote.getOpen());
      ps.setDouble(2, quote.getHigh());
      ps.setDouble(3, quote.getLow());
      ps.setDouble(4, quote.getPrice());
      ps.setInt(5, quote.getVolume());
      ps.setDate(6, new java.sql.Date(quote.getLatestTradingDay()
          .getTime())); // converting from java.util.Date to java.sql.Date
      ps.setDouble(7, quote.getPreviousClose());
      ps.setDouble(8, quote.getChange());
      ps.setString(9, quote.getChangePercent());
      ps.setTimestamp(10, quote.getTimestamp());
      ps.setString(11, quote.getTicker());

      ps.executeUpdate();
      logger.info("Quote saved successfully");
    } catch (SQLException e) {
      logger.info("Failed to save quote");
      logger.error("Failed to save quote: {}", quote, e);
      throw new RuntimeException("Failed to save quote", e);
    }
    return null;
  }

  @Override
  public Optional<Quote> findById(String symbol) throws IllegalArgumentException {
    if (symbol == null) {
      logger.error("Attempted to find quote with null symbol");
      throw new IllegalArgumentException("Symbol must not be null.");
    }

    logger.info("Looking for quote with symbol = {}", symbol);
    try (PreparedStatement ps = connection.prepareStatement(findById)) {
      ps.setString(1, symbol);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        Quote quote = new Quote();

        quote.setTicker(rs.getString("symbol"));
        quote.setOpen(rs.getDouble("open"));
        quote.setHigh(rs.getDouble("high"));
        quote.setLow(rs.getDouble("low"));
        quote.setPrice(rs.getDouble("price"));
        quote.setVolume(rs.getInt("volume"));
        quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
        quote.setPreviousClose(rs.getDouble("previous_close"));
        quote.setChange(rs.getDouble("change"));
        quote.setChangePercent(rs.getString("change_percent"));
        quote.setTimestamp(rs.getTimestamp("timestamp"));
        logger.info("Found quote, returning: {}", quote.toString());
        return Optional.of(quote);
      }
    } catch (SQLException e) {
      logger.info("Failed to retrieve quote");
      logger.error("Failed to retrieve quote with symbol = {}", symbol, e);
      throw new RuntimeException("Failed to retrieve quote with symbol = "+ symbol, e);
    }

    return Optional.empty();
  }

  @Override
  public Iterable<Quote> findAll() {
    List<Quote> quotes = new ArrayList<>();
    logger.info("Fetching all quotes...");
    try (PreparedStatement ps = connection.prepareStatement(findAll);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        Quote quote = new Quote();

        quote.setTicker(rs.getString("symbol"));
        quote.setOpen(rs.getDouble("open"));
        quote.setHigh(rs.getDouble("high"));
        quote.setLow(rs.getDouble("low"));
        quote.setPrice(rs.getDouble("price"));
        quote.setVolume(rs.getInt("volume"));
        quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
        quote.setPreviousClose(rs.getDouble("previous_close"));
        quote.setChange(rs.getDouble("change"));
        quote.setChangePercent(rs.getString("change_percent"));
        quote.setTimestamp(rs.getTimestamp("timestamp"));

        quotes.add(quote);
      }
    } catch (SQLException e) {
      logger.error("Failed to fetch all quotes...", e);
      throw new RuntimeException("Failed to retrieve all quotes.", e);
    }

    logger.info("Found {} quotes", quotes.size());
    return quotes;
  }

  @Override
  public void deleteById(String symbol) throws IllegalArgumentException {
    if (symbol == null) {
      logger.error("Attempted to delete quote with null symbol");
      throw new IllegalArgumentException("Symbol must not be null.");
    }
    logger.info("Attempting to delete quote with symbol = {}", symbol);
    try (PreparedStatement ps = connection.prepareStatement(deleteById)) {
      ps.setString(1, symbol);
      ps.executeUpdate();
      logger.info("Successfully deleted");
    } catch (SQLException e) {
      logger.error("Failed to fetch all quotes...", e);
      throw new RuntimeException("Failed to delete quote with symbol = " + symbol, e);
    }
  }

  @Override
  public void deleteAll() {
    logger.info("Attempting to delete all quotes");
    try (PreparedStatement ps = connection.prepareStatement(deleteAll)) {
      ps.executeUpdate();
    } catch (SQLException e) {
      logger.error("Failed to delete all quotes.", e);
      throw new RuntimeException("Failed to delete all quotes.", e);
    }
  }
}
