package dbservice;

import java.util.List;

public interface DBService {
    void create(Object objectData);
    void update(Object objectData);
    void createOrUpdate(Object objectData);
    <I> I load(long id, Class<I> clazz);
    <I> List<I> loadEntities(Class<I> clazz);
}