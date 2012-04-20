package com.playground_soft.chord;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.playground_soft.chord.db.DatabaseHelper;
import com.playground_soft.chordlib.Document;

import android.os.Environment;

public class FileSystemUtils {
    
    public static final File ROOT_DIR = Environment.getExternalStorageDirectory();
    public static final File EXTERNAL_DIR = new File(ROOT_DIR.getAbsolutePath() + "/chordpro");
    public static final File INTERNAL_DIR = new File(ROOT_DIR.getAbsoluteFile()
            + "/Android/data/com.playground_soft.chord/cache/");
    
    public static void copyToLibrary(File input, DatabaseHelper dbHelper) throws IOException {
        String filename = input.getName();
        File externalFile = new File(EXTERNAL_DIR, filename);
        if(externalFile.exists()) {
            for(int i = 0; externalFile.exists();i ++) {
                int dot = filename.lastIndexOf('.');
                String name = filename.substring(0, dot);
                String ext = filename.substring(dot+1);
                
                externalFile = new File(EXTERNAL_DIR, name+i+'.'+ext);
            }
        }
        externalFile.createNewFile();
        copyFile(input, externalFile);
        
        File internalFile = new File(INTERNAL_DIR.getAbsoluteFile() + "/"
                + externalFile.getName());
        
        internalFile.createNewFile();

        copyFile(externalFile, internalFile);
        
        Document doc = Document.createFromFile(internalFile);

        dbHelper.createOrUpdate(doc.title, doc.subtitle,
                internalFile.getAbsolutePath());
    }
    
    public static void copyFile(File from, File to) throws IOException {
        FileReader reader = new FileReader(from);
        FileWriter writer = new FileWriter(to);

        final int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        int read = 0;
        while ((read = reader.read(buffer, 0, buffer.length)) != -1) {
            writer.write(buffer, 0, read);
        }

        reader.close();
        writer.close();
    }
    

}
