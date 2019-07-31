package atm;

import currency.Currency;
import java.util.Map;

public interface ATM {

    Map<Currency,Integer> withdraw(long sum) throws Exception;
    long getBalance();
    void deposit(Currency currency, int count) throws Exception;

}
