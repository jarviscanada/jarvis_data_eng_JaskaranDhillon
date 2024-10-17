package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dto.EODQuoteRequestDTO;
import ca.jrvs.apps.trading.exception.ResourceNotFoundException;
import ca.jrvs.apps.trading.model.Quote;
import ca.jrvs.apps.trading.repository.QuoteDao;
import ca.jrvs.apps.trading.util.QuoteUtils;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuoteService {
    private MarketDataDao marketDataDao;
    private QuoteDao quoteDao;

    @Autowired
    public QuoteService(MarketDataDao marketDataDao, QuoteDao quoteDao) {
        this.marketDataDao = marketDataDao;
        this.quoteDao = quoteDao;
    }

    /**
     * Find an EODQuote
     *
     * @param ticker
     * @return EODQuote object
     * @throws IllegalArgumentException  if ticker is invalid
     * @throws ResourceNotFoundException if data not found for given ticker
     */
    public EODQuoteRequestDTO findEODQuoteByTicker(String ticker) {
        Optional<EODQuoteRequestDTO> eodQuote = marketDataDao.findById(ticker);
        if (eodQuote.isEmpty()) {
            throw new ResourceNotFoundException("No quote found for given ticker.");
        }

        Quote quote = QuoteUtils.buildQuoteFromEODQuote(eodQuote.get());
        quoteDao.save(quote);

        return eodQuote.get();
    }

    /**
     * Update quote table against EOD source
     * <p>
     * - get all quotes from the db
     * - for each ticker get IexQuote
     * - convert IexQuote to Quote entity
     * - persist quote to db
     *
     * @throws ResourceNotFoundException if ticker is not found from EOD API
     * @throws DataAccessException       if unable to retrieve data
     * @throws IllegalArgumentException  for invalid input
     */
    public void updateMarketData() {
        List<Quote> quotes = findAllQuotes();
        if (quotes.isEmpty()) {
            throw new RuntimeException("No quotes found to update.");
        }

        for (Quote quote : quotes) {
            EODQuoteRequestDTO eodQuoteRequestDTO = findEODQuoteByTicker(quote.getTicker());
            Quote updatedQuote = QuoteUtils.buildQuoteFromEODQuote(eodQuoteRequestDTO);

            quoteDao.save(updatedQuote);
        }
    }

    /**
     * Validate (against EOD) and save given tickers to quote table
     * <p>
     * - get IexQuote(s)
     * - convert each IexQuote to Quote entity
     * - persist the quote to db
     *
     * @param tickers
     * @return list of converted quote entities
     * @throws IllegalArgumentException if ticker is not found from IEX
     */
    public List<Quote> saveQuotes(List<String> tickers) {
        List<Quote> savedQuotes = new ArrayList<>();
        for (String ticker : tickers) {
            EODQuoteRequestDTO eodQuoteRequestDTO = findEODQuoteByTicker(ticker);
            Quote quote = QuoteUtils.buildQuoteFromEODQuote(eodQuoteRequestDTO);
            quoteDao.save(quote);
            savedQuotes.add(quote);
        }

        return savedQuotes;
    }


    /**
     * Update a given quote to the quote table without validation
     *
     * @param quote entity to save
     * @return the saved quote entity
     */
    public Quote saveQuote(Quote quote) {
        return quoteDao.save(quote);
    }

    /**
     * Find all quotes from the quote table
     *
     * @return a list of quotes
     */
    public List<Quote> findAllQuotes() {
        List<Quote> quotes = quoteDao.findAll();
        if (quotes.isEmpty()) {
            throw new DataRetrievalFailureException("No quotes found in the database.");
        }

        return quotes;
    }
}
