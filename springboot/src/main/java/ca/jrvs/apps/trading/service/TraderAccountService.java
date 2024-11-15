package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dto.EODQuoteRequestDTO;
import ca.jrvs.apps.trading.dto.TraderAccountViewDto;
import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.model.Position;
import ca.jrvs.apps.trading.model.Quote;
import ca.jrvs.apps.trading.model.Trader;
import ca.jrvs.apps.trading.repository.AccountDao;
import ca.jrvs.apps.trading.repository.PositionDao;
import ca.jrvs.apps.trading.repository.SecurityOrderDao;
import ca.jrvs.apps.trading.repository.TraderDao;
import ca.jrvs.apps.trading.util.QuoteUtils;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import java.util.List;
import java.util.Set;

@Service
public class TraderAccountService {
    private TraderDao traderDao;
    private AccountDao accountDao;
    private PositionDao positionDao;

    private SecurityOrderDao securityOrderDao;

    @Autowired
    public TraderAccountService(TraderDao traderDao, AccountDao accountDao, PositionDao positionDao, SecurityOrderDao securityOrderDao) {
        this.accountDao = accountDao;
        this.securityOrderDao = securityOrderDao;
        this.traderDao = traderDao;
        this.positionDao = positionDao;
    }

    /**
     * Create a new trader and initialize a new account with 0 amount
     * - validate user input (all fields must be non empty)
     * - create a trader
     * - create an account
     * - create, setup, and return a new traderAccountView
     * <p>
     * Assumption: to simplify the logic, each trader has only one account where traderId == accountId
     *
     * @param trader cannot be null. All fields cannot be null except for id (auto-generated by db)
     * @return traderAccountView
     * @throws IllegalArgumentException if a trader has null fields or id is not null
     */
    @Transactional
    public TraderAccountViewDto createTraderAndAccount(Trader trader) {
        if (trader == null) {
            throw new IllegalArgumentException("Trader cannot be null");
        }

        traderDao.save(trader);

        Account traderAccount = new Account();
        traderAccount.setTraderId(trader.getId());
        traderAccount.setAmount(0);

        accountDao.save(traderAccount);


        TraderAccountViewDto traderAccountViewDto = new TraderAccountViewDto();
        traderAccountViewDto.setId(trader.getId());
        traderAccountViewDto.setFirstName(trader.getFirstName());
        traderAccountViewDto.setLastName(trader.getLastName());
        traderAccountViewDto.setDob(trader.getDob());
        traderAccountViewDto.setCountry(trader.getCountry());
        traderAccountViewDto.setEmail(trader.getEmail());
        traderAccountViewDto.setAmount(0);

        return traderAccountViewDto;
    }

    /**
     * A trader can be deleted if and only if it has no open position and 0 cash balance
     * - validate traderId
     * - get trader account by traderId and check account balance
     * - get positions by accountId and check positions
     * - delete all securityOrders, account, trader (in this order)
     *
     * @param traderId must not be null
     * @throws IllegalArgumentException if traderId is null or not found or unable to delete
     */
    public void deleteTraderById(Integer traderId) {
        if (traderId == null) {
            throw new IllegalArgumentException("No trader id provided.");
        }

        Account traderAccount = accountDao.getAccountByTraderId(traderId);

        if (traderAccount == null) {
            throw new IllegalArgumentException("Account not found.");
        } else if (traderAccount.getAmount() != 0) {
            throw new IllegalArgumentException("Account balance is not 0!");
        }

        List<Position> positions = positionDao.findAllByAccountId(traderAccount.getId());

        for (Position pos : positions) {
            if (pos.getPosition() != 0) {
                throw new IllegalArgumentException("Cannot delete trader with open positions");
            }
        }

        securityOrderDao.deleteByAccountId((long) traderAccount.getId());
        accountDao.deleteById(traderAccount.getId());
        traderDao.deleteById(traderId);
    }

    /**
     * Deposit a fund to an account by traderId
     * - validate user input
     * - find account by trader id
     * - update the amount accordingly
     *
     * @param traderId must not be null
     * @param fund     must be greater than 0
     * @return updated Account
     * @throws IllegalArgumentException if traderId is null or not found,
     *                                  and fund is less than or equal to 0
     */
    public Account deposit(Integer traderId, Double fund) {
        if (traderId == null) {
            throw new IllegalArgumentException("No trader id provided.");
        } else if (fund <= 0) {
            throw new IllegalArgumentException("Fund must be greater than 0.");
        }

        Account updatedAccount = accountDao.getAccountByTraderId(traderId);
        if (updatedAccount == null) {
            throw new IllegalArgumentException("Account not found.");
        }
        updatedAccount.setAmount(updatedAccount.getAmount() + fund);
        accountDao.save(updatedAccount);

        return updatedAccount;
    }

    /**
     * Withdraw a fund to an account by traderId
     * - validate user input
     * - find account by trader id
     * - update the amount accordingly
     *
     * @param traderId must not be null
     * @param fund     must be greater than 0
     * @return updated Account
     * @throws IllegalArgumentException if traderId is null or not found,
     *                                  and fund is less than or equal to 0
     */
    public Account withdraw(Integer traderId, Double fund) {
        if (traderId == null) {
            throw new IllegalArgumentException("No trader id provided.");
        } else if (fund <= 0) {
            throw new IllegalArgumentException("Fund must be greater than 0.");
        }

        Account updatedAccount = accountDao.getAccountByTraderId(traderId);
        if (updatedAccount == null) {
            throw new IllegalArgumentException("Account not found.");
        }
        updatedAccount.setAmount(updatedAccount.getAmount() - fund);
        accountDao.save(updatedAccount);

        return updatedAccount;
    }
}
