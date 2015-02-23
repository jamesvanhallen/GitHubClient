package com.james.testgithub.main.Activities;

import android.support.v4.app.Fragment;

import com.james.testgithub.main.Fragments.RepositoryFragment;

/**
 * Created by james on 31.01.15.
 */
public class RepositoryActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RepositoryFragment();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
