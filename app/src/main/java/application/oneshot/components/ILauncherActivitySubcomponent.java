package application.oneshot.components;

import application.oneshot.LauncherActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface ILauncherActivitySubcomponent
        extends AndroidInjector<LauncherActivity> {

    @Subcomponent.Builder
    abstract class Builder
            extends AndroidInjector.Builder<LauncherActivity> {
    }
}
