import atm.ATM;
import atm.ATMImpl;
import cell.CellHolder;
import cell.CellHolderBuilder;
import currency.Currency;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


class ATMImplTest {

    private CellHolder cellHolder;

    @BeforeEach
    void setUp() {
        cellHolder = new CellHolderBuilder()
                .add(Currency.NOMINAL1000)
                .add(Currency.NOMINAL2000)
                .add(Currency.NOMINAL5000)
                .add(Currency.NOMINAL50)
                .add(Currency.NOMINAL100)
                .add(Currency.NOMINAL200)
                .add(Currency.NOMINAL500)
                .build();
    }

    @SneakyThrows
    @Test
    void withdraw() {

        Map<Currency, Integer> expectedResult = new HashMap<>();

        expectedResult.put((Currency.NOMINAL5000),2);
        expectedResult.put(Currency.NOMINAL2000,1);
        expectedResult.put(Currency.NOMINAL1000,1);
        expectedResult.put(Currency.NOMINAL100,3);

        long expectedRemainBalance = 3000;
        ATM atm = new ATMImpl(cellHolder);

        atm.deposit(Currency.NOMINAL5000,2);
        atm.deposit(Currency.NOMINAL2000,2);
        atm.deposit(Currency.NOMINAL1000,2);
        atm.deposit(Currency.NOMINAL200,0);
        atm.deposit(Currency.NOMINAL100,3);

        Map<Currency, Integer> realResult = atm.withdraw(13300);

        Assertions.assertTrue(expectedResult.size() == realResult.size());
        realResult.keySet().stream()
                .map(currency -> expectedResult.get(currency) == realResult.get(currency))
                .forEach(Assertions::assertTrue);
        Assertions.assertTrue(atm.getBalance() == expectedRemainBalance);
    }

    @SneakyThrows
    @Test
    void getBalance() {

        long expectedRemainBalance = 16300;
        ATM atm = new ATMImpl(cellHolder);

        Assertions.assertTrue(atm.getBalance() == 0);

        atm.deposit(Currency.NOMINAL5000,2);
        atm.deposit(Currency.NOMINAL2000,2);
        atm.deposit(Currency.NOMINAL1000,2);
        atm.deposit(Currency.NOMINAL200,0);
        atm.deposit(Currency.NOMINAL100,3);

        Assertions.assertTrue(atm.getBalance() == expectedRemainBalance);
    }

    @SneakyThrows
    @Test
    void deposit() {

        long expectedRemainBalance = 16300;
        ATM atm = new ATMImpl(cellHolder);

        atm.deposit(Currency.NOMINAL5000,2);
        atm.deposit(Currency.NOMINAL2000,2);
        atm.deposit(Currency.NOMINAL1000,2);
        atm.deposit(Currency.NOMINAL200,0);
        atm.deposit(Currency.NOMINAL100,3);

        Assertions.assertTrue(atm.getBalance() == expectedRemainBalance);
    }
}