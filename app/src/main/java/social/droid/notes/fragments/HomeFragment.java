package social.droid.notes.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask.TaskSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.MissingFormatArgumentException;

import social.droid.notes.R;
import social.droid.notes.adapters.FolderAdapter;
import social.droid.notes.adapters.FolderAdapter.OnFolderClickListener;
import social.droid.notes.adapters.NotesAdapter;
import social.droid.notes.adapters.NotesAdapter.OnNoteClickListener;
import social.droid.notes.utils.Note;

public class HomeFragment extends Fragment implements OnFolderClickListener, OnNoteClickListener {
    private static final String TAG = "HomeFragment";
    public String path = "/Subjects";
    private DatabaseReference mFireBaseDatabaseReference;
    private StorageReference mFireBaseStorageReference;
    private Adapter mFolderRecyclerViewAdapter;
    private ProgressBar mLoadNotesProgressBar;
    private RecyclerView mNoteRecyclerView;
    private LayoutManager mNoteRecyclerViewLayoutManager;
    private ArrayList<Note> mNotesArrayList;
    private Adapter mNotesRecyclerViewAdapter;
    private View mView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_home, container, false);
        this.mLoadNotesProgressBar = this.mView.findViewById(R.id.pbLoadNote);
        this.mNoteRecyclerView = this.mView.findViewById(R.id.mNoteRecyclerView);
        this.mNoteRecyclerView.setHasFixedSize(true);
        this.mNoteRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        this.mNoteRecyclerView.setLayoutManager(this.mNoteRecyclerViewLayoutManager);
        this.mNotesArrayList = new ArrayList<>();
        populateSubjects();
        this.mFolderRecyclerViewAdapter = new FolderAdapter(this.mNotesArrayList, this);
        this.mNotesRecyclerViewAdapter = new NotesAdapter(this.mNotesArrayList, this);
        this.mNoteRecyclerView.setAdapter(this.mFolderRecyclerViewAdapter);
        this.mView.setFocusableInTouchMode(true);
        this.mView.requestFocus();
        this.mView.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 4) {
                    return false;
                }
                HomeFragment.this.removeCurrentNode();
                HomeFragment homeFragment = HomeFragment.this;
                homeFragment.populateNote(homeFragment.path);
                return true;
            }
        });
        return this.mView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: is called");
        outState.putParcelableArrayList("notes",mNotesArrayList);
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            mNotesArrayList = savedInstanceState.getParcelableArrayList("notes");

        }catch (Exception e) {
            System.out.println("onsavedInstanceState problem");
            e.printStackTrace();
        }
        }


    private void removeCurrentNode() {
        StringBuffer mPath = new StringBuffer();
        mPath.append(this.path);
        this.path = this.path.replace(mPath.substring(mPath.lastIndexOf("/")), " ");
        Log.d(TAG, "removeCurrentNode: " + this.path);
    }

    private void getNotes(String noteDetail) {
        mFireBaseDatabaseReference = FirebaseDatabase.getInstance().getReference(noteDetail);
        mFireBaseDatabaseReference.addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(HomeFragment.TAG, "onDataChange: " + dataSnapshot);
                HomeFragment.this.mNotesArrayList.add(dataSnapshot.getValue(Note.class));
                HomeFragment.this.mNotesRecyclerViewAdapter.notifyDataSetChanged();
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getDetailNotes(String notes) {
        this.mFireBaseDatabaseReference = this.mFireBaseDatabaseReference.child(notes);
        this.mFireBaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Log.d(HomeFragment.TAG, "geDetailNotes: " + dataSnapshot.getKey());
                HomeFragment.this.mNotesArrayList.add(new Note(dataSnapshot.getKey(), "null"));
                HomeFragment.this.mNotesRecyclerViewAdapter.notifyDataSetChanged();
            }

            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Log.d(HomeFragment.TAG, "geDetailNotes: " + dataSnapshot.getKey());
                HomeFragment.this.mNotesArrayList.add(new Note(dataSnapshot.getKey(), "null"));
                HomeFragment.this.mNotesRecyclerViewAdapter.notifyDataSetChanged();

            }

            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void populateNoteName(String subject) {
        Log.d(TAG, "populateNoteName: "+ subject);
        mFireBaseDatabaseReference = FirebaseDatabase.getInstance().getReference(subject);
        Log.d(TAG, "Database Reference: " + mFireBaseDatabaseReference);
        mFireBaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Log.d(HomeFragment.TAG, "populateNoteName: " + dataSnapshot.getKey());
                HomeFragment.this.mNotesArrayList.add(new Note(dataSnapshot.getKey(), "null"));
                HomeFragment.this.mNotesRecyclerViewAdapter.notifyDataSetChanged();
            }

            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Log.d(HomeFragment.TAG, "populateNoteName: " + dataSnapshot.getKey());
                HomeFragment.this.mNotesArrayList.add(new Note(dataSnapshot.getKey(), "null"));
                HomeFragment.this.mNotesRecyclerViewAdapter.notifyDataSetChanged();
            }

            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void populateSubjects() {
        this.mFireBaseDatabaseReference = FirebaseDatabase.getInstance().getReference("/Subjects");
        this.mFireBaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot mNoteSnapshot : dataSnapshot.getChildren()) {
                    HomeFragment.this.mNotesArrayList.add(new Note(mNoteSnapshot.getKey(), "null"));
                    Log.d(HomeFragment.TAG, "populateSubjects(): " + mNoteSnapshot.getKey());
                    HomeFragment.this.mFolderRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        this.mFireBaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HomeFragment.this.mLoadNotesProgressBar.setVisibility(View.GONE);
            }


            public void onCancelled(@NonNull DatabaseError databaseError) {
                HomeFragment.this.mLoadNotesProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void populateNote(String subject) {
        Log.d(TAG, "Path Accessed : " + subject);
        mFireBaseDatabaseReference = FirebaseDatabase.getInstance().getReference(subject);
        Log.d(TAG, "populateNote: "+ mFireBaseDatabaseReference);
        mFireBaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Log.d(HomeFragment.TAG, "populateNote: " + dataSnapshot.getKey());
                HomeFragment.this.mNotesArrayList.add(new Note(dataSnapshot.getKey(), "null"));
                HomeFragment.this.mFolderRecyclerViewAdapter.notifyDataSetChanged();
            }

            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void onFolderClick(String folderName) {
        path = path + "/" + folderName;
        Snackbar.make(mView, path + " Clicked", Snackbar.LENGTH_LONG).show();
        mNotesArrayList.clear();
        Log.d(TAG, "onFolderClick: "+ path);
        if(path.contains("Assignments") | path.contains("Experiments") | path.contains("Others")) {
            Log.d(TAG, "onFolderClick: calling getDetailsNotes method");
            mNoteRecyclerView.setAdapter(this.mNotesRecyclerViewAdapter);
            mNoteRecyclerView.setLayoutManager(this.mNoteRecyclerViewLayoutManager);
            getNotes(path);

        }
        else {
            populateNote(path);
        }
    }

    public void onNoteClick(int position) {

        Log.d(TAG, mNotesArrayList.toString() + "onNoteClick: " + (this.mNotesArrayList.get(position)).getNoteURL());
        this.mFireBaseStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl((this.mNotesArrayList.get(position)).getNoteURL());
        try {
            final File localFile = File.createTempFile((this.mNotesArrayList.get(position)).getNoteName(), "pdf");
            this.mFireBaseStorageReference.getFile(localFile).addOnCompleteListener(new OnCompleteListener<TaskSnapshot>() {
                public void onComplete(@NonNull Task<TaskSnapshot> task) {
                    Snackbar.make(HomeFragment.this.mView, "Download Done", Snackbar.LENGTH_LONG).show();
                    Log.d(HomeFragment.TAG, "onComplete: " + localFile.getPath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(mView, "Download Done " + localFile.getAbsolutePath(), Snackbar.LENGTH_INDEFINITE).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Snackbar.make(mView, position + " Clicked", Snackbar.LENGTH_LONG).show();
        Intent sendIntent = new Intent();
        sendIntent.setAction("android.intent.action.VIEW");
        sendIntent.putExtra("android.intent.extra.TEXT", "This is my text to send.");
        sendIntent.setDataAndType(Uri.parse((this.mNotesArrayList.get(position)).getNoteURL()), "application/pdf");
        startActivity(sendIntent);
    }
}