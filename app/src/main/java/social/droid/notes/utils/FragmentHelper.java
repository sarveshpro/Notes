package social.droid.notes.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import social.droid.notes.R;

public class FragmentHelper {
     public static void setFragment(Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.frame_main,fragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

}
