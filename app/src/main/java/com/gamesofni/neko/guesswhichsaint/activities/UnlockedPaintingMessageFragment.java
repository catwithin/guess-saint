package com.gamesofni.neko.guesswhichsaint.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.gamesofni.neko.guesswhichsaint.R;

public class UnlockedPaintingMessageFragment extends DialogFragment {

    private TextView name;
    private ImageView pictureView;

    private long paintingId;

    public UnlockedPaintingMessageFragment() {}; //required, not to be used

    public static UnlockedPaintingMessageFragment newInstance(String paintingName,
                                                              Integer resourceId,
                                                              long id) {
        UnlockedPaintingMessageFragment f = new UnlockedPaintingMessageFragment();
        Bundle args = new Bundle();
        args.putString("painting", paintingName);
        args.putInt("resourceId", resourceId);
        args.putLong("id", id);
        f.setArguments(args);
        return f;
    }

    public static final String TAG = UnlockedPaintingMessageFragment.class.getSimpleName();

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout to use as dialog or embedded fragment
//        return inflater.inflate(R.layout.list_item_drawing, container);
//    }


//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        name = view.findViewById(R.id.list_drawing_name);
//
//        String painting = getArguments().getString("painting", "P Name");
//        name.setText(painting);
//        getDialog().setTitle(R.string.dialog_unlocked_painting);
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.setTitle(R.string.dialog_unlocked_painting);
//        return dialog;

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();


        View view = inflater.inflate(R.layout.dialog_unlocked, null);

        name = view.findViewById(R.id.list_drawing_name);
        String painting = getArguments().getString("painting", "P Name");
        name.setText(painting);

        pictureView = view.findViewById(R.id.drawing_preview);
        Integer paintingresourceId = getArguments().getInt("resourceId", -1);
        pictureView.setImageResource(paintingresourceId);

        long paintingId = getArguments().getLong("id", 0);

        builder
            .setView(view)
            .setPositiveButton(R.string.dialog_show, (dialog, id) -> {
                ((GuessSaint)getActivity()).showNowClick(paintingId);
            })
            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
