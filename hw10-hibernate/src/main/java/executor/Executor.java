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
            try {
                transaction.begin();
                action.accept(session);
                transaction.commit();
            } catch (Exception ex) {
                System.out.println(ex);
                transaction.rollback();
            }
        }
    }

    public <I> I execQuery(Function<Session, I> action) {
        I retVal = null;
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.getTransaction();
            try {
                transaction.begin();
                retVal = action.apply(session);
                transaction.commit();
            } catch (Exception ex) {
                System.out.println(ex);
                transaction.rollback();
            }
        }
        return retVal;
    }
}
