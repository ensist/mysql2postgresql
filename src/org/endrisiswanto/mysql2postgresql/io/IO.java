package org.endrisiswanto.mysql2postgresql.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class IO {

    public static String readFile(String path) {
        BufferedReader br = null;
        StringBuilder sContent = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(path));
            String sLine = br.readLine();
            while (sLine != null) {
                sContent.append(sLine).append("\n");
                sLine = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return sContent.toString();
    }

    public static void write(String filename, String content, boolean append) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, append)))) {
            out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
