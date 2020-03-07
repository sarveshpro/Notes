package social.droid.notes.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import social.droid.notes.R;
import social.droid.notes.fragments.AccountFragment;
import social.droid.notes.fragments.HomeFragment;
import social.droid.notes.fragments.SelectNoteFragment;

public class HomeActivity extends AppCompatActivity {


    private HomeFragment mHomeFragment;
    private SelectNoteFragment mSelectNoteFragment;
    private AccountFragment mAccountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView mBottomNavigationView = findViewById(R.id.nav_main);

        mHomeFragment = new HomeFragment();
        mAccountFragment = new AccountFragment();
        mSelectNoteFragment = new SelectNoteFragment();
        setFragment(mHomeFragment);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        setFragment(mHomeFragment);
                        return true;
                    case R.id.nav_upload:
                        setFragment(mSelectNoteFragment);
                        return true;
                    case R.id.nav_account:
                        setFragment(mAccountFragment);
                        return true;
                    default:
                        return false;

                }
            }


        });


    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.frame_main,fragment);
        mFragmentTransaction.commit();
    }
}
