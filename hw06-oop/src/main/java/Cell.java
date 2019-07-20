
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude = { "size" })
public class Cell implements Comparable<Cell>{
    private final Currency currency;
    private int size = 0;


    public Cell(Currency currency) {
        this.currency = currency;
    }

    public Cell(Currency currency, int size) {
        this.currency = currency;
        this.size = size;
    }

    public int getCurrencyDenomination() {
        return currency.getDenomenation();
    }

    public int withdraw(int count) throws Exception {
        if (count > size) throw new Exception("Insufficient funds");
        size -= count;
        return count;
    }

    public void deposit(int count) {
        size += count;
    }

    public int getSize() {
        return size;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public int compareTo(Cell o) {
        return getCurrencyDenomination() - o.getCurrencyDenomination();
    }
}