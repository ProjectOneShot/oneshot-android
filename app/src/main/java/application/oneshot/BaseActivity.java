package application.oneshot;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity
        extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    protected void dismissProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showProgressDialog(int messageResourceId) {
        mProgressDialog = new ProgressDialog(this, R.style.OneShotTheme_DialogOverlay);
        mProgressDialog.setMessage(getResources().getString(messageResourceId));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }
}
