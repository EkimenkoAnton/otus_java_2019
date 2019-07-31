package currency;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
public enum  Currency {

    NOMINAL50(50),
    NOMINAL100(100),
    NOMINAL200(200),
    NOMINAL500(500),
    NOMINAL1000(1000),
    NOMINAL2000(2000),
    NOMINAL5000(5000);

    @Getter private int denomination;
}