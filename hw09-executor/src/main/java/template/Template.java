package template;

public interface Template<T> {
    void create(T objectData);
    void update(T objectData);
    void createOrUpdate(T objectData);
    <I> I load(long id, Class<I> clazz);
}
