package social.droid.notes.utils;

public class Note {
    String noteName;
    String noteURL;

    public Note() {
    }

    public Note(String noteName, String noteURL) {
        this.noteName = noteName;
        this.noteURL = noteURL;
    }

    public String getNoteName() {
        return this.noteName;
    }

    public String getNoteURL() {
        return this.noteURL;
    }
}