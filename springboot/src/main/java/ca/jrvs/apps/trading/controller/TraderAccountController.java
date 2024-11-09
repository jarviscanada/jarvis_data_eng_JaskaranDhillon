package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dto.TraderAccountViewDto;
import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.model.Trader;
import ca.jrvs.apps.trading.service.TraderAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/trader")
public class TraderAccountController {
    private final TraderAccountService traderAccountService;

    @Autowired
    public TraderAccountController(TraderAccountService traderAccountService) {
        this.traderAccountService = traderAccountService;
    }

    @PostMapping
    public ResponseEntity<TraderAccountViewDto> createTrader(@Valid @RequestBody Trader trader) {
        return new ResponseEntity<>(traderAccountService.createTraderAndAccount(trader), HttpStatus.CREATED);
    }

    @PostMapping("/firstname/{firstname}/lastname/{lastname}/dob/{dob}/country/{country}/email/{email}")
    public ResponseEntity<TraderAccountViewDto> createTrader(@PathVariable("firstname") String firstname, @PathVariable("lastname") String lastname, @PathVariable("dob") LocalDate dob, @PathVariable("country") String country, @PathVariable("email") String email) {
        Trader trader = new Trader();
        trader.setFirstName(firstname);
        trader.setLastName(lastname);
        trader.setDob(dob);
        trader.setCountry(country);
        trader.setEmail(email);

        return new ResponseEntity<>(traderAccountService.createTraderAndAccount(trader), HttpStatus.CREATED);
    }

    @DeleteMapping("/traderId/{traderId}")
    public ResponseEntity<Void> deleteTrader(@PathVariable("traderId") Integer traderId) {
        traderAccountService.deleteTraderById(traderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/deposit/traderId/{traderId}/amount/{amount}")
    public ResponseEntity<Account> depositFund(@PathVariable("traderId") Integer traderId, @PathVariable("amount") Double amount) {
        return new ResponseEntity<>(traderAccountService.deposit(traderId, amount), HttpStatus.OK);
    }

    @PutMapping("/withdraw/traderId/{traderId}/amount/{amount}")
    public ResponseEntity<Account> withdrawFund(@PathVariable("traderId") Integer traderId, @PathVariable("amount") Double amount) {
        return new ResponseEntity<>(traderAccountService.withdraw(traderId, amount), HttpStatus.OK);
    }
}
