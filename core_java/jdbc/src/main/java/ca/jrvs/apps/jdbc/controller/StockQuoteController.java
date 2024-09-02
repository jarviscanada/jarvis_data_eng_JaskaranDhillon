package ca.jrvs.apps.jdbc.controller;

import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.service.PositionService;
import ca.jrvs.apps.jdbc.service.QuoteService;
import java.util.Optional;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockQuoteController {
  private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);
  private PositionService positionService;
  private QuoteService quoteService;

  public StockQuoteController(QuoteService quoteService, PositionService positionService) {
    this.positionService = positionService;
    this.quoteService = quoteService;
  }

  public void initClient() {
    Scanner scanner = new Scanner(System.in);
    logger.info("Application started...");
    while (true) {
      try {
        System.out.println(
            "Enter the symbol you're interested in, or type exit to stop the application: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        if (symbol.equals("EXIT")) {
          logger.info("Application exited.");
          break;
        }
        Optional<Quote> quoteFound = quoteService.fetchQuoteDataFromAPI(symbol);

        if (!quoteFound.isPresent()) {
          System.out.println("Invalid symbol provided, please try again...");
          continue;
        }

        Quote quote = quoteFound.get();
        System.out.println(quote.toString());
        boolean stockOptionLoop = true;
        while (stockOptionLoop) {
          System.out.println("What would you like to do?");
          System.out.println("1. Purchase more shares of this symbol");
          System.out.println("2. Sell all shares of this symbol");
          System.out.println("3. Calculate current profit");
          System.out.println("4. Return to main menu");

          String action = scanner.nextLine().trim();
          try {
            switch (action) {
              case "1":
                System.out.println("Enter the number of shares you wish to purchase: ");
                int numberOfShares = Integer.parseInt(scanner.nextLine().trim());

                Position position = positionService.buy(symbol, numberOfShares, quote.getPrice());
                System.out.println(
                    "Successfully purchased " + numberOfShares + " shares of " + symbol + " at $"
                        + quote.getPrice() + " per share.");
                break;
              case "2":
                positionService.sell(symbol);
                System.out.println("All shares of " + symbol + " sold.");
                break;
              case "3":
                System.out.println("If you sell all shares of " + symbol
                    + " right now, there will be a profit of " + positionService.calculateProfit(
                    symbol) + " dollars.");
                break;
              case "4":
                stockOptionLoop = false;
                break;
              default:
                System.out.println("Invalid choice. Please select a valid option.");
                break;
            }
          } catch (Exception e) {
            logger.error("Something went wrong...", e);
            System.out.println("Something went wrong, please try again...");
          }
        }
      } catch (Exception e) {
        logger.error("Something went wrong...", e);
        System.out.println("Something went wrong, please try again...");
      }
    }
    scanner.close();
  }
}

