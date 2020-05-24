package com.example.joinchat.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.joinchat.R;
import com.example.joinchat.activities.MainActivity;

public class PermissionsDialogFragment extends DialogFragment {

    private static final String TAG = "PermissionsDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.permissions_dialog_title);
        builder.setMessage(R.string.no_permissions_granted)
                .setPositiveButton(R.string.accept_permissions_dialog, (dialog, id) -> ((MainActivity) getActivity()).askForPermissions())
                .setNegativeButton(R.string.cancel_dialog, (dialog, id) -> Log.i(TAG, "User cancelled Permissions Dialog"));
        return builder.create();
    }
}
