package ca.jrvs.apps.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;

public class QuoteHttpHelper {

  private String apiKey = "22640d375fmsh51d8b22af25d346p19d5cdjsnd2fc2a5460ae";
  private HttpClient httpClient;

  public QuoteHttpHelper(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /**
   * Fetch latest quote data from Alpha Vantage endpoint
   *
   * @param symbol
   * @return Quote with latest data
   * @throws IllegalArgumentException - if no data was found for the given symbol
   */
  public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(
            "https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol=" + symbol
                + "&datatype=json"))
        .header("X-RapidAPI-Key", "22640d375fmsh51d8b22af25d346p19d5cdjsnd2fc2a5460ae")
        .header("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
        .method("GET", HttpRequest.BodyPublishers.noBody())
        .build();
    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode = objectMapper.readTree(response.body());
      JsonNode globalQuoteNode = rootNode.path("Global Quote");
      Quote quote = objectMapper.treeToValue(globalQuoteNode, Quote.class);
      quote.setTimestamp(new Timestamp(System.currentTimeMillis()));

      return quote;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}