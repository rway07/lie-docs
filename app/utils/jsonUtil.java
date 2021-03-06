package utils;


import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import org.json.simple.parser.ParseException;
import play.Logger;
import play.api.libs.json.Json;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by enrico on 07/10/16.
 */
public class jsonUtil {

    private JSONParser p;
    private JSONObject o;

    private HashMap<String,JSONArray> a = null;

    public void remove(String key)
    {
        o.remove(key);
    }
    public void add(String key,JSONObject obj)
    {
       //try to get JSON array for key
       JSONArray arr = a.getOrDefault(key, null);
       if(arr == null)
       {
           a.put(key,new JSONArray());
           arr = a.get(key);
       }

       arr.add(obj);
    }

    public jsonUtil(String j){

      a = new HashMap<String,JSONArray>();
      p=new JSONParser();
      try{
          if(!j.equals(""))
            o=(JSONObject)p.parse(j);
           else o = new JSONObject();

      } catch (ParseException e){
          Logger.error(e.toString());
      }
    }

    public JSONObject getObject(){
        return o;
    }

    public void put(String key,Object value){o.put(key,value);}
    public Object get(String key){
        return (Object)o.get(key);
    }
    public String toString(){

        if(a.size() > 0)
            for(String key :a.keySet())
                this.put(key,a.get(key));


        return o.toJSONString();
    }
}
