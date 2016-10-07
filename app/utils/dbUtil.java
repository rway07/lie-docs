package utils;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import play.Logger;
import play.db.*;
import scala.concurrent.Future;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Callable;

import static akka.dispatch.Futures.future;

/**
 * Created by enrico on 07/10/16.
 */
public class dbUtil {

    private DB db;
    ActorSystem system;

    public dbUtil(ActorSystem system){
        this.system = system;
    }

    public Future<ResultSet> q(String sql){

        Future<ResultSet> f = future(new Callable<ResultSet>() {
            public ResultSet call() {

                Connection conn =  db.getConnection();
                try{
                    Statement stm = conn.createStatement();
                    ResultSet r = stm.executeQuery(sql);
                    return r;
                }catch (Exception e){
                    Logger.error(e.getMessage() + ":" + e.getCause());
                }

                return null;

            }
        }, system.dispatcher());

        return f;
    }

}
