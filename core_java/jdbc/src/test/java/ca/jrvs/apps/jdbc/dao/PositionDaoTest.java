package ca.jrvs.apps.jdbc.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PositionDaoTest {

  private Connection connection;
  private PositionDao positionDao;
  private QuoteDao quoteDao;

  @Before
  public void setUp() throws SQLException {
    // Credentials
    String url = "jdbc:postgresql://localhost:5432/stock_quote";
    String user = "postgres";
    String password = "password";

    connection = DriverManager.getConnection(url, user, password);
    positionDao = new PositionDao(connection);
    quoteDao = new QuoteDao(connection);

    // Starting with clean database
    connection.createStatement().execute("TRUNCATE TABLE position CASCADE");
    connection.createStatement().execute("TRUNCATE TABLE quote CASCADE");

    // Insert STOCK1 and STOCK2 to satisfy FK requirement
    Quote stock1 = new Quote();
    stock1.setTicker("STOCK1");
    stock1.setOpen(100.0);
    stock1.setHigh(105.0);
    stock1.setLow(95.0);
    stock1.setPrice(102.0);
    stock1.setVolume(1000);
    stock1.setLatestTradingDay(new java.util.Date());
    stock1.setPreviousClose(101.0);
    stock1.setChange(1.0);
    stock1.setChangePercent("1%");
    stock1.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
    quoteDao.save(stock1);

    Quote stock2 = new Quote();
    stock2.setTicker("STOCK2");
    stock2.setOpen(50.00);
    stock2.setHigh(52.00);
    stock2.setLow(49.00);
    stock2.setPrice(51.25);
    stock2.setVolume(1000000);
    stock2.setLatestTradingDay(new java.util.Date());
    stock2.setPreviousClose(50.50);
    stock2.setChange(0.75);
    stock2.setChangePercent("1.49%");
    stock2.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
    quoteDao.save(stock2);
  }

  @After
  public void tearDown() throws SQLException {
    connection.close();
  }

  @Test
  public void testSaveAndFindById() {
    // Create a Position
    Position position = new Position();
    position.setTicker("STOCK1");
    position.setNumOfShares(50);
    position.setValuePaid(5100.0);

    positionDao.save(position);

    Optional<Position> foundPosition = positionDao.findById("STOCK1");
    assertTrue(foundPosition.isPresent());

    Position pos = foundPosition.get();
    assertEquals(position.getTicker(), pos.getTicker());
    assertEquals(position.getNumOfShares(), pos.getNumOfShares());
    assertEquals(position.getValuePaid(), pos.getValuePaid(), 0.01);
  }

  @Test
  public void testFindAll() {
    Position position1 = new Position();
    position1.setTicker("STOCK1");
    position1.setNumOfShares(100);
    position1.setValuePaid(51000.0);

    Position position2 = new Position();
    position2.setTicker("STOCK2");
    position2.setNumOfShares(200);
    position2.setValuePaid(10250.0);


    positionDao.save(position1);
    positionDao.save(position2);

    List<Position> allPositions = (List<Position>) positionDao.findAll();
    assertEquals(2, allPositions.size());

    assertEquals("STOCK1", allPositions.get(0).getTicker());
    assertEquals(100, allPositions.get(0).getNumOfShares());
    assertEquals(51000.0, allPositions.get(0).getValuePaid(), 0.01);

    assertEquals("STOCK2", allPositions.get(1).getTicker());
    assertEquals(200, allPositions.get(1).getNumOfShares());
    assertEquals(10250.0, allPositions.get(1).getValuePaid(), 0.01);
  }

  @Test
  public void testDeleteById() {
    Position position = new Position();
    position.setTicker("STOCK1");
    position.setNumOfShares(100);
    position.setValuePaid(7000.0);

    positionDao.save(position);
    positionDao.deleteById("STOCK1");

    Optional<Position> foundPosition = positionDao.findById("STOCK1");
    assertTrue(!foundPosition.isPresent());
  }

  @Test
  public void testDeleteAll() {
    Position position1 = new Position();
    position1.setTicker("STOCK1");
    position1.setNumOfShares(100);
    position1.setValuePaid(30000.0);

    Position position2 = new Position();
    position2.setTicker("STOCK2");
    position2.setNumOfShares(200);
    position2.setValuePaid(68000.0);


    positionDao.save(position1);
    positionDao.save(position2);
    positionDao.deleteAll();

    List<Position> allPositions = (List<Position>) positionDao.findAll();
    assertTrue(allPositions.isEmpty());
  }
}
