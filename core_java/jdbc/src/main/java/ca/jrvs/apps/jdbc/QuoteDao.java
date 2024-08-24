package ca.jrvs.apps.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class QuoteDao implements CrudDao<Quote, String> {

  private Connection connection;
  private static final String save =
      "INSERT INTO quote (symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, "
          + "change_percent, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
      throw new IllegalArgumentException("Quote must not be null.");
    }

    try (PreparedStatement ps = connection.prepareStatement(save)) {
      ps.setString(1, quote.getTicker());
      ps.setDouble(2, quote.getOpen());
      ps.setDouble(3, quote.getHigh());
      ps.setDouble(4, quote.getLow());
      ps.setDouble(5, quote.getPrice());
      ps.setInt(6, quote.getVolume());
      ps.setDate(7, new java.sql.Date(quote.getLatestTradingDay().getTime())); //converting from java.util.date to java.sql.date
      ps.setDouble(8, quote.getPreviousClose());
      ps.setDouble(9, quote.getChange());
      ps.setString(10, quote.getChangePercent());
      ps.setTimestamp(11, quote.getTimestamp());

      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to save quote", e);
    }
    return null;
  }

  @Override
  public Optional<Quote> findById(String symbol) throws IllegalArgumentException {
    if (symbol == null) {
      throw new IllegalArgumentException("Symbol must not be null.");
    }

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

        return Optional.of(quote);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to retrieve quote with symbol = " + symbol, e);
    }

    return Optional.empty();
  }

  @Override
  public Iterable<Quote> findAll() {
    List<Quote> quotes = new ArrayList<>();

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
      e.printStackTrace();
      throw new RuntimeException("Failed to retrieve all quotes.", e);
    }
    return quotes;
  }

  @Override
  public void deleteById(String symbol) throws IllegalArgumentException {
    if (symbol == null) {
      throw new IllegalArgumentException("Symbol must not be null.");
    }

    try (PreparedStatement ps = connection.prepareStatement(deleteById)) {
      ps.setString(1, symbol);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to delete quote with symbol = " + symbol, e);
    }
  }

  @Override
  public void deleteAll() {
    try (PreparedStatement ps = connection.prepareStatement(deleteAll)) {
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to delete all quotes.", e);
    }
  }
}
