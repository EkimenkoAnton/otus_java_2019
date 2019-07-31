package cell;

import currency.Currency;
import lombok.Getter;

import java.util.*;

public class CellHolder {

    @Getter private final Map<Currency,Cell> cells = new TreeMap<>((o1, o2) -> o2.getDenomination() - o1.getDenomination());

    CellHolder(List<Cell> cellConfig) {
        cellConfig.forEach(cell -> cells.put(cell.getCurrency(), cell));
    }

}
