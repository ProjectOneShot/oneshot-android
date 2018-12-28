package application.oneshot.components;

import application.oneshot.MailboxActivity;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface IMailboxActivitySubcomponent
        extends AndroidInjector<MailboxActivity> {

    @Subcomponent.Builder
    abstract class Builder
            extends AndroidInjector.Builder<MailboxActivity> {
    }
}
