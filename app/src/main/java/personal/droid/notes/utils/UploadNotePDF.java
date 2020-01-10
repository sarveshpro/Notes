package personal.droid.notes.utils;

public class UploadNotePDF {
    String noteName;
    String noteURL;

    public UploadNotePDF() {
    }

    public UploadNotePDF(String noteName, String noteURL) {
        this.noteName = noteName;
        this.noteURL = noteURL;
    }

    public String getNoteName() {
        return noteName;
    }

    public String getNoteURL() {
        return noteURL;
    }
}
