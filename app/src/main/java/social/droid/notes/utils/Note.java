package social.droid.notes.utils;

import android.os.Parcel;
import android.os.Parcelable;


public class Note  {
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