package social.droid.notes.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import social.droid.notes.R;
import social.droid.notes.activities.LoginActivity;

public class AccountFragment extends Fragment {
    private View mView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_account, container, false);
        TextView mUserName = this.mView.findViewById(R.id.tvUserName);
        Button mSignOut = this.mView.findViewById(R.id.btSignOut);
        FirebaseUser mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFireBaseUser != null) {
            mUserName.setText(mFireBaseUser.getDisplayName());
        }
        mSignOut.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                AccountFragment accountFragment = AccountFragment.this;
                accountFragment.startActivity(new Intent(accountFragment.mView.getContext(), LoginActivity.class));
            }
        });
        return this.mView;
    }
}