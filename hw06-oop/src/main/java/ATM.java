import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ATM implements IATM{

    private final CellHolder cellHolder;

    public ATM(CellHolder cellHolder) {
        this.cellHolder = cellHolder;
    }

    public  Map<Currency,Integer> withdraw(long sum) throws Exception {

        if(sum> getBalance())
            throw new Exception("Insufficient funds");

        Map<Currency,Integer> combination = calculateCombination(sum);

        for (Map.Entry<Currency, Integer> entry : combination.entrySet()) {
            Currency currency = entry.getKey();
            Integer count = entry.getValue();
            cellHolder.getCells().get(currency.getDenomenation()).withdraw(count);
        }

        return combination;
    }

    public long getBalance(){
        return  cellHolder.getCells().values().stream().mapToLong(value -> value.getCurrencyDenomination() * value.getSize()).sum();
    }

    public void deposit(Currency currency, int count) throws Exception {
        Cell cell = cellHolder.getCells().get(currency.getDenomenation());
        if (cell == null)
            throw new Exception("Unsupported currency");
        cell.deposit(count);
    }

    private Map<Currency,Integer> calculateCombination(long sum) throws Exception {
        Map<Currency,Integer> combination = new HashMap<>();
        long tempSum, remainSum = sum, realSum = 0;
        int count;
        long minDenomination = new LinkedList<>(cellHolder.getCells().keySet()).getLast();

        for (Cell value : cellHolder.getCells().values()) {

            if(value.getCurrencyDenomination() > remainSum || value.getSize() == 0)
                continue;

            count = (int) remainSum / value.getCurrencyDenomination();

            while (count != 0 && (
                    count > value.getSize() ||
                            (tempSum = remainSum - count * value.getCurrencyDenomination()) < minDenomination && tempSum != 0
            ) && --count > 0);

            if (count == 0)
                continue;

            remainSum = remainSum-(count*value.getCurrencyDenomination());
            combination.put(value.getCurrency(),count);
            realSum+=value.getCurrencyDenomination()*count;

        }

        if (sum != realSum)
            throw new Exception("Requested amount cannot be issued");

        return combination;
    }

}