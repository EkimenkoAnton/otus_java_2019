import java.util.Map;

public interface IATM {
    Map<Currency,Integer> withdraw(long sum) throws Exception;
    long getBalance();
    void deposit(Currency currency, int count) throws Exception;
}
