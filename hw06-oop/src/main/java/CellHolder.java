import java.util.*;

public class CellHolder {

    private final Map<Integer,Cell> cells = new TreeMap<>(Collections.reverseOrder());

    private CellHolder(List<Cell> cellConfig) {
        cellConfig.forEach(cell -> cells.put(cell.getCurrencyDenomination(), cell));
    }

    public static class Builder {

        private final List<Cell> cellConfig = new ArrayList<>();

        public Builder add(Currency currency) {
            cellConfig.add(new Cell(currency));
            return this;
        }

        public CellHolder build() {
            return new CellHolder(cellConfig);
        }
    }

    public Map<Integer, Cell> getCells() {
        return cells;
    }
}
