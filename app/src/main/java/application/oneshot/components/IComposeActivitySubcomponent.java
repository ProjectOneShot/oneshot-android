package application.oneshot.components;

import application.oneshot.ComposeActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface IComposeActivitySubcomponent
        extends AndroidInjector<ComposeActivity> {

    @Subcomponent.Builder
    abstract class Builder
            extends AndroidInjector.Builder<ComposeActivity> {
    }
}
