package ca.jrvs.apps.trading.util;

import ca.jrvs.apps.trading.dto.EODQuoteRequestDTO;
import ca.jrvs.apps.trading.model.Quote;

public class QuoteUtils {
    /**
     * Helper method to map an IexQuote to a Quote entity
     * Note: 'iexQuote.getLatestPrice() == null' if the stock market is closed
     * Make sure to set a default value for number field(s)
     */
    public static Quote buildQuoteFromEODQuote(EODQuoteRequestDTO eodQuoteRequestDTO) {
        Quote quote = new Quote();
        quote.setTicker(eodQuoteRequestDTO.getTicker());
        quote.setLastPrice(eodQuoteRequestDTO.getLastPrice());
        quote.setAskPrice(eodQuoteRequestDTO.getAskPrice());
        quote.setAskSize(eodQuoteRequestDTO.getAskSize());
        quote.setBidPrice(eodQuoteRequestDTO.getBidPrice());
        quote.setBidSize(eodQuoteRequestDTO.getBidSize());

        return quote;
    }
}
