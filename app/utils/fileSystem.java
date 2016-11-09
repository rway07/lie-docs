package utils;

import play.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by enrico on 20/10/16.
 */
public class fileSystem {

    public static String getTempDir() {
        return "/tmp";//System.getProperty("java.io.tmpdir");
    }

    public static void cleanWorkingDir(File path){
        if (!path.exists()) {

            path.mkdirs();
            path.setExecutable(true, false);
            path.setReadable(true, false);
            path.setWritable(true, false);
        } else {
            for (File child : path.listFiles())
            {
                child.delete();
            }
        }
    }

    public static File getWorkingDir(String name) {
        String tmpDir = fileSystem.getTempDir();

        String workingDir = tmpDir + "/" + name;

        File cwd = new File(workingDir);
        cleanWorkingDir(cwd);

        return cwd;
    }

    public static BufferedWriter bufferedWriter(String file){

        File f = new File(file);
        if(f.exists())
            f.delete();

        try{
            f.createNewFile();
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            return bw;

        }catch(IOException e){Logger.error("ERORR CREAZIONE FILE: " + e.getMessage());}

        return null;


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
                OutputStream output = null;
                try {
                    output = new BufferedOutputStream(new FileOutputStream(path));
                    output.write(data);
                }
                finally {
                    output.close();
                }

        }catch(Exception e){Logger.error(e.getMessage());}

    }

    public static byte[] readBinary(String path) {
        File file = new File(path);
        byte[] result = new byte[(int) file.length()];
        try {
            InputStream input = null;
            try {
                int totalBytesRead = 0;
                input = new BufferedInputStream(new FileInputStream(file));
                while (totalBytesRead < result.length) {
                    int bytesRemaining = result.length - totalBytesRead;
                    //input.read() returns -1, 0, or more :
                    int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0) {
                        totalBytesRead = totalBytesRead + bytesRead;
                    }
                }
            } finally {
                input.close();
            }
            return result;
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

        return null;
    }
}
