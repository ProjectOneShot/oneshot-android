package application.oneshot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import application.oneshot.R;
import application.oneshot.constants.Extras;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AlertDialogFragment
        extends DialogFragment {

    @BindView(R.id.text_view)
    TextView textView;

    private AlertDialogListener mAlertDialogListener;

    private Unbinder mUnbinder;

    public static AlertDialogFragment newInstance(@NonNull String message) {
        final Bundle bundle = new Bundle();
        bundle.putString(Extras.ALERT_DIALOG_FRAGMENT_MESSAGE, message);

        return newInstance(bundle);
    }

    public static AlertDialogFragment newInstance(@NonNull String message, int callback) {
        final Bundle bundle = new Bundle();
        bundle.putString(Extras.ALERT_DIALOG_FRAGMENT_MESSAGE, message);
        bundle.putInt(Extras.ALERT_DIALOG_FRAGMENT_CALLBACK, callback);

        return newInstance(bundle);
    }

    private static AlertDialogFragment newInstance(@NonNull Bundle bundle) {
        final AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mAlertDialogListener = (AlertDialogListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString(Extras.ALERT_DIALOG_FRAGMENT_MESSAGE);
        final int callback = getArguments().getInt(Extras.ALERT_DIALOG_FRAGMENT_CALLBACK);

        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        final View view = layoutInflater.inflate(R.layout.alert_dialog, null);

        mUnbinder = ButterKnife.bind(this, view);

        textView.setText(title);

        return new AlertDialog.Builder(getActivity(), R.style.OneShotTheme_DialogOverlay).setPositiveButton(
                R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialogListener.onDialogPositiveClick(callback);

                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialogListener.onDialogNegativeClick(callback);

                        dialog.cancel();
                    }
                })
                .setView(view)
                .create();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mAlertDialogListener = null;

        mUnbinder.unbind();
    }

    public interface AlertDialogListener {
        void onDialogNegativeClick(int callback);

        void onDialogPositiveClick(int callback);
    }
}
