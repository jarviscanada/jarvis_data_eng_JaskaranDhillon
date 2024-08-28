package ca.jrvs.apps.jdbc.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class PositionServiceTest {

  private PositionDao positionDao;
  private QuoteDao quoteDao;
  private PositionService positionService;

  @Before
  public void setUp() {
    positionDao = mock(PositionDao.class);
    quoteDao = mock(QuoteDao.class);
    positionService = new PositionService(positionDao, quoteDao);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTicker() {
    positionService.buy("   ", 10, 20.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidNumberOfShares() {
    positionService.buy("STOCK1", 0, 20.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPrice() {
    positionService.buy("STOCK1", 10, -20.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSymbolDataNotFound() {
    when(quoteDao.findById("STOCK1")).thenReturn(Optional.empty());
    positionService.buy("STOCK1", 10, 20.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuyInsufficientVolume() {
    Quote quote = new Quote();
    quote.setTicker("STOCK1");
    quote.setVolume(100);
    when(quoteDao.findById("STOCK1")).thenReturn(Optional.of(quote));

    positionService.buy("STOCK1", 250, 100.0);
  }

  @Test
  public void testSuccessfulNewBuy() {
    Quote quote = new Quote();
    quote.setTicker("STOCK1");
    quote.setVolume(100);
    when(quoteDao.findById("STOCK1")).thenReturn(Optional.of(quote));

    Position position = new Position();
    position.setTicker("STOCK1");
    position.setNumOfShares(10);
    position.setValuePaid(1000.0);
    when(positionDao.save(any(Position.class))).thenReturn(position);

    Position result = positionService.buy("STOCK1", 10, 100.0);

    assertNotNull(result);
    assertEquals("STOCK1", result.getTicker());
    assertEquals(10, result.getNumOfShares());
    assertEquals(1000.0, result.getValuePaid(), 0.01);
  }

  @Test
  public void testSuccessfulExistingBuy() {
    Quote quote = new Quote();
    quote.setTicker("STOCK1");
    quote.setVolume(100);
    when(quoteDao.findById("STOCK1")).thenReturn(Optional.of(quote));

    Position existingPosition = new Position();
    existingPosition.setTicker("STOCK1");
    existingPosition.setNumOfShares(10);
    existingPosition.setValuePaid(1000.0);
    when(positionDao.findById("STOCK1")).thenReturn(Optional.of(existingPosition));

    Position updatedPosition = new Position();
    updatedPosition.setTicker("STOCK1");
    updatedPosition.setNumOfShares(15);
    updatedPosition.setValuePaid(1500.0);
    when(positionDao.save(any(Position.class))).thenReturn(updatedPosition);

    Position result = positionService.buy("STOCK1", 5, 100.0);

    assertNotNull(result);
    assertEquals("STOCK1", result.getTicker());
    assertEquals(15, result.getNumOfShares());
    assertEquals(1500.0, result.getValuePaid(), 0.01);
  }

  @Test
  public void testSell() {
    positionService.sell("STOCK1");
    verify(positionDao).deleteById("STOCK1");
  }
}
