package personal.droid.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //          UI Components       //
    private Button mSignIn;
    private TextView mCreateAccount;
    private EditText mUserName,mUserPassword;
    //          FireBase Vars       //
    private FirebaseAuth mFireBaseAuth;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mCreateAccount = findViewById(R.id.tvCreateOne);
        mSignIn = findViewById(R.id.btSignIn);
        mUserName = findViewById(R.id.etUserName);
        mUserPassword = findViewById(R.id.etUserPassword);
        mCreateAccount.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
        mFireBaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCreateOne:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
            case R.id.btSignIn:
                String userName = mUserName.getText().toString();
                String password = mUserPassword.getText().toString();
                getLogin(userName,password);
                break;
        }
    }

    private void getLogin(String userName,String password) {
        mFireBaseAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent dashboardIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                    dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(dashboardIntent);
                    finish();
                }
                else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(LoginActivity.this,"Invalid Crendentials !",Toast.LENGTH_LONG).show();
                    }
                    else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(LoginActivity.this,"Please Create an Account !",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"Error Occurred !",Toast.LENGTH_LONG).show();
                        Log.d(TAG, "getLogin() "+ task.getException());

                    }
                }
            }
        });

    }

}
