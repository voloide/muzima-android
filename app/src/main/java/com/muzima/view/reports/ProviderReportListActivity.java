/*
 * Copyright (c) The Trustees of Indiana University, Moi University
 * and Vanderbilt University Medical Center. All Rights Reserved.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license
 * with additional health care disclaimer.
 * If the user is an entity intending to commercialize any application that uses
 * this code in a for-profit venture, please contact the copyright holder.
 */

package com.muzima.view.reports;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.muzima.MuzimaApplication;
import com.muzima.R;
import com.muzima.adapters.ListAdapter;
import com.muzima.adapters.reports.AvailableReportsAdapter;
import com.muzima.model.AvailableForm;
import com.muzima.utils.LanguageUtil;
import com.muzima.utils.ThemeUtils;
import com.muzima.view.custom.ActivityWithBottomNavigation;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ProviderReportListActivity extends ActivityWithBottomNavigation implements AdapterView.OnItemClickListener,
        ListAdapter.BackgroundListQueryTaskListener {
    private ListView listView;
    private View noDataView;
    private FrameLayout progressBarContainer;
    private AvailableReportsAdapter reportsAdapter;
    private final LanguageUtil languageUtil = new LanguageUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.getInstance().onCreate(this,true);
        languageUtil.onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_layout_list);
        progressBarContainer = findViewById(R.id.progressbarContainer);
        loadBottomNavigation();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_provider_reports));
        }

        setupListView();
        setupNoDataView();
        logEvent("VIEW_PROVIDER_REPORTS");
    }

    private void setupListView() {
        reportsAdapter = new AvailableReportsAdapter(this, R.layout.item_forms_list,
                ((MuzimaApplication)getApplicationContext()).getFormController(), ((MuzimaApplication)getApplicationContext()).getObservationController());
        reportsAdapter.setBackgroundListQueryTaskListener(this);
        listView = findViewById(R.id.list);
        listView.setAdapter(reportsAdapter);
        listView.setOnItemClickListener(this);
    }
    private void setupNoDataView() {

        noDataView = findViewById(R.id.no_data_layout);

        TextView noDataMsgTextView = findViewById(R.id.no_data_msg);
        noDataMsgTextView.setText(getResources().getText(R.string.info_downloaded_reports_unavailable));

        TextView noDataTipTextView = findViewById(R.id.no_data_tip);
        noDataTipTextView.setText(R.string.hint_reports_unavailable);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        AvailableForm report = reportsAdapter.getItem(position);
        Intent intent = null;
        if(report.isProviderReport()) {
            intent = new Intent(this, ProviderReportViewActivity.class);
            intent.putExtra(ProviderReportViewActivity.REPORT, report);
        } else if(report.isProviderPerformanceReport()){
            intent = new Intent(this, ProviderPerformanceReportViewActivity.class);
            intent.putExtra(ProviderPerformanceReportViewActivity.REPORT, report);
        }

        if(intent != null) startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reportsAdapter.reloadData();
    }

    @Override
    public void onQueryTaskStarted() {
        listView.setVisibility(INVISIBLE);
        noDataView.setVisibility(INVISIBLE);
        listView.setEmptyView(progressBarContainer);
        progressBarContainer.setVisibility(VISIBLE);
    }

    @Override
    public void onQueryTaskFinish() {
        listView.setVisibility(VISIBLE);
        listView.setEmptyView(noDataView);
        progressBarContainer.setVisibility(INVISIBLE);
    }

    @Override
    public void onQueryTaskCancelled() {
        Log.e(getClass().getSimpleName(), "Cancelled...");
    }

    @Override
    public void onQueryTaskCancelled(Object errorDefinition) {
        Log.e(getClass().getSimpleName(), "Cancelled...");

    }

    @Override
    protected int getBottomNavigationMenuItemId() {
        return R.id.action_reports;
    }

}
