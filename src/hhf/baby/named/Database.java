package hhf.baby.named;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */
public class Database {

    static Connection conn;

    private static Database inst;

    public static Database getInst() {
        if(inst == null){
            inst = new Database();
            try {
//                Class.forName("com.hxtt.sql.access.AccessDriver");
//                conn = DriverManager.getConnection("jdbc:access:///named.accdb");
                String url = "jdbc:mysql://localhost:3306/named?user=root&password=123456&useUnicode=true&characterEncoding=UTF8";

                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(url, "root", "123456");
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return inst;
    }

    public void addName(String name) throws SQLException {

        PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO NAMED(fullname) VALUES (?) ");
        ps.setString(1, name);
        ps.executeUpdate();
        ps.close();
    }

    public List<Named> all() throws SQLException {


        List<Named> all = new ArrayList<>();
//        PreparedStatement ps = conn.prepareStatement("SELECT count(1) FROM NAMED ");
//        ResultSet rs  = ps.executeQuery();
//        rs.next();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM NAMED ");
        ResultSet rs  = ps.executeQuery();
        Named named = null;
        while(rs.next()){
            named = new Named();
            named.fullName = rs.getString("fullname");
            named.first = rs.getString("first");
            named.second = rs.getString("second");
            named.third = rs.getString("third");
            named.wx1 = rs.getString("wx1");
            named.wx2 = rs.getString("wx2");
            named.wx3 = rs.getString("wx3");
            named.score = rs.getFloat("score");
            all.add(named);
        }
        return all;
    }


    public Named get(String name) throws SQLException {
        name = name.trim();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM NAMED WHERE fullname=?");
        ps.setString(1, name);
        ResultSet rs  = ps.executeQuery();
        Named named = null;
        while(rs.next()){
            named = new Named();

            named.fullName = rs.getString("fullname");
            named.first = rs.getString("first");
            named.second = rs.getString("second");
            named.third = rs.getString("third");
            named.wx1 = rs.getString("wx1");
            named.wx2 = rs.getString("wx2");
            named.wx3 = rs.getString("wx3");
            named.score = rs.getFloat("score");
        }
        return named;
    }

    public Named save(Named named) throws SQLException {

        PreparedStatement preparedStmt = conn.prepareStatement("UPDATE NAMED SET first=?, second=?, third=?, wx1=?, wx2=?, wx3=?, score=?  WHERE fullname=?");
        preparedStmt.setString(1, named.first);
        preparedStmt.setString(2, named.second);
        preparedStmt.setString(3, named.third);
        preparedStmt.setString(4, named.wx1);
        preparedStmt.setString(5, named.wx2);
        preparedStmt.setString(6, named.wx3);
        preparedStmt.setFloat(7, named.score);
        preparedStmt.setString(8, named.fullName);
        preparedStmt.executeUpdate();
        preparedStmt.close();
        return named;
    }

}
