import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Print {
    private static final ReentrantLock locker = new ReentrantLock();
    private static final Condition condition = locker.newCondition();

    public static void print(String threadName, String output){
        locker.lock();
        try {

            System.out.println(threadName+output);
            condition.signalAll();
            try {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            locker.unlock();
        }
    }

    public static void close(){
        locker.lock();
        try {
            condition.signalAll();
        } finally {
            locker.unlock();
        }
    }
}
