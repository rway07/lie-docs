package model;


import play.Logger;
import utils.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by enrico on 16/10/16.
 */
public class editorModel {

    int project = -1;
    int file = -1;

    public editorModel(String project, String file){
        this.setProject(project);
        this.setFile(file);
    }

    public void setProject(String prj){
        project = this.getProjectID(prj);
    }

    public void setFile(String file){
       this.file = this.getFileID(file,this.project);
    }


    public HashMap getRow(int row){
        ArrayList rows = this.getRows();
        return (HashMap) rows.get(row);
    }

    public void removeChar(int row, int col){
        long chars = this.countChars(row);
        if(col == chars) col--;

        HashMap chr = getChar(row,col);
        dbUtil.query("DELETE FROM `character` WHERE paragraph = " + chr.get("paragraph") + " and idx = " + chr.get("idx"));
    }

    public void addChar(int row, int col,String chr)
    {
        HashMap r = this.getRow(row);
        int paragraphID = (int)r.get("id");
        double col_idx = -1;
        switch(col){
            case 0: {
                col_idx = 0;
                break;
            }
            default:{
                long chars = this.countChars(row);
                Logger.info("sono dentro:" + chars + " richista pos: " + col);
                if(col == chars)
                {

                    col_idx = ((double)getChar(row,(int)chars-1).get("idx"))+1;
                }

                else
                  col_idx = (((double)getChar(row,col).get("idx") + (double)getChar(row,col-1).get("idx"))/2);
                break;
            }
        }

        dbUtil.query("INSERT INTO `character` (paragraph, `idx`,`value`) VALUES ("+paragraphID+","+col_idx+",'"+chr+"')");
    }

    public HashMap getChar(int row,int col){

        int paragraphID = (int)this.getRow(row).get("id");
        Logger.error("cerco idx per char: " + col + " al paragrafoID : " + paragraphID);
        ArrayList chars = getChars(paragraphID);

        Logger.error("trovati chars: " + chars.size());
        return (HashMap)chars.get(col);
    }

    public long countChars(int row)
    {
        int paragraphID = (int)this.getRow(row).get("id");
        return (long)((ArrayList)getChars(paragraphID)).size();
    }

    public ArrayList getChars(int paragraphID){

        return (ArrayList) dbUtil.query("select * from `character` where paragraph = " + paragraphID + " order by idx asc");
    }
    public ArrayList getRows() {
        return (ArrayList) dbUtil.query("select * from paragraph where file = "+file+" order by idx asc");
    }

    private int getFileID(String file,int prj){
      ArrayList f = (ArrayList)dbUtil.query("select id from files where project = " + prj + " and name = '" + file+"'");
      return (int)((HashMap)f.get(0)).get("id");
    }
    private int getProjectID(String prj){
        ArrayList project = (ArrayList)dbUtil.query("select id from projects where name = '"+prj+"'");
        return (int)(((HashMap)project.get(0)).get("id"));
    }
}
