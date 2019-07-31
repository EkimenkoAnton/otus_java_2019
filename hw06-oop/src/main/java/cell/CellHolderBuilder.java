package cell;

import currency.Currency;
import java.util.ArrayList;
import java.util.List;

public class CellHolderBuilder {
    private final List<Cell> cellConfig = new ArrayList<>();

    public CellHolderBuilder add(Currency currency) {
        cellConfig.add(new Cell(currency));
        return this;
    }

    public CellHolder build() {
        return new CellHolder(cellConfig);
    }
}
