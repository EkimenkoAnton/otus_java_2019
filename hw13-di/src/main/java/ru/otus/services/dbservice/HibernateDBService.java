package ru.otus.services.dbservice;

import ru.otus.services.executor.Executor;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

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

    public <I> List<I> loadEntities(Class<I> clazz) {
        return executor.execMultiQuery(session -> {
            CriteriaQuery<I> criteriaQuery = session.getCriteriaBuilder().createQuery(clazz);
            Root<I> rootEntry = criteriaQuery.from(clazz);
            CriteriaQuery<I> all = criteriaQuery.select(rootEntry);
            Query<I> allQuery = session.createQuery(all);
            return allQuery.getResultList();
        });
    }

}