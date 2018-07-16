package com.codethen.bankapi.domain.service;

import com.codethen.bankapi.domain.errors.AccountNotExistsException;
import com.codethen.bankapi.domain.errors.CurrenciesDontMatchException;
import com.codethen.bankapi.domain.errors.NotEnoughUnitsException;
import com.codethen.bankapi.domain.model.Account;
import com.codethen.bankapi.domain.model.Currency;
import com.codethen.bankapi.domain.repository.AccountRepository;

public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Returns the {@link Account} for the given username.
     * @throws AccountNotExistsException if that user doesn't have an {@link Account}.
     */
    public Account findByUsername(String username) {
        final Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new AccountNotExistsException();
        }
        return account;
    }


    /**
     * Transfers money from one account to another.
     * @throws AccountNotExistsException if any of the given users doesn't have an account
     * @throws NotEnoughUnitsException if the source account doesn't have enough funds
     * @throws CurrenciesDontMatchException if accounts don't have the same {@link Currency}
     */
    public void transferMoney(String fromUser, String toUser, int units) {

        final Account accountFrom = findByUsername(fromUser);
        final Account accountTo = findByUsername(toUser);

        if (accountFrom.getAmount().getCurrency() != accountTo.getAmount().getCurrency()) {
            throw new CurrenciesDontMatchException();
        }

        accountFrom.getAmount().removeUnits(units);
        accountTo.getAmount().addUnits(units);

        // TODO: Add transaction for these changes
        accountRepository.updateAccountAmount(fromUser, accountFrom.getAmount());
        accountRepository.updateAccountAmount(toUser, accountTo.getAmount());
    }
}
