package dbservice;

public class CacheDBServiceManager implements DBServiceManager {

    private final   DBService cacheDBService;

    public CacheDBServiceManager() {
        this.cacheDBService =  new CacheDBService(new HibernateDBServiceManager().getDBService());
    }

    @Override
    public DBService getDBService() {
        return this.cacheDBService;
    }
}
