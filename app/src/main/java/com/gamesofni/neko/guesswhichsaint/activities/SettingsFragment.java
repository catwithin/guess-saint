package com.gamesofni.neko.guesswhichsaint.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.gamesofni.neko.guesswhichsaint.R;


public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_vizualizer);
        Preference resetDb = findPreference("settings_reset_db");
        resetDb.setOnPreferenceClickListener(
                preference -> {
                    DialogFragment resetConfirmationDialog = new ResetDbDialogFragment();
                    resetConfirmationDialog.show(getActivity().getFragmentManager(), TAG);
                    return true;
                }
        );
    }


}
