import java.util.*;

public class TestedClass {
    public int[] array = {1,2,3,4,5,6};
    Integer nullableInt;
    int primitiveInt;
    private Map<Integer,Object> map = new HashMap<>();

    private List<Integer> list = new ArrayList();

    public TestedClass() {
        list.add(1);
        list.add(2);
        list.add(3);

        map.put(1,"value1");
        map.put(2,"value2");
        map.put(3,null);
        map.put(4,list);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TestedClass that = (TestedClass) object;
        return primitiveInt == that.primitiveInt &&
                Arrays.equals(array, that.array) &&
                Objects.equals(nullableInt, that.nullableInt) &&
                Objects.equals(map, that.map) &&
                Objects.equals(list, that.list);
    }

    @Override
    public String toString() {
        return "{" + "\n" +
                "array=" + Arrays.toString(array) + "\n" +
                ", nullableInt=" + nullableInt + "\n" +
                ", primitiveInt=" + primitiveInt + "\n" +
                ", map=" + map + "\n" +
                ", list=" + list + "\n" +
                '}';
    }
}
