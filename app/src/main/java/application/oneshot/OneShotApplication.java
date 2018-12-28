package application.oneshot;

import android.app.Activity;
import android.app.Application;

import javax.inject.Inject;

import application.oneshot.components.DaggerIApplicationComponent;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class OneShotApplication
        extends Application
        implements HasActivityInjector {

//    static {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//    }

    @Inject
    DispatchingAndroidInjector<Activity> mDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerIApplicationComponent.builder()
                .application(this)
                .build()
                .inject(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return mDispatchingAndroidInjector;
    }
}
