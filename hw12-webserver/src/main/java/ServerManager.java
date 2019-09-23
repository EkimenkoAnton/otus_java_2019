import dbservice.DBService;
import org.eclipse.jetty.security.*;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import servlets.AddUserServlet;
import servlets.GetUsersServlet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;


public class ServerManager {

    private static final String REALM_CONFIG_NAME = "realm.properties";
    private static final int DEFAULT_PORT = 80;
    private final int port;
    private final DBService dbService;
    private Server server;

    public ServerManager(DBService dbService, int port) {
        this.dbService = dbService;
        this.port = port;
    }

    public ServerManager(DBService dbService) {
        this.dbService = dbService;
        this.port = DEFAULT_PORT;
    }

    public Server getServer() throws MalformedURLException {
        if (null == this.server) {
            Server server = new Server(port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.addServlet(new ServletHolder(new GetUsersServlet(dbService)), "/getUsers");
            context.addServlet(new ServletHolder(new AddUserServlet(dbService)), "/addUser");

            server.setHandler(new HandlerList(context));

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{createResourceHandler(), createSecurityHandler(context)});

            server.setHandler(handlers);
            this.server = server;
        }
        return this.server;
    }

    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});

        URL fileDir = WebServer.class.getClassLoader().getResource("static");
        if (fileDir == null) {
            throw new RuntimeException("File Directory not found");
        }
        
        resourceHandler.setResourceBase(URLDecoder.decode(fileDir.getPath(), StandardCharsets.UTF_8));
        return resourceHandler;
    }

    private SecurityHandler createSecurityHandler(ServletContextHandler context) throws MalformedURLException {
        Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[]{"user", "admin"});

        ConstraintMapping constraintMapping = createConstraintMapping(constraint);
        LoginService loginService = getLoginService();
        ConstraintSecurityHandler securityHandler = createConstrainSecurityHandler();

        securityHandler.setLoginService(loginService);
        securityHandler.setHandler(new HandlerList(context));
        securityHandler.setConstraintMappings(Collections.singletonList(constraintMapping));

        return securityHandler;
    }

    private LoginService getLoginService(){
        String pathToConfig = getConfigPath(REALM_CONFIG_NAME);
        return new HashLoginService("MyRealm", pathToConfig);
    }

    private String getConfigPath(String configName) {
        URL propFile = null;

        File realmFile = new File(configName);
        if (realmFile.exists()) {
            try {
                propFile = realmFile.toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        if (propFile == null) {
            System.out.println("local realm config not found, looking into Resources");
            propFile = WebServer.class.getClassLoader().getResource("realm.properties");
        }

        if (propFile == null) {
            throw new RuntimeException("Realm property file not found");
        }

        return URLDecoder.decode(propFile.getPath(), StandardCharsets.UTF_8);
    }

    private ConstraintMapping createConstraintMapping(Constraint constraint){
        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec("/*");
        mapping.setConstraint(constraint);
        return mapping;
    }

    private ConstraintSecurityHandler createConstrainSecurityHandler() {
        ConstraintSecurityHandler security = new ConstraintSecurityHandler();
        security.setAuthenticator(new BasicAuthenticator());
        return security;
    }
}
