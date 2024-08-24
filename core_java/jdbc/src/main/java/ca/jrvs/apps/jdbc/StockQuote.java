package ca.jrvs.apps.jdbc;

import java.net.http.HttpClient;

class StockQuote {
  public static void main(String[] args) {
    HttpClient httpClient = HttpClient.newHttpClient();
    QuoteHttpHelper quoteHttpHelper = new QuoteHttpHelper(httpClient);
    Quote myQuote = quoteHttpHelper.fetchQuoteInfo("MSFT");
    System.out.println(myQuote.toString());
  }
}