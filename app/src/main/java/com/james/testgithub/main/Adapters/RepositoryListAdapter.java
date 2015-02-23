package com.james.testgithub.main.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.james.testgithub.R;
import com.james.testgithub.main.Another.Repository;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by james on 03.02.15.
 */
public class RepositoryListAdapter extends RecyclerView.Adapter<RepositoryListAdapter.ViewHolder> {
    private List<Repository> mItems;
    private Context mCont;
    public RepositoryListAdapter(List<Repository> repos) {
        mItems = repos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_repository, viewGroup, false);
        mCont = v.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int i) {
        h.tvRepoName.setText(mItems.get(i).name);
        h.tvRepoOwner.setText(mItems.get(i).owner.login);
        h.tvRepoForks.setText(String.valueOf(mItems.get(i).forks_count));
        h.tvRepoWatchers.setText(String.valueOf(mItems.get(i).watchers_count));
        Picasso.with(mCont).cancelRequest(h.ivOwnerPic);
        Picasso.with(mCont).load(mItems.get(i).owner.avatar_url)
                .resize(mCont.getResources().getDimensionPixelSize(R.dimen.repo_list_avatar), mCont.getResources().getDimensionPixelSize(R.dimen.repo_list_avatar))
                .into(h.ivOwnerPic);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivRepoOwnerPic)public ImageView ivOwnerPic;
        @InjectView(R.id.tvRepoName) TextView tvRepoName;
        @InjectView(R.id.tvRepoOwner)TextView tvRepoOwner;
        @InjectView(R.id.tvRepoForks) TextView tvRepoForks;
        @InjectView(R.id.tvRepoWatchers) TextView tvRepoWatchers;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
