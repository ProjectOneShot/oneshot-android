package application.oneshot.components;

import application.oneshot.BaseActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface IBaseActivitySubcomponent
        extends AndroidInjector<BaseActivity> {

    @Subcomponent.Builder
    abstract class Builder
            extends AndroidInjector.Builder<BaseActivity> {
    }
}
