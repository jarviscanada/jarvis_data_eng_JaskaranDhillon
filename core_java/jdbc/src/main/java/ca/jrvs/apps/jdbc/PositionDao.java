package ca.jrvs.apps.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class PositionDao implements CrudDao<Position, String> {

  private Connection connection;
  private static final String save =
      "INSERT INTO position (symbol, number_of_shares, value_paid) VALUES (?, ?, ?)";
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
      throw new IllegalArgumentException("Position must not be null.");
    }

    try (PreparedStatement ps = connection.prepareStatement(save)) {
      ps.setString(1, position.getTicker());
      ps.setInt(2, position.getNumOfShares());
      ps.setDouble(3, position.getValuePaid());

      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to save position", e);
    }
    return position;
  }

  @Override
  public Optional<Position> findById(String symbol) throws IllegalArgumentException {
    if (symbol == null) {
      throw new IllegalArgumentException("Symbol must not be null.");
    }

    try (PreparedStatement ps = connection.prepareStatement(findById)) {
      ps.setString(1, symbol);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        Position position = new Position();

        position.setTicker(rs.getString("symbol"));
        position.setNumOfShares(rs.getInt("number_of_shares"));
        position.setValuePaid(rs.getDouble("value_paid"));

        return Optional.of(position);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to retrieve position with symbol = " + symbol, e);
    }

    return Optional.empty();
  }

  @Override
  public Iterable<Position> findAll() {
    List<Position> positions = new ArrayList<>();

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
      e.printStackTrace();
      throw new RuntimeException("Failed to retrieve all positions.", e);
    }
    return positions;
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
      throw new RuntimeException("Failed to delete position with symbol = " + symbol, e);
    }
  }

  @Override
  public void deleteAll() {
    try (PreparedStatement ps = connection.prepareStatement(deleteAll)) {
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to delete all positions.", e);
    }
  }
}