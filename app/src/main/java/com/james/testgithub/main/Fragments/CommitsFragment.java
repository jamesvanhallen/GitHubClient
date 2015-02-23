package com.james.testgithub.main.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.james.testgithub.R;
import com.james.testgithub.main.Activities.AccountActivity;
import com.james.testgithub.main.Another.Commit;
import com.james.testgithub.main.Another.GitHubLoader;
import com.james.testgithub.main.Another.RequestCallback;
import com.james.testgithub.main.adapters.CommitsListAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by james on 03.02.15.
 */
public class CommitsFragment extends Fragment {

    private static final String REPO = "repo";
    private static final String OWNER = "owner";
    private static final String REPO_INFO = "repo_info";
    public static final String REPOINFO_CREATED = "created_at";
    public static final String REPOINFO_PUSHED = "pushed_at";
    public static final String REPOINFO_OWNER_NAME = "owner_name";
    public static final String REPOINFO_OWNER_PIC = "avatar";


    private boolean mIsUpdating = false;
    private boolean mEndOfTheList = false;
    private boolean mIsRetained = false;

    private ArrayList<Commit> mCommits;
    private CommitsListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mPage;

    @InjectView(R.id.commits_emptyView)
    TextView mEmptyView;

    public CommitsFragment() {
    }

    public static CommitsFragment getInstance(String owner, String repo, HashMap<String,String> repoInfo) {
        Bundle args = new Bundle();
        args.putString(OWNER, owner);
        args.putString(REPO, repo);
        args.putSerializable(REPO_INFO, repoInfo);
        CommitsFragment fragment = new CommitsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View list = inflater.inflate(R.layout.commits_fragment_list, container, false);
        ButterKnife.inject(this, list);

        if(mCommits == null) mCommits = new ArrayList<>();
        mRecyclerView = (RecyclerView)list.findViewById(R.id.repository_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);


        HashMap<String,String> repoInfo = (HashMap<String,String>)getArguments().getSerializable(REPO_INFO);

        if(repoInfo.get(REPOINFO_OWNER_PIC)!=null) {
            Picasso.with(getActivity()).load( repoInfo.get(REPOINFO_OWNER_PIC) )
                    .resize( getResources().getDimensionPixelSize(R.dimen.commit_list_header_avatar), getResources().getDimensionPixelSize(R.dimen.commit_list_header_avatar) )
                    .into( (ImageView) list.findViewById(R.id.ivRepoOwnerPic) );
        }

        ((TextView)list.findViewById(R.id.tvRepoName)).setText(getArguments().getString(REPO));
        if(repoInfo.get(REPOINFO_OWNER_NAME)!=null) {
            ((TextView)list.findViewById(R.id.tvRepoOwner)).setText( repoInfo.get(REPOINFO_OWNER_NAME)+" ("+getArguments().getString(OWNER)+")" );
        } else {
            ((TextView)list.findViewById(R.id.tvRepoOwner)).setText( getArguments().getString(OWNER) );
        }

        if(repoInfo.get(REPOINFO_CREATED)!=null) {
            ((TextView)list.findViewById(R.id.tvRepoDateCreated)).setText( repoInfo.get(REPOINFO_CREATED ));
        } else {
            ((TextView)list.findViewById(R.id.tvRepoDateCreated)).setText( getString(R.string.na ));
        }

        if(repoInfo.get(REPOINFO_PUSHED)!=null) {
            ((TextView)list.findViewById(R.id.tvRepoDatePushed)).setText( repoInfo.get(REPOINFO_PUSHED) );
        } else {
            ((TextView)list.findViewById(R.id.tvRepoDatePushed)).setText( getString(R.string.na) );
        }

        loadFirstItems();
        mAdapter = new CommitsListAdapter(mCommits);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mIsUpdating && !mEndOfTheList && !mIsRetained && mCommits.size()>0) {
                        loadMoreItems();
                }
            }
        });
        return list;
    }

    protected void loadFirstItems() {
        mIsUpdating = true;
        mEndOfTheList = false;
        mPage = 1;
        GitHubLoader.getInstance().getRepositoryCommits(getArguments().getString(OWNER),
                getArguments().getString(REPO), 50, mPage, new RequestCallback<List<Commit>>() {
                    @Override
                    public void onSuccess(List<Commit> commitList) {
                        mCommits.clear();
                        mCommits.addAll(commitList);
                        mAdapter.notifyDataSetChanged();
                        mIsUpdating = false;

                        if (!GitHubLoader.getInstance().hasLastResponseNextPage()) {
                            mEndOfTheList = true;
                        }

                        if (mAdapter.getItemCount() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }

                    }
                    @Override
                    public void onFailure(int error_code) {
                        failureHandler(error_code);
                    }
                });
    }

    protected void loadMoreItems() {
        mIsUpdating = true;
        mPage++;

        GitHubLoader.getInstance().getRepositoryCommits( getArguments().getString(OWNER),
                getArguments().getString(REPO), 50, mPage, new RequestCallback<List<Commit>>() {
            @Override
            public void onSuccess(List<Commit> commitList) {
                mCommits.addAll(commitList);
                mAdapter.notifyDataSetChanged();
                mIsUpdating = false;
                if(!GitHubLoader.getInstance().hasLastResponseNextPage()) {
                    mEndOfTheList = true;
                }
            }

            @Override
            public void onFailure(int error_code) {
                mPage--;
                failureHandler(error_code);
            }
        });
    }

    private void failureHandler(int error_code) {
        if(error_code==GitHubLoader.ERR_CODE_CONFLICT) {
            if (mAdapter.getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }

        } else if(error_code == GitHubLoader.ERR_CODE_NOT_FOUND) {
            mCommits.clear();
            mAdapter.notifyDataSetChanged();


        } else {
            Intent i = new Intent(getActivity(), AccountActivity.class);
            i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(i);
        }
    }
}
