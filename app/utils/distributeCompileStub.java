package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class distributeCompileStub {

    public ArrayList commands() {
        BufferedReader br = null;
        String filename = "/Users/kain/IdeaProjects/test/src/test_file";
        String currentLine;
        String objectPattern = "^\\w+\\.o";
        String headerPattern = "\\w+\\.h";
        String sourcePattern = "-c\\s*([^\\n\\r]*)";
        String currentObject = new String();

        HashMap<String, HashMap<String, ArrayList<String>>> list =
                new HashMap<String, HashMap<String, ArrayList<String>>>();

        try {
            br = new BufferedReader(new FileReader(filename));

            // Regex patterns
            Pattern o = Pattern.compile(objectPattern);
            Pattern h = Pattern.compile(headerPattern);
            Pattern s = Pattern.compile(sourcePattern);

            ArrayList<String> headers = new ArrayList<String>();
            HashMap<String, ArrayList<String>> source;
            // loop for each line
            while ((currentLine = br.readLine()) != null) {
                // looking for object files
                Matcher m = o.matcher(currentLine);

                while (m.find()) {
                    if (m.group(0).length() != 0) {
                        currentObject = m.group(0);
                    }
                }

                // looking for headers files
                m = h.matcher(currentLine);
                while (m.find()) {
                    for (int i = 0; i <= m.groupCount(); i++) {
                        headers.add(m.group(i));
                    }
                }

                // looking for source files
                m = s.matcher(currentLine);
                source = new HashMap<String, ArrayList<String>>();
                while (m.find()) {
                    if (!headers.isEmpty()) {
                        ArrayList<String> temp = new ArrayList<String>(headers);
                        source.put(m.group(1), temp);
                        list.put(currentObject, source);
                        headers.clear();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Errore: " + e.toString());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.out.println("Errore: " + e.toString());
            }
        }

        // Build commands string
        ArrayList<String> commands = new ArrayList<>();
        for (HashMap.Entry<String, HashMap<String, ArrayList<String>>> object : list.entrySet()) {
            String currentObj = object.getKey();
            String currentCmd = "gcc -o " + currentObj;

            for (HashMap.Entry<String, ArrayList<String>> source : object.getValue().entrySet()) {
                String currentSource = source.getKey();
                currentCmd += " -c " + currentSource + " ";

                for (String h : source.getValue()) {
                    currentCmd += h + " ";
                }
            }
            commands.add(currentCmd);
        }

        for (String cmd : commands) {
            System.out.println(cmd);
        }

        return commands;
    }
}
