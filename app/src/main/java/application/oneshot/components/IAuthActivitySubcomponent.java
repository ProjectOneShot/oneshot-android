package application.oneshot.components;

import application.oneshot.AuthActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface IAuthActivitySubcomponent
        extends AndroidInjector<AuthActivity> {

    @Subcomponent.Builder
    abstract class Builder
            extends AndroidInjector.Builder<AuthActivity> {
    }
}
