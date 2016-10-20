package utils;

import play.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by enrico on 20/10/16.
 */
public class fileSystem {

    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    public static File getWorkingDir(String name) {
        String tmpDir = fileSystem.getTempDir();

        String workingDir = tmpDir + "/" + name;

        File cwd = new File(workingDir);
        if (!cwd.exists()) {

            cwd.mkdirs();
            cwd.setExecutable(true, false);
            cwd.setReadable(true, false);
            cwd.setWritable(true, false);
        } else {
            for (File child : cwd.listFiles())
            {
                child.delete();
            }
        }

        return cwd;
    }

    public static void writeText(String path,String content){
        try{
        PrintWriter f = new PrintWriter(path,"UTF-8");
        f.write(content);
        f.close();
        File file = new File(path);
        file.setReadable(true,false);

        } catch (Exception e){
            Logger.error(e.getMessage());
        }
    }

    public static void writeBinary(String path,byte[] data){

        try{

            FileOutputStream f = new FileOutputStream(new File(path));
            for (int i = 0; i <= data.length; ++i)
                f.write(i);
            f.close();

        }catch(Exception e){Logger.info(e.getMessage());}

    }

    public static byte[] readBinary(String path){
        try{
            Path objFile = Paths.get(path);
            return Files.readAllBytes(objFile);
        } catch (Exception e ){Logger.error(e.getMessage());}
        return null;
    }


}
