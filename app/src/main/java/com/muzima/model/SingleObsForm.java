/*
 * Copyright (c) The Trustees of Indiana University, Moi University
 * and Vanderbilt University Medical Center. All Rights Reserved.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license
 * with additional health care disclaimer.
 * If the user is an entity intending to commercialize any application that uses
 * this code in a for-profit venture, please contact the copyright holder.
 */

package com.muzima.model;

import com.muzima.api.model.Concept;

import java.util.Date;

public class SingleObsForm {
    private Concept concept;
    private Date date;
    /**
     * Can Be;
     * NUMERIC_TYPE = "Numeric";
     * CODED_TYPE = "Coded";
     * DATETIME_TYPE = "Datetime";
     * DATE_TYPE = "Date";
     *
     */
    private String inputDataType;
    private String inputValue;
    private int readingCount;
    private String inputDateValue;

    public SingleObsForm(Concept concept, Date date, String inputDataType, String inputValue, int readingCount) {
        this.concept = concept;
        this.date = date;
        this.inputDataType = inputDataType;
        this.inputValue = inputValue;
        this.readingCount = readingCount;
    }

    public Concept getConcept() {
        return concept;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public int getReadingCount() {
        return readingCount;
    }

    public String getInputDateValue() {
        return inputDateValue;
    }

    public void setInputDateValue(String inputDateValue) {
        this.inputDateValue = inputDateValue;
    }
}
