package utils;


import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import org.json.simple.parser.ParseException;
import play.Logger;
/**
 * Created by enrico on 07/10/16.
 */
public class jsonUtil {

    private JSONParser p;
    private JSONObject o;

    public jsonUtil(String j){
      p=new JSONParser();
      try{
          o=(JSONObject)p.parse(j);
      } catch (ParseException e){
          Logger.error(e.toString());
      }
    }

    public Object get(String key){
        return (Object)o.get(key);
    }



}
