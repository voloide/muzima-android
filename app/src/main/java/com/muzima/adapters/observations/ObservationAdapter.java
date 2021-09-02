/*
 * Copyright (c) The Trustees of Indiana University, Moi University
 * and Vanderbilt University Medical Center. All Rights Reserved.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license
 * with additional health care disclaimer.
 * If the user is an entity intending to commercialize any application that uses
 *  this code in a for-profit venture,please contact the copyright holder.
 */

package com.muzima.adapters.observations;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.FragmentActivity;
import com.muzima.R;
import com.muzima.adapters.ListAdapter;
import com.muzima.adapters.RecyclerAdapter;
import com.muzima.api.model.Concept;
import com.muzima.api.model.Observation;
import com.muzima.api.model.Patient;
import com.muzima.controller.ConceptController;
import com.muzima.controller.EncounterController;
import com.muzima.controller.ObservationController;
import com.muzima.view.patients.ClientSummaryActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class ObservationAdapter extends RecyclerAdapter<ObservationAdapter.ViewHolder> {
    protected Context context;
    private Patient patient;
    final ConceptController conceptController;
    final EncounterController encounterController;
    final ObservationController observationController;
    private BackgroundListQueryTaskListener backgroundListQueryTaskListener;
    private AsyncTask<?, ?, ?> backgroundQueryTask;

    public BackgroundListQueryTaskListener getBackgroundListQueryTaskListener() {
        return backgroundListQueryTaskListener;
    }

    ObservationAdapter(Context context, EncounterController encounterController, ConceptController conceptController,
                       ObservationController observationController, Patient patient) {
        this.context = context;
        this.patient = patient;
        this.encounterController = encounterController;
        this.conceptController = conceptController;
        this.observationController = observationController;
    }

    public void setBackgroundListQueryTaskListener(BackgroundListQueryTaskListener backgroundListQueryTaskListener) {
        this.backgroundListQueryTaskListener = backgroundListQueryTaskListener;
    }

    public void cancelBackgroundQueryTask() {
        if (backgroundQueryTask != null) {
            backgroundQueryTask.cancel(true);
        }
    }

    void setRunningBackgroundQueryTask(AsyncTask<?, ?, ?> backgroundQueryTask) {
        this.backgroundQueryTask = backgroundQueryTask;
    }

    abstract class ViewHolder {

        final LayoutInflater inflater;
        LinearLayout observationLayout;
        final List<LinearLayout> observationViewHolders;

        ViewHolder() {
            observationViewHolders = new ArrayList<>();
            inflater = LayoutInflater.from(context);
        }

        void addEncounterObservations(List<Observation> observations) {
            //draws each concept row
            for (int i = 0; i < observations.size(); i++) { //populate this concept's rows.
                LinearLayout layout = getLinearLayoutForObservation(i);
                Observation observation = observations.get(i);
                setObservation(layout, observation);
            }

            shrink(observations.size()); //mover to next row
        }

        LinearLayout getLinearLayoutForObservation(int i) {
            LinearLayout layout;
            if (observationViewHolders.size() <= i) {
                layout = (LinearLayout) inflater.inflate(getObservationLayout(), null);
                observationViewHolders.add(layout);
                observationLayout.addView(layout);
            }
            else {
                layout = observationViewHolders.get(i);
            }

            setStyle(layout);
            return layout;
        }

        protected abstract void setObservation(LinearLayout layout, Observation observation);

        protected abstract int getObservationLayout();

        private void shrink(int startIndex) {
            List<LinearLayout> holdersToRemove = new ArrayList<>();
            for (int i = startIndex; i < observationViewHolders.size(); i++) {
                holdersToRemove.add(observationViewHolders.get(i));
            }
            removeObservations(holdersToRemove);
        }

        private void setStyle(LinearLayout layout) {
            int observationPadding = (int) context.getResources().getDimension(R.dimen.observation_element_padding);
            int height = (int) context.getResources().getDimension(getObservationElementHeight());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            layoutParams.setMargins(observationPadding, observationPadding, observationPadding, observationPadding);
            layout.setLayoutParams(layoutParams);
        }

        protected abstract int getObservationElementHeight();

        private void removeObservations(List<LinearLayout> holdersToRemove) {
            observationViewHolders.removeAll(holdersToRemove);
            for (LinearLayout linearLayout : holdersToRemove) {
                observationLayout.removeView(linearLayout);
            }
        }

        String getConceptDisplay(Concept concept) {
            String text = concept.getName();
            if (concept.getConceptType().getName().equals(Concept.NUMERIC_TYPE)) {
                text += " (" + concept.getUnit() + ")";
            }
            return text;
        }
    }
}
