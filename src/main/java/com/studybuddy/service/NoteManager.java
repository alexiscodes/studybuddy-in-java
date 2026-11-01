
package com.studybuddy.service;

import com.studybuddy.model.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NoteManager {

    private final List<Note> notes = new ArrayList<>();

    // load previously saved notes when the manager starts
    public NoteManager() {
        notes.addAll(NoteStorage.loadNotes());
    }

    // saves all the current notes
    private void save() {
        NoteStorage.saveNotes(notes);
    }

    // adding a new note
    public void addNote(String title, String subject, String content) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                "Note title cannot be empty!"
            );
        }

        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException(
                "Subject cannot be empty!"
            );
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                "note content cannot be empty!"
            );
        }

        Note note = new Note(
            title.trim(),
            subject.trim(),
            content.trim()
        );

        notes.add(note);

        save();
    }

    // get all notes
    public List<Note> getAllNotes() {
        return List.copyOf(notes);
    }

    // get a note thru its position
    public Note getNote(int index) {

        if (index < 0 || index >= notes.size()) {
            return null;
        }

        return notes.get(index);
    }

    // editing an existing note
    public boolean editNote(
        int index,
        String title,
        String subject,
        String content
    ) {

        if (index < 0 || index >= notes.size()) {
            return false;
        }

        if (title == null || title.isBlank()
                || subject == null || subject.isBlank()
                || content == null || content.isBlank()) {
            return false;
        }

        Note note = notes.get(index);

        note.setTitle(title.trim());
        note.setSubject(subject.trim());
        note.setContent(content.trim());

        save();

        return true;
    }

    // delete a note
    public boolean deleteNote(int index) {

        if (index < 0 || index >= notes.size()) {
            return false;
        }

        notes.remove(index);

        save();

        return true;
    }

    // search notes by title,subject or content
    public List<Note> searchNotes(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return getAllNotes();
        }

        String search = keyword.toLowerCase().trim();

        return notes.stream()
            .filter(note ->
                note.getTitle().toLowerCase().contains(search)
                || note.getSubject().toLowerCase().contains(search)
                || note.getContent().toLowerCase().contains(search)
            )
            .collect(Collectors.toList());
    }
    
    // get notes belonging to a particular sub
    public List<Note> getNotesBySubject(String subject) {

        if (subject == null || subject.isBlank()) {
            return List.of();
        }

        return notes.stream()
            .filter(note -> 
                note.getSubject().equalsIgnoreCase(subject.trim())
            )
            .collect(Collectors.toList());

    }

    // get the total number of notes
    public int getNoteCount() {
        return notes.size();
    }

    // delete all notes
    public void clearNotes() {
        notes.clear();
        save();
    }
}