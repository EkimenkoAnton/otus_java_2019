package atm;

import cell.Cell;
import cell.CellHolder;
import currency.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ATMImpl implements ATM {

    private final CellHolder cellHolder;

    public ATMImpl(CellHolder cellHolder) {
        this.cellHolder = cellHolder;
    }

    @Override
    public  Map<Currency,Integer> withdraw(long sum) throws Exception {

        if(sum> getBalance())
            throw new Exception("Insufficient funds");

        Map<Currency,Integer> combination = calculateCombination(sum);

        for (Map.Entry<Currency, Integer> entry : combination.entrySet()) {
            Currency currency = entry.getKey();
            Integer count = entry.getValue();
            cellHolder.getCells().get(currency).withdraw(count);
        }

        return combination;
    }

    @Override
    public long getBalance(){
        return  cellHolder.getCells().values().stream().mapToLong(value -> value.getCurrency().getDenomination() * value.getSize()).sum();
    }

    @Override
    public void deposit(Currency currency, int count) throws Exception {
        Cell cell = cellHolder.getCells().get(currency);
        if (cell == null)
            throw new Exception("Unsupported currency");
        cell.deposit(count);
    }

    private Map<Currency,Integer> calculateCombination(long sum) throws Exception {
        Map<Currency,Integer> combination = new HashMap<>();
        long tempSum, remainSum = sum, realSum = 0;
        int count;
        long minDenomination = new LinkedList<>(cellHolder.getCells().keySet()).getLast().getDenomination();

        for (Cell value : cellHolder.getCells().values()) {

            if(value.getCurrency().getDenomination() > remainSum || value.getSize() == 0)
                continue;

            count = (int) remainSum / value.getCurrency().getDenomination();

            while (count != 0 && (
                    count > value.getSize() ||
                            (tempSum = remainSum - count * value.getCurrency().getDenomination()) < minDenomination && tempSum != 0
            ) && --count > 0);

            if (count == 0)
                continue;

            remainSum = remainSum-(count*value.getCurrency().getDenomination());
            combination.put(value.getCurrency(),count);
            realSum+=value.getCurrency().getDenomination()*count;

        }

        if (sum != realSum)
            throw new Exception("Requested amount cannot be issued");

        return combination;
    }

}