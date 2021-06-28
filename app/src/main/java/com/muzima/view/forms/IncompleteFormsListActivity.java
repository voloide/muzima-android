package com.muzima.view.forms;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.muzima.MuzimaApplication;
import com.muzima.R;
import com.muzima.adapters.IncompleteFormsWithDataAdapter;
import com.muzima.model.CompleteFormWithPatientData;
import com.muzima.model.IncompleteFormWithPatientData;
import com.muzima.tasks.LoadFormsWithDataTask;
import com.muzima.utils.ThemeUtils;
import com.muzima.view.MainDashboardActivity;

import java.util.ArrayList;
import java.util.List;

public class IncompleteFormsListActivity extends AppCompatActivity implements IncompleteFormsWithDataAdapter.OnFormClickedListener {
    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private View notDataView;
    private IncompleteFormsWithDataAdapter recyclerViewAdapter;
    private List<IncompleteFormWithPatientData> formList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        new ThemeUtils().onCreate(IncompleteFormsListActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms_list_layout);
        initializeResources();
        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            launchDashboard();
        }
        return true;
    }

    private void loadData() {
        ((MuzimaApplication) getApplicationContext()).getExecutorService()
                .execute(new LoadFormsWithDataTask(getApplicationContext(), null, true, new LoadFormsWithDataTask.LoadFormsFinishedCallback() {
                    @Override
                    public void onIncompleteFormsLoaded(final List<IncompleteFormWithPatientData> forms) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                if (forms.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "No items available", Toast.LENGTH_LONG).show();
                                    notDataView.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    notDataView.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    formList.clear();
                                    formList.addAll(forms);
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCompleteFormsLoaded(final List<CompleteFormWithPatientData> forms) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }
                }));
    }

    private void initializeResources() {
        toolbar = findViewById(R.id.forms_list_toolbar);
        recyclerView = findViewById(R.id.forms_list_recycler_view);
        progressBar = findViewById(R.id.forms_list_progress);
        notDataView = findViewById(R.id.no_data_view);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.info_incomplete_form));
        }
        recyclerViewAdapter = new IncompleteFormsWithDataAdapter(getApplicationContext(), formList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onFormClicked(int position) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        launchDashboard();
    }

    private void launchDashboard() {
        startActivity(new Intent(getApplicationContext(), MainDashboardActivity.class));
        finish();
    }
}
