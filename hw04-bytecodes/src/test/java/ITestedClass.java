import annotations.Log;

import java.util.Collection;
import java.util.Map;

public interface ITestedClass {
    @Log
    void loggerMethod(Map param1, Collection param2);

    void logPrimitiveArray(double[] arr);

    void logMap(Map map);

    void nonLoggedMethod(String param1);
}
