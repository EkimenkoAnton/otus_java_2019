import dbservice.DBService;
import dbservice.DBServiceManager;
import dbservice.HibernateDBServiceManager;
import org.eclipse.jetty.server.Server;

public class WebServer {
    private final static int PORT = 8080;

    public static void main(String[] args) throws Exception {
        new WebServer().start();
    }

    private void start() throws Exception {
        DBServiceManager hibernateDBServiceManager = new HibernateDBServiceManager();
        DBService dbService = hibernateDBServiceManager.getDBService();

        ServerManager serverManager = new ServerManager(dbService, PORT);
        Server server = serverManager.getServer();
        server.start();
        server.join();
    }
}
