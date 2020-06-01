package sample.DataBase;

import java.net.HttpURLConnection;
import java.sql.*;

public class DataBaseServecies implements DataBaseInterface {

    String url = "jdbc:mysql://localhost:3306/cursach?serverTimezone=Europe/Minsk&useSSL=false";
    String user = "root";
    String dbPassword = "9360447As";

    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    public long registrateUser(String name, String password){
        String query = "select id from users where name='" + name + "'";
        try {
            connection = DriverManager.getConnection(url, user, dbPassword);
            System.out.println("Connection to Store DB successful!");
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                System.out.println("exist");
                return -1;
            }else{
                System.out.println("not");
                query = "insert into users values (NULL, '" + name + "', '"  + password +  "')";
                boolean res;
                res = statement.execute(query);
                if (!res){
                    query = "select id from users where name='" + name + "'";
                    resultSet = statement.executeQuery(query);
                    int id;
                    if (resultSet.next()){
                        id = resultSet.getInt(1);
                        return id;
                    }
                }else{
                    System.out.println("Error");
                    return 0;
                }
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            try { connection.close(); } catch(SQLException se) {se.printStackTrace();}
            try { statement.close(); } catch(SQLException se) {se.printStackTrace();}
            try { resultSet.close(); } catch(SQLException se) {se.printStackTrace();}
        }
    return 0;
    }

    public long logInUser(String name, String password){
        String query = "select id from users where name='" + name + "' and password='" + password + "'";
        int id = -1;
        try {
            connection = DriverManager.getConnection(url, user, dbPassword);
            System.out.println("Connection to Store DB successful!");
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }else{
                System.out.println("not exist");
                return id;
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            try { connection.close(); } catch(SQLException se) {se.printStackTrace();}
            try { statement.close(); } catch(SQLException se) {se.printStackTrace();}
            try { resultSet.close(); } catch(SQLException se) {se.printStackTrace();}
        }
        return id;
    }

    public int regFolder(String id, String folderName, String mac, String path){
        String query = "insert into folders values ('" + id + "','" + folderName + "','"  + mac +  "','" + path + "')";
        try {
            connection = DriverManager.getConnection(url, user, dbPassword);
            System.out.println("Connection to Store DB successful!");
            statement = connection.createStatement();
            boolean res;
            res = statement.execute(query);
            if (!res){
                System.out.println("Ok");
                return HttpURLConnection.HTTP_OK;
            }else{
                System.out.println("not");
                return HttpURLConnection.HTTP_BAD_REQUEST;
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            try { connection.close(); } catch(SQLException se) {se.printStackTrace();}
            try { statement.close(); } catch(SQLException se) {se.printStackTrace();}
        }
        return 0;
    }

    public String getFolder(String id, String mac){
        String query = "select pathToFilder from folders where id='" + id + "' and macAddress='" + mac + "'";
        try {
            connection = DriverManager.getConnection(url, user, dbPassword);
            System.out.println("Connection to Store DB successful!");
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                System.out.println(resultSet.getString(1));
                return resultSet.getString(1);
            }else{
                System.out.println("not");
                return null;
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            try { resultSet.close(); } catch(SQLException se) {se.printStackTrace();}
            try { connection.close(); } catch(SQLException se) {se.printStackTrace();}
            try { statement.close(); } catch(SQLException se) {se.printStackTrace();}
        }
        return null;
    }
}
