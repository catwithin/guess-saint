package com.gamesofni.neko.guesswhichsaint.activities;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.gamesofni.neko.guesswhichsaint.R;


public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_vizualizer);
    }

}
