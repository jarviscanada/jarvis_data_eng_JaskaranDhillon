package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dto.EODQuoteRequestDTO;
import ca.jrvs.apps.trading.service.DailyListService;
import ca.jrvs.apps.trading.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/quote")
public class QuoteController {
    private final QuoteService quoteService;
    private final DailyListService dailyListService;

    public QuoteController(QuoteService quoteService, DailyListService dailyListService) {
        this.quoteService = quoteService;
        this.dailyListService = dailyListService;
    }

    @GetMapping("/eod/ticker/{ticker}")
    public ResponseEntity<EODQuoteRequestDTO> getQuote(@PathVariable("ticker") String ticker) {
        EODQuoteRequestDTO quote = quoteService.findEODQuoteByTicker(ticker);
        return new ResponseEntity<>(quote, HttpStatus.OK);
    }

    @PutMapping("/eodMarketData")
    public ResponseEntity<Void> getQuote() {
        quoteService.updateMarketData();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/tickerId/{tickerId}")
    public ResponseEntity<Void> createQuote(@PathVariable("tickerId") String tickerId) {
        dailyListService.addTicker(tickerId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/dailyList")
    public ResponseEntity<Set<String>> getDailyList() {
        return new ResponseEntity<>(dailyListService.getList(), HttpStatus.OK);
    }
}
