package dbservice;

import executor.Executor;


public class HibernateDBService implements DBService {

    private final Executor executor;

    public HibernateDBService(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void create(Object objectData) {
        executor.execUpdate(session -> session.save(objectData));
    }

    @Override
    public void update(Object objectData) {
        executor.execUpdate(session -> session.update(objectData));
    }

    @Override
    public void createOrUpdate(Object objectData) {
        executor.execUpdate(session -> session.saveOrUpdate(objectData));
    }

    @Override
    public <I> I load(long id, Class<I> clazz) {
        return executor.execQuery(session -> session.get(clazz, id));
    }

}
