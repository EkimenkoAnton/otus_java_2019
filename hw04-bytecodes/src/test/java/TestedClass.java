import annotations.Log;

import java.util.Collection;
import java.util.Map;

public class TestedClass implements ITestedClass{

    @Override
    @Log
    public void loggerMethod(Map param1, Collection param2) {

    }

    @Override
    @Log
    public void logPrimitiveArray(double[] arr) {

    }

    @Override
    @Log
    public void logMap(Map map) {

    }


    @Override
    public void nonLoggedMethod(String param1) {

    }
}
