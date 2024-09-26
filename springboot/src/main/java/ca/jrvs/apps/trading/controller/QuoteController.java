package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dto.EODQuote;
import ca.jrvs.apps.trading.service.QuoteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/quote")
public class QuoteController {
    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService){
        this.quoteService = quoteService;
    }
    @GetMapping("/eod/ticker/{ticker}")
    public ResponseEntity<EODQuote> getQuote(@PathVariable String ticker) throws JsonProcessingException {
        return new ResponseEntity<>(quoteService.findEODQuoteByTicker(ticker), HttpStatus.OK);
    }
}
