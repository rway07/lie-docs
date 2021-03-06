package utils;

import akka.actor.ActorRef;
import play.Logger;
import scala.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by enrico on 19/10/16.
 */
public class cProject implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<String,String> sources = null;
    private HashMap<String,String> headers = null;
    private HashMap<String,ArrayList<String>> sourceHeaders = null;
    private ActorRef sender;

    public String getProjectName() {
        return projectName;
    }

    public cProject setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    private String projectName = "";


    public ActorRef getSender() {
        return sender;
    }

    public cProject setSender(ActorRef sender) {
        this.sender = sender;
        return this;
    }

    public cProject(){
        sources = new HashMap<String, String>();
        headers = new HashMap<String, String>();
        sourceHeaders = new HashMap<String,ArrayList<String>>();
    }


    public String getSource(String name){ return sources.get(name);}
    public String getHeader(String name){ return headers.get(name);}



    public void addSource(String name, String content)
    {
        ArrayList<String> headersForSource = new ArrayList<String>();

        sources.put(name,content);
        Pattern p_include = Pattern.compile(".*#include\\s+\\\"([\\d\\w]+\\.h)\\\"",Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher include = p_include.matcher(content);
        while(include.find())
        {
            headers.put(include.group(1),"");
            headersForSource.add(include.group(1));
        }

        sourceHeaders.put(name,headersForSource);
    }

    public boolean checkHeader(String headerName){
        return !(headers.get(headerName) == null || headers.get(headerName).equals(""));
    }
    public Iterator<String> getSources(){ return sources.keySet().iterator();}
    public Iterator<String> getHeaders(){ return headers.keySet().iterator(); }

    public void addHeaderContent(String key, String content)
    {
        headers.put(key,content);
    }

    public String toString()
    {
        String ret = "\n\n";
        ret += "************* -- " + (sources.keySet().toArray())[0] + "-- **********************\n";
        ret += sources.get((sources.keySet().toArray())[0]) + "\n";
        ret += "************* HEADERS ***************\n";
        Iterator h = headers.keySet().iterator();
        while(h.hasNext())
        {
            String hFile = (String)h.next();
            ret += "------ " + hFile + "------\n";
            ret += headers.get(hFile) + "\n";
        }
        ret+= "**************  END ************";

        return ret;
    }

    public cProject getSourceHeaders(String file)
    {
        cProject cObject = new cProject();
        cObject.addSource(file,sources.get(file));

        ArrayList<String> fileHeaders = sourceHeaders.get(file);
        Iterator iFileHeaders = fileHeaders.iterator();

        while(iFileHeaders.hasNext())
        {
            String h = (String)iFileHeaders.next();
            cObject.addHeaderContent(h,headers.get(h));
        }

        return cObject;

    }

}
