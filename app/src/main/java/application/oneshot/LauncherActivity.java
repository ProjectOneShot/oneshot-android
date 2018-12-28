package application.oneshot;

import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;

import javax.inject.Inject;

import application.oneshot.constants.NotificationChannels;
import application.oneshot.constants.Preferences;
import application.oneshot.helpers.SharedPreferencesHelper;
import dagger.android.AndroidInjection;

public class LauncherActivity
        extends BaseActivity {

    @Inject
    SharedPreferencesHelper mSharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        // Bootstrap
        createNotificationChannels();
        setNightMode();

        // Startup
        startConfirmDeviceCredentialIntent();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final CharSequence name = getString(R.string.notification_channel_new_message_name);

            final NotificationChannel channel = new NotificationChannel(NotificationChannels.NEW_MESSAGE, name,
                    NotificationManager.IMPORTANCE_DEFAULT);

            final NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setNightMode() {
        final String nightMode = mSharedPreferencesHelper.get(Preferences.USE_NIGHT_MODE,
                Preferences.USE_NIGHT_MODE_DEFAULT);

        AppCompatDelegate.setDefaultNightMode(Integer.parseInt(nightMode));
    }

    private void startConfirmDeviceCredentialIntent() {
        if (mSharedPreferencesHelper.get(Preferences.CONFIRM_DEVICE_CREDENTIALS,
                Preferences.CONFIRM_DEVICE_CREDENTIALS_DEFAULT)) {

            final KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

            final Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);

            if (intent != null) {
                startActivityForResult(intent, 1);
            } else {
                startDefaultIntent();
            }
        } else {
            startDefaultIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            startDefaultIntent();
        } else {
            finish();
        }
    }

    private void startDefaultIntent() {
        final Intent intent;

        final boolean showIntroduction = mSharedPreferencesHelper.get(Preferences.INTRODUCTION,
                Preferences.INTRODUCTION_DEFAULT);

        if (showIntroduction) {
            intent = new Intent(this, OnboardingActivity.class);
        } else {
            intent = new Intent(this, AuthActivity.class);
        }

        startActivity(intent);

        finish();
    }
}
