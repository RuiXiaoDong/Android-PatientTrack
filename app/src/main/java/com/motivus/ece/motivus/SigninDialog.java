package com.motivus.ece.motivus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by dongx on 29/03/2015.
 */
public class SigninDialog extends DialogFragment {
    public interface SigninDialogListener {
        public void onDialogPositiveClick(SigninDialog dialog);
        public void onDialogNegativeClick(SigninDialog dialog);
    }

    public EditText mEdit_username;
    public EditText mEdit_password;
    public SigninDialogListener mListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SigninDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_signin, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(checkUserAuthentication(mEdit_username.getText().toString(), mEdit_password.getText().toString()))
                            mListener.onDialogPositiveClick(SigninDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //SigninDialog.this.getDialog().cancel();
                        mListener.onDialogNegativeClick(SigninDialog.this);
                    }
                });

        mEdit_username = (EditText)view.findViewById(R.id.username);
        mEdit_password = (EditText)view.findViewById(R.id.password);

        return builder.create();
    }

    public boolean checkUserAuthentication(String username, String password) {
        if(username.compareToIgnoreCase("admin") == 0 && password.compareToIgnoreCase("admin") == 0)
            return true;
        else
            return false;
    }
}
