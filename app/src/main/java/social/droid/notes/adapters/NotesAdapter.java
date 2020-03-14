package social.droid.notes.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;

import social.droid.notes.R;
import social.droid.notes.utils.Note;

public class NotesAdapter extends Adapter<NotesAdapter.ViewHolder> {
    private static final String TAG = "NotesAdapter";
    private ArrayList<Note> mNoteArrayList;
    private OnNoteClickListener onNoteClickListener;

    public NotesAdapter(ArrayList<Note> mNoteArrayList2, OnNoteClickListener onNoteClickListener) {
        this.mNoteArrayList = mNoteArrayList2;
        this.onNoteClickListener = onNoteClickListener;
    }

    public @NonNull
    ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_note, parent, false), this.onNoteClickListener);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mNoteName.setText((this.mNoteArrayList.get(position)).getNoteName());
    }

    public int getItemCount() {
        return this.mNoteArrayList.size();
    }

    public interface OnNoteClickListener {
        void onNoteClick(int i);
    }

    class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener {
        ImageView mNoteImageView;
        TextView mNoteName;
        RelativeLayout mNoteRelativeLayout;
        OnNoteClickListener onNoteClickListener;

        public ViewHolder(View itemView, OnNoteClickListener onNoteClickListener) {
            super(itemView);
            this.onNoteClickListener = onNoteClickListener;
            this.mNoteRelativeLayout = itemView.findViewById(R.id.layout_notes);
            this.mNoteImageView = itemView.findViewById(R.id.ivNote);
            this.mNoteName = itemView.findViewById(R.id.tvNoteName);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            Log.d(NotesAdapter.TAG, "onClick: ViewHolder OnClick");
            this.onNoteClickListener.onNoteClick(getAdapterPosition());
        }
    }
}
