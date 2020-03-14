package social.droid.notes.utils;

import android.os.Parcel;
import android.os.Parcelable;


public class Note implements Parcelable {
    String noteName;
    String noteURL;

    public Note() {
    }

    public Note(Parcel mParcel) {
        noteName = mParcel.readString();
        noteURL = mParcel.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(noteName);
        dest.writeString(noteURL);

    }
    public static Creator<Note> CREATOR = new Creator<Note>() {

        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }

    };
}