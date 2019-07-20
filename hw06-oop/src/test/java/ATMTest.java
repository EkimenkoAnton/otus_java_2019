import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


class ATMTest {

    private CellHolder cellHolder;

    @BeforeEach
    void setUp() {
        cellHolder = new CellHolder.Builder()
                .add(new Currency(50))
                .add(new Currency(100))
                .add(new Currency(200))
                .add(new Currency(500))
                .add(new Currency(1000))
                .add(new Currency(2000))
                .add(new Currency(5000))
                .build();
    }

    @SneakyThrows
    @Test
    void withdraw() {
        Map<Currency, Integer> expectedResult = new HashMap<>();
        expectedResult.put(new Currency(5000),2);
        expectedResult.put(new Currency(2000),1);
        expectedResult.put(new Currency(1000),1);
        expectedResult.put(new Currency(100),3);

        long expectedRemainBalance = 3000;

        IATM atm = new ATM(cellHolder);
        atm.deposit(new Currency(5000),2);
        atm.deposit(new Currency(2000),2);
        atm.deposit(new Currency(1000),2);
        atm.deposit(new Currency(200),0);
        atm.deposit(new Currency(100),3);
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
        IATM atm = new ATM(cellHolder);
        Assertions.assertTrue(atm.getBalance() == 0);
        atm.deposit(new Currency(5000),2);
        atm.deposit(new Currency(2000),2);
        atm.deposit(new Currency(1000),2);
        atm.deposit(new Currency(200),0);
        atm.deposit(new Currency(100),3);
        Assertions.assertTrue(atm.getBalance() == expectedRemainBalance);
    }

    @SneakyThrows
    @Test
    void deposit() {
        long expectedRemainBalance = 16300;
        IATM atm = new ATM(cellHolder);
        atm.deposit(new Currency(5000),2);
        atm.deposit(new Currency(2000),2);
        atm.deposit(new Currency(1000),2);
        atm.deposit(new Currency(200),0);
        atm.deposit(new Currency(100),3);
        Assertions.assertTrue(atm.getBalance() == expectedRemainBalance);
    }
}