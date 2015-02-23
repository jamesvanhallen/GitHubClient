package com.james.testgithub.main.Fragments;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.james.testgithub.R;
import com.james.testgithub.main.Activities.AccountActivity;
import com.james.testgithub.main.Activities.CommitsActivity;
import com.james.testgithub.main.Another.GitHubLoader;
import com.james.testgithub.main.Another.Repository;
import com.james.testgithub.main.Another.RequestCallback;
import com.james.testgithub.main.adapters.RepositoryListAdapter;
import com.james.testgithub.main.Another.DataParser;
import com.james.testgithub.main.adapters.RecyclerItemClickListener;


import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by james on 31.01.15.
 */
public class RepositoryFragment extends Fragment {



    private ArrayList<Repository> mRepos;
    private RepositoryListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final int PER_PAGE = 30;
    private int mPage;
    private boolean mIsUpdating = false;
    private boolean mEndOfTheList = false;
    private boolean mIsRetained = false;

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.mTvUser)
    TextView mTvUser;
    @InjectView(R.id.repo_empty_view)
    TextView mEmptyView;


    public RepositoryFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View list = inflater.inflate(R.layout.repository_fragment_list, container, false);
        ButterKnife.inject(this, list);

        if(mRepos==null) mRepos = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        String login = GitHubLoader.getInstance(getActivity()).getCurrentUser().login;
        mTvUser.setText(login);

        loadFirstItems();
        mAdapter = new RepositoryListAdapter(mRepos);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!mIsUpdating && !mEndOfTheList && !mIsRetained && mRepos.size() > 0) {
                    loadMoreItems();
                }
            }
        });

        mRecyclerView.addOnItemTouchListener( new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(getActivity(), CommitsActivity.class);
                        Repository repo = mRepos.get(position);
                        i.putExtra(CommitsActivity.EXTRA_REPO, repo.name);
                        i.putExtra(CommitsActivity.EXTRA_OWNER, repo.owner.login );
                        i.putExtra(CommitsActivity.EXTRA_RI_OWNER_PIC, repo.owner.avatar_url);
                        i.putExtra(CommitsActivity.EXTRA_RI_CREATED, DataParser.formatDate(repo.created_at, DataParser.FORMAT_DATETIME_SL));
                        i.putExtra(CommitsActivity.EXTRA_RI_PUSHED, DataParser.formatDate(repo.pushed_at, DataParser.FORMAT_DATETIME_SL));
                        startActivity(i);
                    }
                }));
        return list;
    }

    @OnClick(R.id.log_out_btn)
    void OnClick(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getText(R.string.logout))
                .setPositiveButton(getText(R.string.positive_alert), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GitHubLoader.getInstance().logout();
                        Intent i = new Intent(getActivity(), AccountActivity.class);
                        i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(i);
                        getActivity().finish();
                    }
                })
                .setNegativeButton(getText(R.string.negative_alert), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alert.create();
        alert.show();
    }

    protected void loadFirstItems() {

        mPage = 1;
        mEndOfTheList = false;
        GitHubLoader.getInstance(getActivity()).getRepositoreisList(PER_PAGE, mPage, new RequestCallback<List<Repository>>() {
            @Override
            public void onSuccess(List<Repository> repositoryList) {
                mRepos.clear();
                mRepos.addAll(repositoryList);
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
        GitHubLoader.getInstance().getRepositoreisList(PER_PAGE, mPage, new RequestCallback<List<Repository>>() {
            @Override
            public void onSuccess(List<Repository> commitList) {
                mRepos.addAll(commitList);
                mAdapter.notifyDataSetChanged();
                mIsUpdating = false;
                if (!GitHubLoader.getInstance().hasLastResponseNextPage()) {
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

            mAdapter.notifyDataSetChanged();

        } else {
            Intent i = new Intent(getActivity(), AccountActivity.class);
            i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(i);
        }
    }
}
