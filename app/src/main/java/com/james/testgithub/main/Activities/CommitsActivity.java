package com.james.testgithub.main.Activities;

import android.support.v4.app.Fragment;

import com.james.testgithub.main.Fragments.CommitsFragment;

import java.util.HashMap;

/**
 * Created by james on 03.02.15.
 */
public class CommitsActivity extends SingleFragmentActivity {
    public static final String EXTRA_REPO = "inst.repo";
    public static final String EXTRA_OWNER = "inst.owner";
    public static final String EXTRA_RI_OWNER_NAME = "inst.repo_info.owner_name";
    public static final String EXTRA_RI_CREATED = "inst.repo_info.created_at";
    public static final String EXTRA_RI_PUSHED = "inst.repo_info.pushed_at";
    public static final String EXTRA_RI_OWNER_PIC = "inst.repo_info.avatar";

    @Override
    protected Fragment createFragment() {
        HashMap<String,String> repoInfo = new HashMap<>();
        repoInfo.put(CommitsFragment.REPOINFO_CREATED, getIntent().getStringExtra(EXTRA_RI_CREATED));
        repoInfo.put(CommitsFragment.REPOINFO_PUSHED, getIntent().getStringExtra(EXTRA_RI_PUSHED));
        repoInfo.put(CommitsFragment.REPOINFO_OWNER_NAME, getIntent().getStringExtra(EXTRA_RI_OWNER_NAME));
        repoInfo.put(CommitsFragment.REPOINFO_OWNER_PIC, getIntent().getStringExtra(EXTRA_RI_OWNER_PIC));
        return CommitsFragment.getInstance(getIntent().getStringExtra(EXTRA_OWNER), getIntent().getStringExtra(EXTRA_REPO), repoInfo);
    }

}
