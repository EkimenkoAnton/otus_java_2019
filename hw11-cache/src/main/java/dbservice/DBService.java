package dbservice;

public interface DBService {
    void create(Object objectData);
    void update(Object objectData);
    void createOrUpdate(Object objectData);
    <I> I load(long id, Class<I> clazz);
}
