package application.oneshot.components;

import application.oneshot.SettingsActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface ISettingsActivitySubcomponent
        extends AndroidInjector<SettingsActivity> {

    @Subcomponent.Builder
    abstract class Builder
            extends AndroidInjector.Builder<SettingsActivity> {
    }
}
