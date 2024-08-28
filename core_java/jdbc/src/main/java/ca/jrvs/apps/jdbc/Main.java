package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.controller.StockQuoteController;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.http.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.service.QuoteService;
import ca.jrvs.apps.jdbc.service.PositionService;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.HttpClient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Main {

  public static void main(String[] args) {
    Map<String, String> properties = new HashMap<>();
    try (BufferedReader br = new BufferedReader(
        new FileReader("src/main/resources/properties.txt"))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] tokens = line.split(":");
        properties.put(tokens[0], tokens[1]);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      Class.forName(properties.get("db-class"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    HttpClient client = HttpClient.newHttpClient();

    String url =
        "jdbc:postgresql://" + properties.get("server") + ":" + properties.get("port") + "/"
            + properties.get("database");

    try (Connection c = DriverManager.getConnection(url, properties.get("username"),
        properties.get("password"))) {
      QuoteDao qRepo = new QuoteDao(c);
      PositionDao pRepo = new PositionDao(c);
      QuoteHttpHelper rcon = new QuoteHttpHelper(properties.get("api-key"), client);
      QuoteService sQuote = new QuoteService(qRepo, rcon);
      PositionService sPos = new PositionService(pRepo, qRepo);
      StockQuoteController con = new StockQuoteController(sQuote, sPos);
      con.initClient();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}