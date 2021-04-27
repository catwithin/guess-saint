package com.gamesofni.neko.guesswhichsaint.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.gamesofni.neko.guesswhichsaint.R;


public class ResetDbDialogFragment extends DialogFragment {

    ResetDbDialogListener dialogListener;

    public interface ResetDbDialogListener {
        void onDialogPositiveClick(ResetDbDialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dialogListener = (ResetDbDialogListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_reset_db_counters_title);
        builder.setMessage(R.string.dialog_reset_db_counters_message);

        builder.setPositiveButton(R.string.dialog_reset_db_confirm, (dialog, id) -> {
            // Send the positive button event back to the host activity
            dialogListener.onDialogPositiveClick(ResetDbDialogFragment.this);
        });

        // only here for the "Cancel" button to b displayed
        builder.setNegativeButton(R.string.dialog_reset_db_cancel, (dialog, id) -> {});

        return builder.create();
    }
}