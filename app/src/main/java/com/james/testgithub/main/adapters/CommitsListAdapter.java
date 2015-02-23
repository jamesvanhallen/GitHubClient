package com.james.testgithub.main.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.james.testgithub.R;
import com.james.testgithub.main.Another.Commit;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by james on 03.02.15.
 */
public class CommitsListAdapter extends RecyclerView.Adapter<CommitsListAdapter.ViewHolder> {
    private List<Commit> mCommits;
    public CommitsListAdapter(List<Commit> commits) {
        this.mCommits = commits;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_commit, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        h.tvName.setText(mCommits.get(position).getName());
        h.tvMessage.setText(mCommits.get(position).commit.message);
        h.tvAuthor.setText(mCommits.get(position).commit.author.name);
        h.tvDate.setText(mCommits.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return mCommits.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tvCommitHash) TextView tvName;
        @InjectView(R.id.tvCommitMsg) TextView tvMessage;
        @InjectView(R.id.tvCommitAuthor) TextView tvAuthor;
        @InjectView(R.id.tvCommitDate) TextView tvDate;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}


