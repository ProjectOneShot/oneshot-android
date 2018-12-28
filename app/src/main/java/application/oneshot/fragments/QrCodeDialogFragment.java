package application.oneshot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import application.oneshot.R;
import application.oneshot.constants.Extras;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class QrCodeDialogFragment
        extends DialogFragment {

    @BindView(R.id.image_view)
    ImageView imageView;

    private Unbinder mUnbinder;

    public static QrCodeDialogFragment newInstance(@NonNull Bitmap bitmap) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        final byte[] byteArray = byteArrayOutputStream.toByteArray();

        final Bundle bundle = new Bundle();
        bundle.putByteArray(Extras.QR_CODE_DIALOG_FRAGMENT_QR_CODE, byteArray);

        final QrCodeDialogFragment fragment = new QrCodeDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final byte[] qrCode = getArguments().getByteArray(Extras.QR_CODE_DIALOG_FRAGMENT_QR_CODE);

        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        final View view = layoutInflater.inflate(R.layout.qr_code_alert_dialog, null);

        mUnbinder = ButterKnife.bind(this, view);

        final Bitmap bitmap = BitmapFactory.decodeByteArray(qrCode, 0, qrCode.length);
        imageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity(), R.style.OneShotTheme_DialogOverlay_QrCode).setView(view)
                .create();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mUnbinder.unbind();
    }
}
