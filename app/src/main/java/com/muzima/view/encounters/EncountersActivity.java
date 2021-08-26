/*
 * Copyright (c) The Trustees of Indiana University, Moi University
 * and Vanderbilt University Medical Center. All Rights Reserved.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license
 * with additional health care disclaimer.
 * If the user is an entity intending to commercialize any application that uses
 *  this code in a for-profit venture,please contact the copyright holder.
 */
package com.muzima.view.encounters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.Menu;
import com.muzima.MuzimaApplication;
import com.muzima.R;
import com.muzima.adapters.ListAdapter;
import com.muzima.adapters.encounters.EncountersByPatientAdapter;
import com.muzima.adapters.patients.PatientAdapterHelper;
import com.muzima.api.model.Encounter;
import com.muzima.api.model.Patient;
import com.muzima.utils.LanguageUtil;
import com.muzima.utils.ThemeUtils;
import com.muzima.view.BroadcastListenerActivity;
import com.muzima.view.patients.PatientSummaryActivity;

import static com.muzima.utils.DateUtils.getFormattedDate;

public class EncountersActivity extends BroadcastListenerActivity implements AdapterView.OnItemClickListener, ListAdapter.BackgroundListQueryTaskListener {
    private Patient patient;
    private EncountersByPatientAdapter encountersByPatientAdapter;
    private View noDataView;
    private final ThemeUtils themeUtils = new ThemeUtils();
    private final LanguageUtil languageUtil = new LanguageUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeUtils.onCreate(this);
        languageUtil.onCreate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_encounters);
        patient = (Patient) getIntent().getSerializableExtra(PatientSummaryActivity.PATIENT);
        setupPatientMetadata();
        setupStillLoadingView();
        setupPatientEncounters();
        setTitle(R.string.title_activity_client_encounters);
            logEvent("VIEW_CLIENT_ENCOUNTERS","{\"patientuuid\":\""+patient.getUuid()+"\"}");
    }

    @Override
    protected void onResume() {
        super.onResume();
        themeUtils.onResume(this);
        languageUtil.onResume(this);
    }

    private void setupPatientMetadata() {

        TextView patientName = findViewById(R.id.patientName);

        patientName.setText(PatientAdapterHelper.getPatientFormattedName(patient));

        ImageView genderIcon = findViewById(R.id.genderImg);
        int genderDrawable = patient.getGender().equalsIgnoreCase("M") ? R.drawable.gender_male : R.drawable.ic_female;
        genderIcon.setImageDrawable(getResources().getDrawable(genderDrawable));

        TextView dob = findViewById(R.id.dob);
        if(patient.getBirthdate() != null) {
            dob.setText(String.format("DOB: %s", getFormattedDate(patient.getBirthdate())));
        }else{
            dob.setText(String.format(""));
        }

        TextView patientIdentifier = findViewById(R.id.patientIdentifier);
        patientIdentifier.setText(patient.getIdentifier());
    }

    private void setupPatientEncounters(){
        ListView  encountersLayout = findViewById(R.id.encounter_list);
        encountersByPatientAdapter = new EncountersByPatientAdapter(EncountersActivity.this,
                R.layout.item_encounter,
                ((MuzimaApplication) getApplicationContext()).getEncounterController(),patient);
        encountersByPatientAdapter.setBackgroundListQueryTaskListener(this);
        encountersLayout.setEmptyView(noDataView);
        encountersLayout.setAdapter(encountersByPatientAdapter);
        encountersLayout.setOnItemClickListener(this);
        encountersByPatientAdapter.reloadData();

    }

    private void setupNoDataView() {
        noDataView = findViewById(R.id.no_data_layout);
        TextView noDataMsgTextView = findViewById(R.id.no_data_msg);
        noDataMsgTextView.setText(getResources().getText(R.string.info_encounter_unavailable));
    }

    private void setupStillLoadingView() {

        noDataView = findViewById(R.id.no_data_layout);
        TextView noDataMsgTextView = findViewById(R.id.no_data_msg);
        noDataMsgTextView.setText(R.string.general_loading_encounters);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Encounter encounter = encountersByPatientAdapter.getItem(position);
        Intent intent = new Intent(this,EncounterSummaryActivity.class);
        intent.putExtra(EncounterSummaryActivity.ENCOUNTER,encounter);
        intent.putExtra(PatientSummaryActivity.PATIENT,patient);
        startActivity(intent);
    }

    @Override
    public void onQueryTaskStarted() {}

    @Override
    public void onQueryTaskFinish() {
        if(encountersByPatientAdapter.isEmpty()) {
            setupNoDataView();
        }
    }

    @Override
    public void onQueryTaskCancelled(){}

    @Override
    public void onQueryTaskCancelled(Object errorDefinition){}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.encounter_list, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        