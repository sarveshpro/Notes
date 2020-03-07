package social.droid.notes.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest.Builder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import social.droid.notes.R;

public class RegisterActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "RegisterActivity";
    public FirebaseAuth mFireBaseAuth;
    public DatabaseReference mFireBaseDataBaseReference;
    public ProgressBar mRegisterProgressBar;
    private EditText mEmail;
    private EditText mName;
    private EditText mPassword;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TextView mSignIn = findViewById(R.id.tvSignIn);
        Button mRegister = findViewById(R.id.btRegister);
        this.mName = findViewById(R.id.etName);
        this.mEmail = findViewById(R.id.etEmail);
        this.mPassword = findViewById(R.id.etPassword);
        this.mRegisterProgressBar = findViewById(R.id.pbRegister);
        mSignIn.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        this.mFireBaseAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btRegister) {
            this.mRegisterProgressBar.setVisibility(View.VISIBLE);
            getUserRegistered(this.mName.getText().toString(), this.mEmail.getText().toString(), this.mPassword.getText().toString());
        } else if (id == R.id.tvSignIn) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void getUserRegistered(final String name, final String email, String password) {
        final View mView = findViewById(android.R.id.content);
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (mInputMethodManager != null) {
            mInputMethodManager.hideSoftInputFromWindow(this.mPassword.getWindowToken(), 0);
        }
        this.mFireBaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (mFireBaseUser != null) {
                        RegisterActivity.this.mFireBaseDataBaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mFireBaseUser.getUid());
                        mFireBaseUser.updateProfile(new Builder().setDisplayName(name).build());
                        HashMap<String, String> mUserDetails = new HashMap<>();
                        mUserDetails.put("Name", name);
                        mUserDetails.put("Email", email);
                        RegisterActivity.this.mFireBaseDataBaseReference.setValue(mUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    RegisterActivity.this.mFireBaseAuth.signOut();
                                    RegisterActivity.this.mRegisterProgressBar.setVisibility(View.GONE);
                                    Snackbar.make(mView, name + ",your account has been created successfully", Snackbar.LENGTH_LONG).show();
                                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    RegisterActivity.this.startActivity(loginIntent);
                                    RegisterActivity.this.finish();
                                    return;
                                }
                                RegisterActivity.this.mRegisterProgressBar.setVisibility(View.GONE);
                                Snackbar.make(mView, name + ",there was a error while creating account", Snackbar.LENGTH_LONG).show();
                                Log.e(RegisterActivity.TAG, "Error in Creating Account" + task.getException());
                            }
                        });
                    }
                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    RegisterActivity.this.mRegisterProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "User already exists. Try Signing In", Toast.LENGTH_LONG).show();
                } else {
                    RegisterActivity.this.mRegisterProgressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Error in Creating Account", Toast.LENGTH_LONG).show();
                    Log.d(RegisterActivity.TAG, "Error in Creating Account " + task.getException());
                }
            }
        });
    }
}