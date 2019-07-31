package cell;

import currency.Currency;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(exclude = { "size" })
public class Cell {

    @Getter private final Currency currency;
    @Getter private int size = 0;

    public Cell(Currency currency) {
        this.currency = currency;
    }

    public Cell(Currency currency, int size) {
        this.currency = currency;
        this.size = size;
    }

    public int withdraw(int count) throws Exception {
        if (count > size) throw new Exception("Insufficient funds");
        size -= count;
        return count;
    }

    public void deposit(int count) {
        size += count;
    }

}