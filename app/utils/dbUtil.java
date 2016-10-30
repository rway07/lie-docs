package utils;

import akka.actor.ActorSystem;
import play.Logger;
import play.db.*;
import scala.concurrent.Future;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.*;
import java.util.concurrent.Callable;
import java.util.*;
import java.util.regex.Pattern;

import static akka.dispatch.Futures.future;

/**
 * Created by enrico on 07/10/16.
 */
public class dbUtil {

    private static DB db;


    public static DB getDB(){ return db;}

    public static Object query(String sql) {
        Pattern p = Pattern.compile("^\\s*SELECT.*",Pattern.CASE_INSENSITIVE);
        if(p.matcher(sql).matches()) {
            return (Object)dbUtil.executeQuery(sql);
        } else {
            //insert , update, replace delete
            return (Object)dbUtil.executeUpdate(sql);
        }
    }

    public static int executeUpdate(String query) {
        Connection c = db.getConnection();
        int r = 0;
        while(true){
            try {
                Statement s = c.createStatement();
                r = s.executeUpdate(query);

                if(r == 0)
                    throw new SQLException("affected row = 0");

                c.close();
                return r;
            } catch (SQLException e) {
                try{
                    Thread.sleep(3);
                }catch(InterruptedException f){
                    // Logger.error("interrupted exception execute update 0:" + e.getMessage());
                }
            }
        }
    }

    public static ArrayList executeQuery(String query) {
        Connection c = db.getConnection();
        ResultSet rs;
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        while(true){
            try {
                Statement s = c.createStatement();
                rs = s.executeQuery(query);

                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();
                while (rs.next()){
                    HashMap<String, Object> row = new HashMap<String, Object>(columns);
                    for(int i=1; i<=columns; ++i){
                        row.put(md.getColumnLabel(i), rs.getObject(i));
                    }
                    list.add(row);
                }
                c.close();
                return list;

            } catch (Exception e) {
                try{
                    Thread.sleep(3);
                }catch(InterruptedException f){
                    // Logger.error("interrupted exception execute update 0:" + e.getMessage());
                }
                Logger.error("ERRORE: " + e.getMessage() + " - " + e.getCause());
            }

        }
    }

}
