package com.nuvo.parsler.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class OpenShipmentAcceptDialogFragment extends DialogFragment {

    private static final String ACCEPT_MSG = "Are you sure you want to accept this shipment?";
    private static final String REJECT_MSG = "Are you sure you want to reject this shipment?";

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface OpenShipmentDialogListener {
        public void onDialogAcceptClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogRejectClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    OpenShipmentDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OpenShipmentDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OpenShipmentDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String action = args.getString("action");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.accept_dialog_title);
        if (action.equals("accept")){
            builder.setMessage(ACCEPT_MSG)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // Send the positive button event back to the host activity
                            mListener.onDialogAcceptClick(OpenShipmentAcceptDialogFragment.this);
                        }
                    });
        }
        else{
            builder.setMessage(REJECT_MSG)
                    .setPositiveButton(R.string.reject, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // Send the positive button event back to the host activity
                            mListener.onDialogRejectClick(OpenShipmentAcceptDialogFragment.this);
                        }
                    });
        }

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(OpenShipmentAcceptDialogFragment.this);
                    }
                });
        return builder.create();
    }
}