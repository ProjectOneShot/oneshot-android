package application.oneshot.components;

import application.oneshot.ReadActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface IReadActivitySubcomponent
        extends AndroidInjector<ReadActivity> {

    @Subcomponent.Builder
    abstract class Builder
            extends AndroidInjector.Builder<ReadActivity> {
    }
}
