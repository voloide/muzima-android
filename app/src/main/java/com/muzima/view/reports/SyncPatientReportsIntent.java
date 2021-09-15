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

import android.app.Activity;
import com.muzima.view.SyncIntent;

import static com.muzima.utils.Constants.DataSyncServiceConstants.SYNC_PATIENT_REPORTS_HEADERS;
import static com.muzima.utils.Constants.DataSyncServiceConstants.SYNC_TYPE;
import static com.muzima.utils.Constants.SyncPatientReportsConstants.PATIENT_UUID;

public class SyncPatientReportsIntent extends SyncIntent {
    public SyncPatientReportsIntent(Activity activity, String patientUuid) {
        super(activity);
        putExtra(SYNC_TYPE, SYNC_PATIENT_REPORTS_HEADERS);
        putExtra(PATIENT_UUID, patientUuid);
    }
}
