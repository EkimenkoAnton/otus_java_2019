package template;

import datasets.Account;
import datasets.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateTest {

    private Connection connection;
    private Template<User> userTemplate;
    private Template<Account> accountTemplate;

    private User user = new User(1, "username", 11);
    private User alternateUser = new User(1, "alternateusername", 33);
    private User anotherUser = new User(2, "anotherusername", 66);

    private Account account = new Account(1, "accountname", 10);
    private Account alternateAccount = new Account(1, "alternateAccountname", 20);
    private Account anotherAccount = new Account(2, "anotherAccountname", 40);

    @BeforeEach
    @SneakyThrows
    void setUp() {
        connection = new DataSourceH2().getConnection();
        userTemplate = JDBCTemplateFactory.getInstance().getTemplateInstance(connection, User.class);
        accountTemplate = JDBCTemplateFactory.getInstance().getTemplateInstance(connection, Account.class);
    }

    @Test
    @SneakyThrows
    void create(){
        userTemplate.create(user);
        accountTemplate.create(account);

        User realUser = null;
        Account realAccount = null;

        String userQuery = "SELECT id,name,age FROM USER WHERE id = ?";
        String accountQuery = "SELECT no,type,rest FROM ACCOUNT WHERE no= ?";
        try (
                PreparedStatement userStatement = connection.prepareStatement(userQuery);
                PreparedStatement accountStatement = connection.prepareStatement(accountQuery);
        ){
            userStatement.setLong(1,user.getId());
            accountStatement.setLong(1,account.getNo());
            try (
                    ResultSet userResultSet = userStatement.executeQuery();
                    ResultSet accountResultSet = accountStatement.executeQuery();
            ) {
                while (userResultSet.next()) {
                    long id  = userResultSet.getLong("id");
                    String name = userResultSet.getString("name");
                    int age = userResultSet.getInt("age");
                    realUser = new User(id, name, age);
                }
                while (accountResultSet.next()) {
                    long no  = accountResultSet.getLong("no");
                    String type = accountResultSet.getString("type");
                    long rest = accountResultSet.getLong("rest");
                    realAccount = new Account(no,type,rest);
                }
            }
        }

        Assertions.assertNotNull(realUser);
        Assertions.assertNotNull(realAccount);

        Assertions.assertEquals(user,realUser);
        Assertions.assertEquals(account,realAccount);
    }

    @Test
    @SneakyThrows
    void update(){

        String userCreateQuery = "INSERT INTO USER (id,name,age) VALUES (?,?,?)";
        String accountCreateQuery = "INSERT INTO ACCOUNT (no,type,rest) VALUES (?,?,?)";
        try (
                PreparedStatement userStatement = connection.prepareStatement(userCreateQuery);
                PreparedStatement accountStatement = connection.prepareStatement(accountCreateQuery);
        ){
            userStatement.setLong(1,user.getId());
            userStatement.setString(2,user.getName());
            userStatement.setInt(3,user.getAge());

            accountStatement.setLong(1,account.getNo());
            accountStatement.setString(2,account.getType());
            accountStatement.setLong(3,account.getRest());

            userStatement.executeUpdate();
            accountStatement.executeUpdate();

        }


        userTemplate.update(alternateUser);
        accountTemplate.update(alternateAccount);

        User realUser = null;
        Account realAccount = null;

        String userQuery = "SELECT id,name,age FROM USER WHERE id = ?";
        String accountQuery = "SELECT no,type,rest FROM ACCOUNT WHERE no= ?";
        try (
                PreparedStatement userStatement = connection.prepareStatement(userQuery);
                PreparedStatement accountStatement = connection.prepareStatement(accountQuery);
        ){
            userStatement.setLong(1,user.getId());
            accountStatement.setLong(1,account.getNo());
            try (
                    ResultSet userResultSet = userStatement.executeQuery();
                    ResultSet accountResultSet = accountStatement.executeQuery();
            ) {
                while (userResultSet.next()) {
                    long id  = userResultSet.getLong("id");
                    String name = userResultSet.getString("name");
                    int age = userResultSet.getInt("age");
                    realUser = new User(id, name, age);
                }
                while (accountResultSet.next()) {
                    long no  = accountResultSet.getLong("no");
                    String type = accountResultSet.getString("type");
                    long rest = accountResultSet.getLong("rest");
                    realAccount = new Account(no,type,rest);
                }
            }
        }

        Assertions.assertNotNull(realUser);
        Assertions.assertNotNull(realAccount);

        Assertions.assertEquals(alternateUser,realUser);
        Assertions.assertEquals(alternateAccount,realAccount);

    }

    @Test
    @SneakyThrows
    void createOrUpdate(){
        userTemplate.createOrUpdate(user);
        userTemplate.createOrUpdate(alternateUser);
        userTemplate.createOrUpdate(anotherUser);

        accountTemplate.createOrUpdate(account);
        accountTemplate.createOrUpdate(alternateAccount);
        accountTemplate.createOrUpdate(anotherAccount);

        Map<Long,User> users = new HashMap<>();
        Map<Long,Account> accounts = new HashMap<>();
        String userQuery = "SELECT id,name,age FROM USER WHERE id = ? or id = ?";
        String accountQuery = "SELECT no,type,rest FROM ACCOUNT WHERE no=? or no=?";
        try (
                PreparedStatement userStatement = connection.prepareStatement(userQuery);
                PreparedStatement accountStatement = connection.prepareStatement(accountQuery);
                ){
            userStatement.setLong(1,user.getId());
            userStatement.setLong(2,anotherUser.getId());
            accountStatement.setLong(1,account.getNo());
            accountStatement.setLong(2,anotherAccount.getNo());
            try (
                    ResultSet userResultSet = userStatement.executeQuery();
                    ResultSet accountResultSet = accountStatement.executeQuery();
                    ) {
                while (userResultSet.next()) {
                    long id  = userResultSet.getLong("id");
                    String name = userResultSet.getString("name");
                    int age = userResultSet.getInt("age");
                    users.put(id, new User(id,name,age));
                }
                while (accountResultSet.next()) {
                    long no  = accountResultSet.getLong("no");
                    String type = accountResultSet.getString("type");
                    long rest = accountResultSet.getLong("rest");
                    accounts.put(no, new Account(no,type,rest));
                }
            }
        }

        assertEquals(2, users.size());
        assertEquals(2, accounts.size());

        assertEquals(alternateUser,users.get(alternateUser.getId()));
        assertEquals(anotherUser,users.get(anotherUser.getId()));

        assertEquals(alternateAccount,accounts.get(alternateAccount.getNo()));
        assertEquals(anotherAccount,accounts.get(anotherAccount.getNo()));
    }

    @Test
    void load(){

        String userCreateQuery = "INSERT INTO USER (id,name,age) VALUES (?,?,?)";
        String accountCreateQuery = "INSERT INTO ACCOUNT (no,type,rest) VALUES (?,?,?)";
        try (
                PreparedStatement userStatement = connection.prepareStatement(userCreateQuery);
                PreparedStatement accountStatement = connection.prepareStatement(accountCreateQuery);
        ){
            userStatement.setLong(1,user.getId());
            userStatement.setString(2,user.getName());
            userStatement.setInt(3,user.getAge());

            accountStatement.setLong(1,account.getNo());
            accountStatement.setString(2,account.getType());
            accountStatement.setLong(3,account.getRest());

            userStatement.executeUpdate();
            accountStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        User loadUser = userTemplate.load(user.getId(), User.class);
        Account loadAccount = accountTemplate.load(account.getNo(), Account.class);

        Assertions.assertNotNull(loadUser);
        Assertions.assertNotNull(loadAccount);
        Assertions.assertEquals(user,loadUser);
        Assertions.assertEquals(account,loadAccount);
    }


    private void clear(){
        String query = "delete from USER;delete from ACCOUNT";
        try (PreparedStatement statement = connection.prepareStatement(query);){
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    @SneakyThrows
    void tearDown() {
        clear();
        if(null != connection)
            connection.close();
    }
}