package ca.jrvs.apps.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuoteDaoTest {

  private Connection connection;
  private QuoteDao quoteDao;

  @Before
  public void setUp() throws SQLException {
    // Credentials
    String url = "jdbc:postgresql://localhost:5432/stock_quote";
    String user = "postgres";
    String password = "password";

    connection = DriverManager.getConnection(url, user, password);
    quoteDao = new QuoteDao(connection);

    // Starting with clean database
    connection.createStatement().execute("TRUNCATE TABLE quote CASCADE");
  }

  @After
  public void tearDown() throws SQLException {
    connection.close();
  }

  @Test
  public void testSaveAndFindById() {
    Quote quote = new Quote();
    quote.setTicker("STOCK1");
    quote.setOpen(100.1);
    quote.setHigh(105.2);
    quote.setLow(99.36);
    quote.setPrice(103.73);
    quote.setVolume(1234567);
    quote.setLatestTradingDay(new Date());
    quote.setPreviousClose(102.16);
    quote.setChange(1.57);
    quote.setChangePercent("1.53%");
    quote.setTimestamp(new Timestamp(System.currentTimeMillis()));

    quoteDao.save(quote);

    Optional<Quote> foundQuote = quoteDao.findById("STOCK1");
    assertTrue(foundQuote.isPresent());

    Quote q = foundQuote.get();
    assertEquals(quote.getTicker(), q.getTicker());
    assertEquals(quote.getOpen(), q.getOpen(), 0.01);
    assertEquals(quote.getPrice(), q.getPrice(), 0.01);
    assertEquals(quote.getChange(), q.getChange(), 0.01);
  }

  @Test
  public void testFindAll() {
    Quote quote1 = new Quote();
    quote1.setTicker("STOCK1");
    quote1.setOpen(500.5);
    quote1.setHigh(520.0);
    quote1.setLow(495.3);
    quote1.setPrice(510.75);
    quote1.setVolume(5000000);
    quote1.setLatestTradingDay(new Date());
    quote1.setPreviousClose(505.00);
    quote1.setChange(5.75);
    quote1.setChangePercent("1.14%");
    quote1.setTimestamp(new Timestamp(System.currentTimeMillis()));

    Quote quote2 = new Quote();
    quote2.setTicker("STOCK2");
    quote2.setOpen(50.00);
    quote2.setHigh(52.00);
    quote2.setLow(49.00);
    quote2.setPrice(51.25);
    quote2.setVolume(1000000);
    quote2.setLatestTradingDay(new Date());
    quote2.setPreviousClose(50.50);
    quote2.setChange(0.75);
    quote2.setChangePercent("1.49%");
    quote2.setTimestamp(new Timestamp(System.currentTimeMillis()));

    quoteDao.save(quote1);
    quoteDao.save(quote2);

    List<Quote> allQuotes = (List<Quote>) quoteDao.findAll();
    assertEquals(2, allQuotes.size());

    assertEquals("STOCK1", allQuotes.get(0).getTicker());
    assertEquals(510.75, allQuotes.get(0).getPrice(), 0.01);
    assertEquals(5.75, allQuotes.get(0).getChange(), 0.01);

    assertEquals("STOCK2", allQuotes.get(1).getTicker());
    assertEquals(51.25, allQuotes.get(1).getPrice(), 0.01);
    assertEquals(0.75, allQuotes.get(1).getChange(), 0.01);
  }

  @Test
  public void testDeleteById() {
    Quote quote = new Quote();
    quote.setTicker("STOCK1");
    quote.setOpen(700.00);
    quote.setHigh(710.00);
    quote.setLow(690.00);
    quote.setPrice(705.00);
    quote.setVolume(1000000);
    quote.setLatestTradingDay(new Date());
    quote.setPreviousClose(695.00);
    quote.setChange(10.00);
    quote.setChangePercent("1.43%");
    quote.setTimestamp(new Timestamp(System.currentTimeMillis()));


    quoteDao.save(quote);
    quoteDao.deleteById("STOCK1");

    Optional<Quote> foundQuote = quoteDao.findById("STOCK1");
    assertTrue(!foundQuote.isPresent());
  }

  @Test
  public void testDeleteAll() {
    Quote quote1 = new Quote();
    quote1.setTicker("STOCK1");
    quote1.setOpen(300.00);
    quote1.setHigh(310.00);
    quote1.setLow(290.00);
    quote1.setPrice(305.00);
    quote1.setVolume(800000);
    quote1.setLatestTradingDay(new Date());
    quote1.setPreviousClose(295.00);
    quote1.setChange(10.00);
    quote1.setChangePercent("1.68%");
    quote1.setTimestamp(new Timestamp(System.currentTimeMillis()));

    Quote quote2 = new Quote();
    quote2.setTicker("STOCK2");
    quote2.setOpen(3400.00);
    quote2.setHigh(3450.00);
    quote2.setLow(3350.00);
    quote2.setPrice(3425.00);
    quote2.setVolume(600000);
    quote2.setLatestTradingDay(new Date());
    quote2.setPreviousClose(3380.00);
    quote2.setChange(45.00);
    quote2.setChangePercent("1.33%");
    quote2.setTimestamp(new Timestamp(System.currentTimeMillis()));


    quoteDao.save(quote1);
    quoteDao.save(quote2);
    quoteDao.deleteAll();

    List<Quote> allQuotes = (List<Quote>) quoteDao.findAll();
    assertTrue(allQuotes.isEmpty());
  }
}
