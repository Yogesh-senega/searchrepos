package com.systems.senega.searchrepos.adapter;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.systems.senega.searchrepos.R;
import com.systems.senega.searchrepos.model.GitHubRepo;

import java.util.ArrayList;
import java.util.List;

public class GithubRepoAdapter extends BaseAdapter {
    private List<GitHubRepo> gitHubRepos = new ArrayList<>();

    @Override
    public int getCount() {
        return gitHubRepos == null ? 0 : gitHubRepos.size();
    }

    @Override
    public Object getItem(int position) {
        return (position < 0 || position >= gitHubRepos.size()) ? null : gitHubRepos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = (convertView != null ? convertView : createView(parent));
        final GitHubRepoViewHolder viewHolder = (GitHubRepoViewHolder) view.getTag();
        viewHolder.setGitHubRepo((GitHubRepo) getItem(position));
        return view;
    }

    public void setGitHubRepos(@Nullable List<GitHubRepo> repos) {
        if (repos == null)
            return;
        gitHubRepos.clear();
        gitHubRepos.addAll(repos);
        notifyDataSetChanged();
    }

    private View createView(ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.list_github_repo, parent, false);
        final GitHubRepoViewHolder viewHolder = new GitHubRepoViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    private static class GitHubRepoViewHolder {

        private TextView textRepoName;
        private TextView textRepoDescription;
        private TextView textLanguage;
        private TextView textStars;

        GitHubRepoViewHolder(View view) {
            textRepoName = (TextView) view.findViewById(R.id.text_repo_name);
            textRepoDescription = (TextView) view.findViewById(R.id.text_repo_description);
            textLanguage = (TextView) view.findViewById(R.id.text_language);
            textStars = (TextView) view.findViewById(R.id.text_stars);
        }

        void setGitHubRepo(GitHubRepo gitHubRepo) {
            textRepoName.setText(gitHubRepo.getName());
            textRepoDescription.setText(gitHubRepo.getDescription());
            textLanguage.setText("Language: " + gitHubRepo.getLanguage());
            textStars.setText("Stars: " + gitHubRepo.getStargazersCount());
        }
    }

}
