package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dto.EODQuoteRequestDTO;
import ca.jrvs.apps.trading.dto.MarketOrderDTO;
import ca.jrvs.apps.trading.model.*;
import ca.jrvs.apps.trading.repository.*;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {
    private QuoteService quoteService;
    private TraderAccountService traderAccountService;
    private SecurityOrderDao securityOrderDao;
    private TraderDao traderDao;
    private AccountDao accountDao;
    private PositionDao positionDao;

    @Autowired
    public OrderService(PositionDao positionDao, QuoteService quoteService, TraderDao traderDao, AccountDao accountDao, TraderAccountService traderAccountService, SecurityOrderDao securityOrderDao) {
        this.traderDao = traderDao;
        this.accountDao = accountDao;
        this.quoteService = quoteService;
        this.traderAccountService = traderAccountService;
        this.securityOrderDao = securityOrderDao;
        this.positionDao = positionDao;
    }

    /**
     * Execute a market order
     * - validate the order (e.g. size and ticker)
     * - create a securityOrder
     * - handle buy or sell orders
     * - buy order : check account balance
     * - sell order : check position for the ticker/symbol
     * - do not forget to update the securityOrder.status
     * - save and return securityOrder
     * <p>
     * NOTE: you are encouraged to make some helper methods (protected or private)
     *
     * @param orderData market order
     * @return SecurityOrder from security_order table
     * @throws DataAccessException      if unable to get data from DAO
     * @throws IllegalArgumentException for invalid inputs
     */
    public SecurityOrder executeMarketOrder(MarketOrderDTO orderData) {
        String ticker = orderData.getTicker();
        Optional<Trader> trader = traderDao.findById(orderData.getTraderId());

        if (ticker == null || ticker.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid ticker provided");
        } else if (orderData.getSize() <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        } else if (trader.isEmpty()) {
            throw new IllegalArgumentException("Invalid trader id provided.");
        }

        Account account = accountDao.getAccountByTraderId(trader.get().getId());

        SecurityOrder securityOrder = new SecurityOrder();
        securityOrder.setAccountId(account.getId());
        securityOrder.setTicker(ticker);
        securityOrder.setSize(orderData.getSize());
        securityOrder.setStatus("FILLED");

        if (orderData.getOption().equals("BUY")) {
            handleBuyMarketOrder(orderData, securityOrder, account);
        } else if (orderData.getOption().equals("SELL")) {
            handleSellMarketOrder(orderData, securityOrder, account);
        } else {
            throw new IllegalArgumentException("Invalid option provided");
        }

        return securityOrder;
    }

    /**
     * Helper method to execute a buy order
     *
     * @param marketOrderDto user order
     * @param securityOrder  to be saved in database
     * @param account        account
     */
    protected void handleBuyMarketOrder(MarketOrderDTO marketOrderDto, SecurityOrder securityOrder, Account account) {
        EODQuoteRequestDTO eodQuote = quoteService.findEODQuoteByTicker(marketOrderDto.getTicker());//get quote

        if (marketOrderDto.getSize() > eodQuote.getAskSize()) {  //check if size to purchase is avail
            throw new IllegalArgumentException("Cannot purchase more shares than available.");
        } else if ((marketOrderDto.getSize() * eodQuote.getAskPrice()) > account.getAmount()) {  //check if enough balance
            throw new RuntimeException("Insufficient funds available for purchase");
        }

        traderAccountService.withdraw(marketOrderDto.getTraderId(), (double) (marketOrderDto.getSize() * eodQuote.getAskPrice()));  //subtract balance from account

        securityOrder.setPrice((double) eodQuote.getAskPrice()); //set price of order

        securityOrderDao.save(securityOrder); //save security order
    }

    /**
     * Helper method to execute a sell order
     *
     * @param marketOrderDto user order
     * @param securityOrder  to be saved in database
     * @param account        account
     */
    protected void handleSellMarketOrder(MarketOrderDTO marketOrderDto, SecurityOrder securityOrder, Account account) {
        EODQuoteRequestDTO eodQuote = quoteService.findEODQuoteByTicker(marketOrderDto.getTicker());//get quote
        Optional<Position> existingPosition = positionDao.findByTicker(marketOrderDto.getTicker());

        if (existingPosition.isEmpty() || existingPosition.get().getPosition() < marketOrderDto.getSize()) { //check if enough shares exist to sell
            throw new IllegalArgumentException("Cannot sell more shares than available.");
        }

        traderAccountService.deposit(marketOrderDto.getTraderId(), (double) (marketOrderDto.getSize() * eodQuote.getBidPrice()));  //add sale profit to account

        securityOrder.setPrice((double) eodQuote.getBidPrice()); //set price of order
        securityOrder.setSize(securityOrder.getSize() * -1); //negative size for a sell order
        securityOrderDao.save(securityOrder); //save security order
    }
}
