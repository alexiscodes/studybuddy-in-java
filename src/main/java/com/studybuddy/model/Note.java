

package com.studybuddy.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String subject;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Note(String title, String subject, String content) {
        this.title = title;
        this.subject = subject;
        this.content = content;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // for notes title
    public String getTitle() {
        return title;
    }

    // for updating note title
    public void setTitle(String title) {
        this.title = title;
        updateTimestamp();
    }

    // get subject
    public String getSubject() {
        return subject;
    }

    // update subject
    public void setSubject(String subject) {
        this.subject = subject;
        updateTimestamp();
    }

    // get note content
    public String getContent() {
        return content;
    }

    // update note content
    public void setContent(String content) {
        this.content = content;
        updateTimestamp();
    }

    // get the creation date
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // get last updated date
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // update the last modified time
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // format creation date
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        return createdAt.format(formatter);
    }

    // format last updated date
    public String getFormattedUpdatedAt() {
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        return updatedAt.format(formatter);
    }

    // displays note info
    @Override
    public String toString() {
        return title + "(" + subject +")";
    }
}

