package com.james.testgithub.main.Activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.james.testgithub.main.Fragments.AccountFragment;


public class AccountActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
