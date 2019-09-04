package executor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

public class Executor {
    private final SessionFactory sessionFactory;

    public Executor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void execUpdate(Consumer<Session> action) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();
            action.accept(session);
            transaction.commit();
        }
    }

    public <I> I execQuery(Function<Session, I> action) {
        try (Session session = sessionFactory.openSession()){
            return action.apply(session);
        }
    }
}
