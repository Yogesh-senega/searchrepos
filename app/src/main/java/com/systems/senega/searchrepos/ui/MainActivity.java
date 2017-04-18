package com.systems.senega.searchrepos.ui;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.systems.senega.searchrepos.R;
import com.systems.senega.searchrepos.adapter.GithubRepoAdapter;
import com.systems.senega.searchrepos.model.GitHubRepo;
import com.systems.senega.searchrepos.network.DialUp;
import com.systems.senega.searchrepos.rest.RestInterface;
import com.systems.senega.searchrepos.rest.RestService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private GithubRepoAdapter adapter = new GithubRepoAdapter();
    private Disposable disposable;

    private LinearLayout root;
    private EditText editTextUsername;
    private Button buttonSearch;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = (LinearLayout) findViewById(R.id.root);
        final ListView listView = (ListView) findViewById(R.id.list_view_repos);
        listView.setAdapter(adapter);
        editTextUsername = (EditText) findViewById(R.id.edit_text_username);
        buttonSearch = (Button) findViewById(R.id.button_search);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        DialUp.listen(MainActivity.this)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean value) {
                        if(!value)
                            Snackbar.make(root, "Network not available.", Snackbar.LENGTH_LONG)
                                    .show();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        Observable<String> buttonClickStream = createButtonClickObservable();
        Observable<String> textChangeStream = createTextChangeObservable();
        Observable<String> searchTextObservable = Observable.merge(buttonClickStream,
                textChangeStream);

        disposable = searchTextObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> showProgressBar())
                .observeOn(Schedulers.io())
                .switchMap(this::getStarredRepos)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gitHubRepos -> {
                    hideProgressBar();
                    showResults(gitHubRepos);
                });
    }

    private void showResults(List<GitHubRepo> gitHubRepos) {
        adapter.setGitHubRepos(gitHubRepos);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF4081"),
                PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
    }

    private Observable<String> createButtonClickObservable(){
        return Observable.create(e -> {
            buttonSearch.setOnClickListener(v ->
                    e.onNext(editTextUsername.getText().toString()));
            e.setCancellable(() -> buttonSearch.setOnClickListener(null));
        });
    }

    private Observable<String> createTextChangeObservable(){
        Observable<String> textChangeObservable =
                Observable.create(e -> {
                    final TextWatcher textWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start,
                                                      int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            e.onNext(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {}
                    };
                    editTextUsername.addTextChangedListener(textWatcher);
                    e.setCancellable(() -> editTextUsername.removeTextChangedListener(textWatcher));
                });
        return textChangeObservable.filter(s -> s.length() > 2).debounce(1000, TimeUnit.MILLISECONDS);
    }

    private Observable<List<GitHubRepo>> getStarredRepos(String username) {
        RestInterface restService = RestService.getClient().create(RestInterface.class);
        return restService.getStarredRepos(username)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void onDestroy() {
        if(disposable != null && !disposable.isDisposed())
            disposable.dispose();
        super.onDestroy();
    }
}