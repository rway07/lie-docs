package model;


import play.Logger;
import utils.*;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by enrico on 16/10/16.
 */
public class editorModel {

    int project = -1;

    public editorModel(String project){
        this.setProject(project);
    }

    public Iterator<HashMap<String, Object>> getSources(){
        ArrayList<HashMap<String, Object>> sources = (ArrayList<HashMap<String,Object>>) dbUtil.query("select name, id from files where project = " + project + " and name like '%.c' ");
        return sources.iterator();
    }

    public String getSource(String sourceFile)
    {
        String file = "";
        ArrayList rows = getRows(sourceFile);

        Iterator irows = rows.iterator();

        while(irows.hasNext())
        {
            HashMap row = (HashMap)irows.next();
            ArrayList chars =  getChars((int)row.get("id"));
            Iterator ichars = chars.iterator();

            while(ichars.hasNext())
            {
                HashMap chr = (HashMap) ichars.next();
                file+= chr.get("value");
            }

            file+="\n";
        }

        return file;

    }

    public void setProject(String prj){
        project = this.getProjectID(prj);
    }

    public HashMap getRow(String file,int row){
        ArrayList rows = this.getRows(file);
        return (HashMap) rows.get(row);
    }

    public void removeChar(String file, int row, int col){
        long chars = this.countChars(file, row);
        //if(col == chars) col--;
        if (col != 0) {
            col--;
        }
        HashMap chr = getChar(file,row,col);
        dbUtil.query("DELETE FROM `character` WHERE paragraph = " + chr.get("paragraph") + " and idx = " + chr.get("idx"));
    }

    public void addRowMoveText(String file, int row,int col){
        addRowNoMoveText(file,row);

        int currRowID = (int)getRow(file, row).get("id");
        int newRowID = (int)getRow(file, row+1).get("id");

        double cidx = (double)getChar(file, row,col).get("idx");

        dbUtil.query("UPDATE `character` set paragraph = " + newRowID + " where paragraph = " + currRowID + " and idx >= " + cidx);
    }

    public void removeRowBackspace(String file, int row, int col){


        int preRowChars = (int)countChars(file,row-1);
        if (preRowChars != 0) preRowChars -= 1;
        double base_col_idx = (double)this.getChar(file,row-1,preRowChars).get("idx");



        int paragraph_id = (int)this.getRow(file,row).get("id");
        int prev_paragraph_id = (int)this.getRow(file,row-1).get("id");

        dbUtil.query("UPDATE `character` set idx = idx + " + base_col_idx + " , paragraph = " + prev_paragraph_id + " where paragraph = " + paragraph_id);
        dbUtil.query("DELETE from `paragraph` where id = " + paragraph_id);

    }
    public void removeRowCanc(String file, int row, int col){


        double base_col_idx = (double)this.getChar(file,row,col-1).get("idx");

        int paragraph_id = (int)this.getRow(file,row).get("id");
        int next_paragraph_id = (int)this.getRow(file,row+1).get("id");

        dbUtil.query("UPDATE `character` set idx = idx + " + base_col_idx + " , paragraph = " + paragraph_id + " where paragraph = " + next_paragraph_id);
        dbUtil.query("DELETE from `paragraph` where id = " + next_paragraph_id);
    }

    public void addRowNoMoveText(String file, int row) {
        double row_idx = -1;
        long rows = this.countRows(file);




        if(row == 0 && rows == 0)
            row_idx = 0;
        else if(rows == row+1 ) {

            row_idx = ((double) getRow(file,row).get("idx")) + 1;
        }else{

            row_idx = (((double)getRow(file,row).get("idx") + (double)getRow(file,row+1).get("idx"))/2);}

        dbUtil.query("INSERT INTO `paragraph` (`file`, `idx`) VALUES ("+getFileID(file,project)+","+row_idx+")");

    }

    public void addChar(String file,int row, int col, String chr)
    {

        HashMap r = this.getRow(file,row);
        int paragraphID = (int)r.get("id");
        double col_idx = -1;
        switch(col){
            case 0: {
                col_idx = 0;
                break;
            }
            default:{
                long chars = this.countChars(file,row);

                if(col == chars)
                    col_idx = (double)getChar(file,row, (int)chars-1).get("idx") + 1;
                else
                    col_idx = (((double)getChar(file,row, col).get("idx") + (double)getChar(file,row, col-1).get("idx")) / 2);


                break;
            }
        }

        try{
            Connection conn = dbUtil.getDB().getConnection();
            PreparedStatement sql = conn.prepareStatement("INSERT INTO `character` (paragraph, `idx`,`value`) VALUES (?,?,?)");
            sql.setInt(1, paragraphID);
            sql.setDouble(2, col_idx);
            sql.setString(3, chr);

            Logger.error("eseguo " + sql.toString());

            sql.executeUpdate();


            ArrayList rs = (ArrayList)dbUtil.query("SELECT *,@curRank := @curRank + 1 AS rank FROM `character` d, (SELECT @curRank := 0) r where d.paragraph = " + paragraphID + " ORDER BY  idx asc;");
            Logger.error("costruito: " + rs.size());
            Iterator irs = rs.iterator();
            HashMap myrow = null;

            while(irs.hasNext())
            {
                myrow = (HashMap) irs.next();
                String query = "update `character` set idx = " + (double)myrow.get("rank") + " where paragraph = " + paragraphID + " and id = " + (int)myrow.get("id");
                Logger.error("eseguo: " + query);
                dbUtil.query(query);
            }

            conn.close();

        }catch(SQLException e)
        {
            Logger.error("*******************************************************************************************************" + e.getMessage());
        }


        //dbUtil.query("INSERT INTO `character` (paragraph, `idx`,`value`) VALUES ("++","+col_idx+",'"+chr+"')");
    }

    public HashMap getChar(String file,int row,int col){

        int paragraphID = (int)this.getRow(file,row).get("id");

        ArrayList chars = getChars(paragraphID);
        HashMap<String, Object> ret;

        if (chars.size() == 0) {
            ret = new HashMap<>();
            ret.put("idx", 0.0);
        } else {

            ret = (HashMap)chars.get(Math.min(col,chars.size()-1));
        }
        return ret;
    }


    public long countChars(String file,int row)
    {
        int paragraphID = (int)this.getRow(file,row).get("id");
        return (long)((ArrayList)getChars(paragraphID)).size();
    }

    public ArrayList getChars(int paragraphID){

        return (ArrayList) dbUtil.query("select * from `character` where paragraph = " + paragraphID + " order by idx asc");
    }

    public long countRows(String file){
        return (long)this.getRows(file).size();
    }

    public ArrayList getRows(String file) {
        int fileID = getFileID(file,project);
        return (ArrayList) dbUtil.query("select * from paragraph where file = "+fileID+" order by idx asc");
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
