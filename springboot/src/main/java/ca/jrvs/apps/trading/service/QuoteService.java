package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dto.EODQuote;
import ca.jrvs.apps.trading.repository.QuoteDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuoteService {
    MarketDataDao marketDataDao;
    QuoteDao quoteDao;

    public QuoteService(MarketDataDao marketDataDao, QuoteDao quoteDao){
        this.marketDataDao = marketDataDao;
        this.quoteDao = quoteDao;
    }

    /**
     * Find an IexQuote
     * @param ticker
     * @return IexQuote object
     * @throws IllegalArgumentException if ticker is invalid
     */
    public EODQuote findEODQuoteByTicker(String ticker) throws JsonProcessingException {
        Optional<EODQuote> eodQuote = marketDataDao.findById(ticker);
        if (eodQuote.isEmpty()) {
            throw new RuntimeException("No quote found for given ticker.");
        }

        return eodQuote.get();
    }

    /**
     * Update quote table against IEX source
     *
     * - get all quotes from the db
     * - for each ticker get IexQuote
     * - convert IexQuote to Quote entity
     * - persist quote to db
     *
     * @throws ResourceNotFoundException if ticker is not found from IEX
     * @throws DataAccessException if unable to retrieve data
     * @throws IllegalArgumentException for invalid input
     */
    public void updateMarketData() {
        //TODO
    }

    /**
     * Validate (against IEX) and save given tickers to quote table
     *
     * - get IexQuote(s)
     * - convert each IexQuote to Quote entity
     * - persist the quote to db
     *
     * @param tickers
     * @return list of converted quote entities
     * @throws IllegalArgumentException if ticker is not found from IEX
     */
    public List<Quote> saveQuotes(List<String> tickers) {
        //TODO
    }

    /**
     * Find an IexQuote from the given ticker
     *
     * @param ticker
     * @return corresponding IexQuote object
     * @throws IllegalArgumentExcpetion if ticker is invalid
     */
    public IexQuote findIexQuoteByTicker(String ticker) {
        //TODO
    }

    /**
     * Update a given quote to the quote table without validation
     *
     * @param quote entity to save
     * @return the saved quote entity
     */
    public Quote saveQuote(Quote quote) {
        //TODO
    }

    /**
     * Find all quotes from the quote table
     *
     * @return a list of quotes
     */
    public List<Quote> findAllQuotes() {
        //TODO
    }
}
