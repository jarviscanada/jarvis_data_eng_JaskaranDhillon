package ca.jrvs.apps.jdbc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class QuoteHttpHelperTest {

  @Mock
  private HttpClient httpClient;

  @Mock
  private HttpResponse<String> httpResponse;

  @InjectMocks
  private QuoteHttpHelper quoteHttpHelper;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void fetchQuoteInfo_success() throws Exception {

    String jsonResponse = "{ \"Global Quote\": { \"01. symbol\": \"MSFT\", \"02. open\": \"332.3800\", \"03. high\": \"333.8300\", \"04. low\": \"326.3600\", \"05. price\": \"327.7300\", \"06. volume\": \"21085695\", \"07. latest trading day\": \"2023-10-13\", \"08. previous close\": \"331.1600\", \"09. change\": \"-3.4300\", \"10. change percent\": \"-1.0358%\" } }";
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpResponse.statusCode()).thenReturn(200);

    Quote result = quoteHttpHelper.fetchQuoteInfo("MSFT");

    assertNotNull(result);
    assertEquals("MSFT", result.getTicker());
    assertEquals(332.38, result.getOpen(), 0.001);
    assertEquals(333.83, result.getHigh(), 0.001);
    assertEquals(326.36, result.getLow(), 0.001);
    assertEquals(327.73, result.getPrice(), 0.001);
    assertEquals(21085695, result.getVolume());
    assertEquals("Fri Oct 13 00:00:00 UTC 2023", result.getLatestTradingDay().toString());
    assertEquals(331.16, result.getPreviousClose(), 0.001);
    assertEquals(-3.43, result.getChange(), 0.001);
    assertEquals("-1.0358%", result.getChangePercent());
    assertNotNull(result.getTimestamp());
  }
}