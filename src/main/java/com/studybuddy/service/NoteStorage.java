package com.studybuddy.service;

import com.studybuddy.model.Note;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NoteStorage {

    private static final String FILE_PATH = "data/notes.dat";

    // save all notes to a file
    public static void saveNotes(List<Note> notes) {

        File file = new File(FILE_PATH);

        try {
            File parent = file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            try (ObjectOutputStream output =
                   new ObjectOutputStream(
                       new FileOutputStream(file)
                   )) {

                output.writeObject(new ArrayList <>(notes));
            }

            System.out.println("notes saved successfully!");

        } catch (IOException e) {
            System.out.println(
                "Error saving notes: " + e.getMessage()
            );           
        }
    }

    // load notes from a saved file 
    public static List<Note> loadNotes() {

        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream input =
                new ObjectInputStream(
                    new FileInputStream(file)
                )) {

            Object savedData = input.readObject();

            if(savedData instanceof List<?>) {

              List<?> loadedList = (List<?>) savedData;
              List<Note> notes = new ArrayList<>();

              for (Object item : loadedList) {
                if (item instanceof Note) {
                    notes.add((Note) item);
                }
              }

              System.out.println(
                  "notes loaded successfully!!"
              );

              return notes;
            }
        
         } catch (IOException | ClassNotFoundException e) {

            System.out.println(
                "error loading notes:" + e.getMessage()
            );
         }

         return new ArrayList<>();
         
    }

    // delete the saved notes file
    public static boolean deleteStorageFile() {

        File file = new File(FILE_PATH);

        if (file.exists()) {
            return file.delete();
        }

        return true;
    }

    // check wheter saved notes exist
    public static boolean hasSavedNotes() {

        File file = new File(FILE_PATH);

        return file.exists() && file.length() > 0;
    }
}