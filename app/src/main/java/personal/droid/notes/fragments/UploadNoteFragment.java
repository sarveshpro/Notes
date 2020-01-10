package personal.droid.notes.fragments;


import android.content.Context;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Objects;

import personal.droid.notes.R;
import personal.droid.notes.utils.UploadNotePDF;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("SpellCheckingInspection")
public class UploadNoteFragment extends Fragment {

    private ArrayList<String> mSubjectArrayList;
    private ArrayList<String> mNoteCategoryArrayList;
    private Spinner mSubjectSpinner;
    private Spinner mNoteCategorySpinner;
    private ProgressBar mNoteUploadProgressBar;



    private View mView;


    public UploadNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_upload_note, container, false);
        mSubjectSpinner = mView.findViewById(R.id.spinnerSubject);
        mNoteCategorySpinner = mView.findViewById(R.id.spinnerNoteCategory);
        Button mUploadNote = mView.findViewById(R.id.btUploadNote);
        final EditText mNoteCategoryNumber = mView.findViewById(R.id.etNoteCategoryNumber);
        mNoteUploadProgressBar = mView.findViewById(R.id.pbUploadNote);
        Context mContext = getContext();

        mSubjectArrayList = new ArrayList<>();
        setupSubjectArrayAdapter(mContext);

        mNoteCategoryArrayList = new ArrayList<>();
        setupNoteCategoryArrayAdapter(mContext);

        mUploadNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mSubjectSpinner.getSelectedItemPosition() == 0) {
                    Snackbar.make(mView,"Please Select Subject",Snackbar.LENGTH_LONG).show();
                }
                else if(mNoteCategorySpinner.getSelectedItemPosition() == 0) {
                    Snackbar.make(mView,"Please Select Note Category",Snackbar.LENGTH_LONG).show();
                }
                else if(isEmpty(mNoteCategoryNumber)) {
                    Snackbar.make(mView,"Please Enter Note Number",Snackbar.LENGTH_LONG).show();
                }
                else {
                    String subject = mSubjectSpinner.getSelectedItem().toString().trim();
                    String noteCategory = mNoteCategorySpinner.getSelectedItem().toString().trim();
                    String noteCategoryNumber = mNoteCategoryNumber.getText().toString().trim();
                    assert getArguments() != null;
                    Uri mURI = Uri.parse(getArguments().getString("NoteUri"));
                    UploadNotePDFToCloudStorage(mURI,subject,noteCategory,noteCategoryNumber);
                }
            }
        });

        // Inflate the layout for this fragment
        return mView;


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
                UploadNotePDF mUploadNotePDF = new UploadNotePDF(subject+" "+noteCategory+" "+noteCategoryNumber,mNoteUri.toString());
                DatabaseReference mFireBaseDatabaseReference = FirebaseDatabase.getInstance().getReference("/Subjects/"+subject+"/"+noteCategory+"/"+noteCategoryNumber);
                mFireBaseDatabaseReference.setValue(mUploadNotePDF);
                mNoteUploadProgressBar.setVisibility(View.GONE);
                HomeFragment mHomeFragment = new HomeFragment();
                setFragment(mHomeFragment);
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
        FragmentTransaction mFragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.frame_main,fragment);
        mFragmentTransaction.commit();
    }
    private boolean isEmpty(EditText editText) {
            String mEditText = editText.getText().toString().trim();
            return TextUtils.isEmpty(mEditText);
    }

    private void populateSubjectArrayList() {
        mSubjectArrayList.add("Select");
        mSubjectArrayList.add("Applied Mathematics IV");
        mSubjectArrayList.add("Computer Networks");
        mSubjectArrayList.add("Operating Systems");
        mSubjectArrayList.add("Computer Organization and Architecture");
        mSubjectArrayList.add("Automata Theory");
        mSubjectArrayList.add("Networking Lab");
        mSubjectArrayList.add("UNIX Lab");
        mSubjectArrayList.add("Microprocessor Programming Lab");
        mSubjectArrayList.add("Python Lab");
    }

    private void populateNoteCategoryArrayList() {
        mNoteCategoryArrayList.add("Select");
        mNoteCategoryArrayList.add("Notes");
        mNoteCategoryArrayList.add("Assignments");
        mNoteCategoryArrayList.add("Experiments");
        mNoteCategoryArrayList.add("Others");
    }

    private void setupSubjectArrayAdapter(Context context) {
        populateSubjectArrayList();
        ArrayAdapter<String> mSubjectAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,mSubjectArrayList);
        mSubjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSubjectSpinner.setAdapter(mSubjectAdapter);
    }

    private void setupNoteCategoryArrayAdapter(Context context) {
        populateNoteCategoryArrayList();
        ArrayAdapter<String> mNoteCategoryAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,mNoteCategoryArrayList);
        mNoteCategoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mNoteCategorySpinner.setAdapter(mNoteCategoryAdapter);
    }

}


