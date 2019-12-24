package personal.droid.notes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import personal.droid.notes.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //          UI Components       //
    private ProgressBar mLoginProgressBar;
    private EditText mUserName,mUserPassword;
    //          FireBase Vars       //
    private FirebaseAuth mFireBaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView mCreateAccountLink = findViewById(R.id.tvCreateAccountLink);
        Button mSignIn = findViewById(R.id.btSignIn);
        mUserName = findViewById(R.id.etUserName);
        mUserPassword = findViewById(R.id.etUserPassword);
        mLoginProgressBar = findViewById(R.id.pbLogin);
        mCreateAccountLink.setOnClickListener(this);
        mSignIn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFireBaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFireBaseUser = mFireBaseAuth.getCurrentUser();
        if(mFireBaseUser!=null)  {
            finish();
            Intent homeIntent = new Intent(LoginActivity.this,HomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);


        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCreateAccountLink:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
            case R.id.btSignIn:
                mLoginProgressBar.setVisibility(View.VISIBLE);
                String userName = mUserName.getText().toString();
                String password = mUserPassword.getText().toString();
                loginUserWithTypedCredentials(userName,password);
                break;
        }
    }

    private void loginUserWithTypedCredentials(String userName,String password) {
        final View mView = findViewById(android.R.id.content);
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(mInputMethodManager!=null)
        mInputMethodManager.hideSoftInputFromWindow(mUserPassword.getWindowToken(),0);
        mFireBaseAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    mLoginProgressBar.setVisibility(View.GONE);
                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homeIntent);
                    finish();
                }
                else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        mLoginProgressBar.setVisibility(View.GONE);
                        Snackbar.make(mView,"Invalid Credentials !",Snackbar.LENGTH_LONG).show();

                    }
                    else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        mLoginProgressBar.setVisibility(View.GONE);
                        Snackbar.make(mView,"Please Create an Account !",Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        mLoginProgressBar.setVisibility(View.GONE);
                        Snackbar.make(mView,"Error Occurred ! Please Try Again",Snackbar.LENGTH_LONG).show();
                      }
                }
            }
        });

    }
}
