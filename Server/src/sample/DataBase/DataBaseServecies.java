package sample.DataBase;

import java.sql.*;

public class DataBaseServecies implements Registrationable {

    String url = "jdbc:mysql://localhost:3306/cursach?serverTimezone=Europe/Minsk&useSSL=false";
    String user = "root";
    String dbPassword = "9360447As";

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    public long registrateUser(String name, String password){
        String query = "select id from users where name='" + name + "'";

        try {
           con = DriverManager.getConnection(url, user, dbPassword);

           System.out.println("Connection to Store DB succesfull!");

           stmt = con.createStatement();

           rs = stmt.executeQuery(query);

            if (rs.next()) {
                System.out.println("exist");
                return -1;
            }else{
                System.out.println("not");
                query = "insert into users values (NULL, '" + name + "', '"  + password +  "')";
                boolean res;
                res = stmt.execute(query);
                if (!res){
                    query = "select id from users where name='" + name + "'";
                    rs = stmt.executeQuery(query);
                    if (rs.next()){
                        int id = rs.getInt(1);
                        System.out.println(id);
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
            try { con.close(); } catch(SQLException se) {se.printStackTrace();}
            try { stmt.close(); } catch(SQLException se) {se.printStackTrace();}
            try { rs.close(); } catch(SQLException se) {se.printStackTrace();}
        }
    return 0;
    }

    public long logInUser(String name, String password){
        String query = "select id from users where name='" + name + "' and password='" + password + "'";

        try {
            con = DriverManager.getConnection(url, user, dbPassword);

            System.out.println("Connection to Store DB succesfull!");
            stmt = con.createStatement();

            rs = stmt.executeQuery(query);

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return count;
            }else{
                System.out.println("not exist");
                return -1;
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            try { con.close(); } catch(SQLException se) {se.printStackTrace();}
            try { stmt.close(); } catch(SQLException se) {se.printStackTrace();}
            try { rs.close(); } catch(SQLException se) {se.printStackTrace();}
        }
        return 0;
    }
}
