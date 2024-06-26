/*
 * Copyright (c) The Trustees of Indiana University, Moi University
 * and Vanderbilt University Medical Center. All Rights Reserved.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license
 * with additional health care disclaimer.
 * If the user is an entity intending to commercialize any application that uses
 * this code in a for-profit venture, please contact the copyright holder.
 */

package com.muzima.model.shr.kenyaemr;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.muzima.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Immunization {

    @JsonProperty("NAME")
    private String name;
    @JsonProperty("DATE_ADMINISTERED")
    private String dateAdministered;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Immunization() {
    }

    @JsonProperty("NAME")
    public String getName() {
        return name;
    }

    @JsonProperty("NAME")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("DATE_ADMINISTERED")
    public String getDateAdministered() {
        return dateAdministered;
    }

    @JsonProperty("DATE_ADMINISTERED")
    public void setDateAdministered(String dateAdministered) {
        this.dateAdministered = dateAdministered;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public boolean lacksMandatoryValues(){
        return StringUtils.isEmpty(name) || StringUtils.isEmpty(dateAdministered);
    }

    public boolean equals(Object o){
        if(o instanceof Immunization) {
            Immunization immunization = (Immunization) o;
            return StringUtils.equalsIgnoreCase(this.getDateAdministered(), immunization.getDateAdministered())
                    && StringUtils.equalsIgnoreCase(this.getName(), immunization.getName());
        }
        return false;
    }
}
