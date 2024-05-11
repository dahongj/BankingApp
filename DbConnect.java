import java.sql.*;

public class DbConnect{
    private static DbConnect instance;
    private Connection connection;
    private String dbName = "bankapp";
    private String db_url = "jdbc:mysql://localhost:3306";
    private String url = "jdbc:mysql://localhost:3306/bankapp";
    private String user = "root";
    private String password = "9768";

    public static DbConnect getInstance(){
        if(instance == null){
            instance = new DbConnect();
        }
        return instance;
    }

    public void connectToDatabase(){

        try{
            connection = DriverManager.getConnection(url,user,password);
            System.out.println("Connection Established Successfully");
            
        } catch (SQLException ex) {
            System.out.println("Exception: "+ex.toString());
            createDatabase();
            createTable();
        }
    }

    private void createDatabase() {
        try {
            connection = DriverManager.getConnection(db_url,user,password);
            Statement s = connection.createStatement();
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            System.out.println("Database Created Successfully");
        } 
        catch (SQLException ex) {
            System.out.println("Exception: "+ex.toString());
        }
    }

    public void createTable(){
        String query = "CREATE TABLE IF NOT EXISTS USERS " +
        "(username VARCHAR(50) not NULL, " +
        " password VARCHAR(50) not NULL, " + 
        " balance DOUBLE not NULL DEFAULT '0.00', " + 
        " PRIMARY KEY ( username ))"; 

        try{
            connection = DriverManager.getConnection(url,user,password);
            Statement s = connection.createStatement();
            s.executeUpdate(query);
            System.out.println("Table Created Successfully");
        } catch (SQLException ex) {
            System.out.println("Exception: "+ex.toString());
        }
    }

    public boolean makeAccount(String username, String password){
        try {
            String query = String.format("insert into USERS(username,password,balance) values ('%s','%s','0')", username, password);
            Statement s = connection.createStatement();
            s.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkAccount(String username, String password) {
        try {
            String query = String.format("select * from USERS where USERS.username='%s' and USERS.password='%s'", username, password);
            Statement s = connection.createStatement();
            ResultSet rs= s.executeQuery(query);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deposit(String username, String password,double amount){
        try {
            String query = String.format("update USERS set USERS.balance=USERS.balance + '%f' where(username = '%s')",amount,username);
            Statement s = connection.createStatement();
            s.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean withdraw(String username, String password,double amount){
        try {
            String query = String.format("select * from USERS where USERS.username='%s' and USERS.password='%s'", username, password);
            Statement s = connection.createStatement();
            ResultSet rs= s.executeQuery(query);

            double value = 0;
            while(rs.next()){
                value = rs.getDouble("balance");
            }

            if((value - amount) >= 0){
                query = String.format("update USERS set USERS.balance=USERS.balance - '%f' where(username = '%s')",amount,username);
                s = connection.createStatement();
                s.executeUpdate(query);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getBalance(String username, String password){
        try {
            String query = String.format("select * from USERS where USERS.username='%s' and USERS.password='%s'", username, password);
            Statement s = connection.createStatement();
            ResultSet rs= s.executeQuery(query);

            double value = 0;
            while(rs.next()){
                value = rs.getDouble("balance");
            }
            return value;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Connection getConnection() {
        return connection;
    }


    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
