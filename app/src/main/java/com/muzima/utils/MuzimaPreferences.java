/*
 * Copyright (c) The Trustees of Indiana University, Moi University
 * and Vanderbilt University Medical Center. All Rights Reserved.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license
 * with additional health care disclaimer.
 * If the user is an entity intending to commercialize any application that uses
 * this code in a for-profit venture, please contact the copyright holder.
 */

package com.muzima.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.muzima.R;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Support system preferences including prefs not
 * displayed in the settings UI
 */
public class MuzimaPreferences {

    private static final String ON_BOARDING_COMPLETED_PREFERENCE = "onboarding_completed_pref";

    public static void setOnBoardingCompletedPreference(Context context, boolean isLightMode) {
        setBooleanPreference(context, ON_BOARDING_COMPLETED_PREFERENCE, isLightMode);
    }

    public static boolean getOnBoardingCompletedPreference(Context context) {
        return getBooleanPreference(context, ON_BOARDING_COMPLETED_PREFERENCE, false);
    }

    public static boolean getIsLightModeThemeSelectedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.preference_light_mode), false);
    }

    public static void setBooleanPreference(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static void setStringPreference(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static String getStringPreference(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }

    private static int getIntegerPreference(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
    }

    private static void setIntegerPrefrence(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
    }

    private static boolean setIntegerPrefrenceBlocking(Context context, String key, int value) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).commit();
    }

    private static long getLongPreference(Context context, String key, long defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defaultValue);
    }

    private static void setLongPreference(Context context, String key, long value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, value).apply();
    }

    private static void removePreference(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply();
    }

    private static Set<String> getStringSetPreference(Context context, String key, Set<String> defaultValues) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(key)) {
            return prefs.getStringSet(key, Collections.<String>emptySet());
        } else {
            return defaultValues;
        }
    }

    private static void createSetPreference(Context context, String key, Set<String> values) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        Set<String> set = new HashSet<>(values);
        editor.putStringSet(key, set);
        editor.apply();
    }

   /* public static void setAppLocalePreference(Context context, String localeDescription) {
        setStringPreference(context, APP_LOCALE_PREFERENCE, localeDescription);
    }

    public static String getAppLocalePreference(Context context) {
        return getStringPreference(context, APP_LOCALE_PREFERENCE, context.getString(R.string.language_portuguese));
    }*/
}
