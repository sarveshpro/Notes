package social.droid.notes.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import social.droid.notes.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectNoteFragment extends Fragment {

    //          Request Code Constants  //
    private final int CHOOSE_PDF_REQUEST_CODE = 101;
    //          UI Vars         //
    private View mView;
    public SelectNoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_select_note, container, false);
        Button mSelectNote = mView.findViewById(R.id.btSelectNote);
        mSelectNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mSelectNoteIntent = new Intent();
                mSelectNoteIntent.setType("application/pdf");
                mSelectNoteIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(mSelectNoteIntent,"Select Note PDF"),CHOOSE_PDF_REQUEST_CODE);

            }
        });
        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_PDF_REQUEST_CODE && resultCode == RESULT_OK && data !=null && data.getData() != null) {
            sendNoteToUpload(data.getData());
        }
        else {
            Snackbar.make(mView,"You didn't select any note",Snackbar.LENGTH_LONG).show();
        }
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction mFragmentTransaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.frame_main,fragment);
        mFragmentTransaction.commit();
    }

    private void sendNoteToUpload(Uri uri) {
        UploadNoteFragment mUploadNoteFragment = new UploadNoteFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("NoteUri",uri.toString());
        mUploadNoteFragment.setArguments(mBundle);
        setFragment(mUploadNoteFragment);
    }
}
