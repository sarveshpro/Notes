package social.droid.notes.adapters;

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

public class FolderAdapter extends Adapter<FolderAdapter.ViewHolder> {
    private ArrayList<Note> mFolderArrayList;
    private OnFolderClickListener onFolderClickListener;

    public FolderAdapter(ArrayList<Note> mFolderArrayList, OnFolderClickListener onFolderClickListener) {
        this.mFolderArrayList = mFolderArrayList;
        this.onFolderClickListener = onFolderClickListener;
    }

    public @NonNull
    ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_folder, parent, false), this.onFolderClickListener);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mNoteName.setText((this.mFolderArrayList.get(position)).getNoteName());
    }

    public int getItemCount() {
        return this.mFolderArrayList.size();
    }

    public interface OnFolderClickListener {
        void onFolderClick(String str);
    }

    class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements OnClickListener {
        RelativeLayout mNoteLayout;
        TextView mNoteName;
        ImageView mNoteView;
        OnFolderClickListener onFolderClickListener;

        public ViewHolder(View itemView, OnFolderClickListener onFolderClickListener2) {
            super(itemView);
            this.onFolderClickListener = onFolderClickListener2;
            this.mNoteLayout = itemView.findViewById(R.id.layout_folder);
            this.mNoteView = itemView.findViewById(R.id.ivNoteFolder);
            this.mNoteName = itemView.findViewById(R.id.tvNote);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            this.onFolderClickListener.onFolderClick((FolderAdapter.this.mFolderArrayList.get(getAdapterPosition())).getNoteName());
        }
    }
}
