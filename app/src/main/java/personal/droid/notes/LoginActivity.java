package personal.droid.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

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
        mFireBaseAuth = FirebaseAuth.getInstance();
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
        mFireBaseAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    mLoginProgressBar.setVisibility(View.GONE);
                    Intent dashboardIntent = new Intent(LoginActivity.this,MainActivity.class);
                    dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(dashboardIntent);
                    finish();
                }
                else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        mLoginProgressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,"Invalid Credentials !",Toast.LENGTH_LONG).show();

                    }
                    else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        mLoginProgressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,"Please Create an Account !",Toast.LENGTH_LONG).show();
                    }
                    else {
                        mLoginProgressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,"Error Occurred ! Please Try Again",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }
}
