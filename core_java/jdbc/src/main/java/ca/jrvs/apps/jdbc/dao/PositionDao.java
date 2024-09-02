package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.dto.Position;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionDao implements CrudDao<Position, String> {

  private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);
  private Connection connection;
  private static final String insert =
      "INSERT INTO position (number_of_shares, value_paid, symbol) VALUES (?, ?, ?)";
  private static final String update =
      "UPDATE position SET number_of_shares = ?, value_paid = ? WHERE symbol = ?";
  private static final String findById = "SELECT * FROM position WHERE symbol = ?";
  private static final String findAll = "SELECT * FROM position";
  private static final String deleteAll = "DELETE FROM position";
  private static final String deleteById = "DELETE FROM position WHERE symbol = ?";

  public PositionDao(Connection connection) {
    this.connection = connection;
  }

  @Override
  public Position save(Position position) throws IllegalArgumentException {
    if (position == null) {
      logger.error("Attempted to save null position");
      throw new IllegalArgumentException("Position must not be null.");
    }
    logger.info("Saving position: {}", position.toString());
    Optional<Position> existingPosition = findById(position.getTicker());

    String query;
    if (existingPosition.isPresent()) {
      query = update;
    } else {
      query = insert;
    }

    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, position.getNumOfShares());
      ps.setDouble(2, position.getValuePaid());
      ps.setString(3, position.getTicker());

      ps.executeUpdate();
      logger.info("Position saved successfully");
    } catch (SQLException e) {
      logger.info("Failed to save position");
      logger.error("Failed to save position: {}", position, e);
      throw new RuntimeException("Failed to save position", e);
    }

    return position;
  }

  @Override
  public Optional<Position> findById(String symbol) throws IllegalArgumentException {
    if (symbol == null) {
      logger.error("Attempted to find position with null symbol");
      throw new IllegalArgumentException("Symbol must not be null.");
    }

    logger.info("Looking for position with symbol = {}", symbol);

    try (PreparedStatement ps = connection.prepareStatement(findById)) {
      ps.setString(1, symbol);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        Position position = new Position();

        position.setTicker(rs.getString("symbol"));
        position.setNumOfShares(rs.getInt("number_of_shares"));
        position.setValuePaid(rs.getDouble("value_paid"));
        logger.info("Found position, returning: {}", position.toString());
        return Optional.of(position);
      }
    } catch (SQLException e) {
      logger.info("Failed to retrieve position");
      logger.error("Failed to retrieve position with symbol = {}", symbol, e);
      throw new RuntimeException("Failed to retrieve position with symbol = " + symbol, e);
    }

    logger.info("No position found");
    return Optional.empty();
  }

  @Override
  public Iterable<Position> findAll() {
    List<Position> positions = new ArrayList<>();
    logger.info("Fetching all positions...");
    try (PreparedStatement ps = connection.prepareStatement(findAll);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        Position position = new Position();

        position.setTicker(rs.getString("symbol"));
        position.setNumOfShares(rs.getInt("number_of_shares"));
        position.setValuePaid(rs.getDouble("value_paid"));

        positions.add(position);
      }
    } catch (SQLException e) {
      logger.error("Failed to fetch all positions...", e);
      throw new RuntimeException("Failed to retrieve all positions.", e);
    }

    logger.info("Found {} positions", positions.size());
    return positions;
  }

  @Override
  public void deleteById(String symbol) throws IllegalArgumentException {
    if (symbol == null) {
      logger.error("Attempted to delete position with null symbol");
      throw new IllegalArgumentException("Symbol must not be null.");
    }
    logger.info("Attempting to delete position with symbol = {}", symbol);
    try (PreparedStatement ps = connection.prepareStatement(deleteById)) {
      ps.setString(1, symbol);
      ps.executeUpdate();
      logger.info("Successfully deleted");
    } catch (SQLException e) {
      logger.error("Failed to fetch all positions...", e);
      throw new RuntimeException("Failed to delete position with symbol = " + symbol, e);
    }
  }

  @Override
  public void deleteAll() {
    logger.info("Attempting to delete all positions");
    try (PreparedStatement ps = connection.prepareStatement(deleteAll)) {
      ps.executeUpdate();
      logger.info("Successfully deleted all positions");
    } catch (SQLException e) {
      logger.error("Failed to delete all positions.", e);
      throw new RuntimeException("Failed to delete all positions.", e);
    }
  }
}