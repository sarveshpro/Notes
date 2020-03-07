package social.droid.notes.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Objects;

import social.droid.notes.R;
import social.droid.notes.utils.Note;

public class UploadNoteFragment extends Fragment {
    private ArrayList<String> mNoteCategoryArrayList;
    private Spinner mNoteCategorySpinner;
    private ProgressBar mNoteUploadProgressBar;
    private ArrayList<String> mSubjectArrayList;
    private Spinner mSubjectSpinner;
    private View mView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_upload_note, container, false);
        this.mSubjectSpinner = this.mView.findViewById(R.id.spinnerSubject);
        this.mNoteCategorySpinner = this.mView.findViewById(R.id.spinnerNoteCategory);
        Button mUploadNote = this.mView.findViewById(R.id.btUploadNote);
        final EditText mNoteCategoryNumber = this.mView.findViewById(R.id.etNoteCategoryNumber);
        this.mNoteUploadProgressBar = this.mView.findViewById(R.id.pbUploadNote);
        Context mContext = getContext();
        this.mSubjectArrayList = new ArrayList<>();
        setupSubjectArrayAdapter(mContext);
        this.mNoteCategoryArrayList = new ArrayList<>();
        setupNoteCategoryArrayAdapter(mContext);
        mUploadNote.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (UploadNoteFragment.this.mSubjectSpinner.getSelectedItemPosition() == 0) {
                    Snackbar.make(UploadNoteFragment.this.mView, "Please Select Subject", Snackbar.LENGTH_LONG).show();
                } else if (UploadNoteFragment.this.mNoteCategorySpinner.getSelectedItemPosition() == 0) {
                    Snackbar.make(UploadNoteFragment.this.mView, "Please Select Note Category", Snackbar.LENGTH_LONG).show();
                } else if (UploadNoteFragment.this.isEmpty(mNoteCategoryNumber)) {
                    Snackbar.make(UploadNoteFragment.this.mView, "Please Enter Note Number", Snackbar.LENGTH_LONG).show();
                } else {
                    String subject = UploadNoteFragment.this.mSubjectSpinner.getSelectedItem().toString().trim();
                    String noteCategory = UploadNoteFragment.this.mNoteCategorySpinner.getSelectedItem().toString().trim();
                    String noteCategoryNumber = mNoteCategoryNumber.getText().toString().trim();
                    UploadNoteFragment.this.UploadNotePDFToCloudStorage(Uri.parse(UploadNoteFragment.this.getArguments().getString("NoteUri")), subject, noteCategory, noteCategoryNumber);
                }
            }
        });
        return this.mView;
    }

    private void UploadNotePDFToCloudStorage(Uri data, final String subject, final String noteCategory, final String noteCategoryNumber) {

        final StorageReference mFireBaseStorageReference = FirebaseStorage.getInstance().getReference().child("/Subjects /"+subject+" /"+noteCategory+" /"+noteCategory+" "+noteCategoryNumber);
        mFireBaseStorageReference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> mURi = taskSnapshot.getStorage().getDownloadUrl();
                while(true){
                    if (mURi.isComplete()) break;
                }
                Uri mNoteUri = mURi.getResult();

                assert mNoteUri != null;
                Note mUploadNotePDF = new Note(subject + " " + noteCategory + " " + noteCategoryNumber, mNoteUri.toString());
                DatabaseReference mFireBaseDatabaseReference = FirebaseDatabase.getInstance().getReference("/Subjects/"+subject+"/"+noteCategory+"/"+noteCategoryNumber);
                mFireBaseDatabaseReference.setValue(mUploadNotePDF);
                mNoteUploadProgressBar.setVisibility(View.GONE);
                HomeFragment mHomeFragment = new HomeFragment();
                setFragment(mHomeFragment);
                BottomNavigationView mBottomNavigationView = Objects.requireNonNull(getActivity()).findViewById(R.id.nav_main);
                mBottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                mNoteUploadProgressBar.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mNoteUploadProgressBar.setVisibility(View.GONE);
                Snackbar.make(mView,"Note Upload Failure ! Try Again",Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction mFragmentTransaction = (Objects.requireNonNull(getActivity())).getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.frame_main, fragment);
        mFragmentTransaction.commit();
    }

    private boolean isEmpty(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString().trim());
    }

    private void populateSubjectArrayList() {
        this.mSubjectArrayList.add("Select");
        this.mSubjectArrayList.add("Applied Mathematics IV");
        this.mSubjectArrayList.add("Computer Networks");
        this.mSubjectArrayList.add("Operating Systems");
        this.mSubjectArrayList.add("Computer Organization and Architecture");
        this.mSubjectArrayList.add("Automata Theory");
        this.mSubjectArrayList.add("Networking Lab");
        this.mSubjectArrayList.add("UNIX Lab");
        this.mSubjectArrayList.add("Microprocessor Programming Lab");
        this.mSubjectArrayList.add("Python Lab");
    }

    private void populateNoteCategoryArrayList() {
        this.mNoteCategoryArrayList.add("Select");
        this.mNoteCategoryArrayList.add("Notes");
        this.mNoteCategoryArrayList.add("Assignments");
        this.mNoteCategoryArrayList.add("Experiments");
        this.mNoteCategoryArrayList.add("Others");
    }

    private void setupSubjectArrayAdapter(Context context) {
        populateSubjectArrayList();
        ArrayAdapter<String> mSubjectAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, this.mSubjectArrayList);
        mSubjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.mSubjectSpinner.setAdapter(mSubjectAdapter);
    }

    private void setupNoteCategoryArrayAdapter(Context context) {
        populateNoteCategoryArrayList();
        ArrayAdapter<String> mNoteCategoryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, this.mNoteCategoryArrayList);
        mNoteCategoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.mNoteCategorySpinner.setAdapter(mNoteCategoryAdapter);
    }
}