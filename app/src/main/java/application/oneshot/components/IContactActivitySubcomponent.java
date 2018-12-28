package application.oneshot.components;

import application.oneshot.ContactActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface IContactActivitySubcomponent
        extends AndroidInjector<ContactActivity> {

    @Subcomponent.Builder
    abstract class Builder
            extends AndroidInjector.Builder<ContactActivity> {
    }
}
