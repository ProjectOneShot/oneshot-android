package application.oneshot.components;

import application.oneshot.ContactsActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface IContactsDirectoryActivitySubcomponent
        extends AndroidInjector<ContactsActivity> {

    @Subcomponent.Builder
    abstract class Builder
            extends AndroidInjector.Builder<ContactsActivity> {
    }
}
