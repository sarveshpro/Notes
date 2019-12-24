package personal.droid.notes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import personal.droid.notes.R;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    //          UI Components       //
    private EditText mName, mEmail, mPassword;
    private ProgressBar mRegisterProgressBar;
    //          FireBase Vars       //
    private FirebaseAuth mFireBaseAuth;
    private DatabaseReference mFireBaseDataBaseReference;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TextView mSignIn = findViewById(R.id.tvSignIn);
        Button mRegister = findViewById(R.id.btRegister);
        mName = findViewById(R.id.etName);
        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etPassword);
        mRegisterProgressBar = findViewById(R.id.pbRegister);
        mSignIn.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mFireBaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSignIn:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btRegister:
                mRegisterProgressBar.setVisibility(ProgressBar.VISIBLE);
                String name, email, password;
                name = mName.getText().toString();
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                getUserRegistered(name, email, password);
                break;
        }

    }

    private void getUserRegistered(final String name, final String email, String password) {
        final View mView = findViewById(android.R.id.content);
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mInputMethodManager!=null)
        mInputMethodManager.hideSoftInputFromWindow(mPassword.getWindowToken(),0);
        mFireBaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (mFireBaseUser != null) {
                                String fireBaseUserId = mFireBaseUser.getUid();
                                mFireBaseDataBaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fireBaseUserId);
                                HashMap<String, String> mUserDetails = new HashMap<>();
                                mUserDetails.put("Name", name);
                                mUserDetails.put("Email", email);
                                mFireBaseDataBaseReference.setValue(mUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mFireBaseAuth.signOut();
                                            mRegisterProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                            Snackbar.make(mView,name + ",your account has been created successfully",Snackbar.LENGTH_SHORT).show();
                                            Intent loginIntent = new Intent(RegisterActivity.this,
                                                    LoginActivity.class);
                                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(loginIntent);
                                            finish();


                                        } else {
                                            mRegisterProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                            Snackbar.make(mView,name + ",there was a error while creating account",Snackbar.LENGTH_LONG).show();
                                            Log.e(TAG, "Error in Creating Account " + task.getException());
                                        }

                                    }
                                });
                            }
                             
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    mRegisterProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this, "User already exists. Try Signing In",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    mRegisterProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this,"Error in Creating Account", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "Error in Creating Account " + task.getException());
                                }

                            }


                        }
                        
                    });

                }
    }
