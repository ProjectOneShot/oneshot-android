package application.oneshot.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import application.oneshot.AuthActivity;
import application.oneshot.ComposeActivity;
import application.oneshot.ContactActivity;
import application.oneshot.ContactsActivity;
import application.oneshot.LauncherActivity;
import application.oneshot.MailboxActivity;
import application.oneshot.OneShotApplication;
import application.oneshot.ReadActivity;
import application.oneshot.components.IAuthActivitySubcomponent;
import application.oneshot.components.IBaseActivitySubcomponent;
import application.oneshot.components.IComposeActivitySubcomponent;
import application.oneshot.components.IContactActivitySubcomponent;
import application.oneshot.components.IContactsDirectoryActivitySubcomponent;
import application.oneshot.components.ILauncherActivitySubcomponent;
import application.oneshot.components.IMailboxActivitySubcomponent;
import application.oneshot.components.IReadActivitySubcomponent;
import application.oneshot.components.ISettingsActivitySubcomponent;
import application.oneshot.constants.Preferences;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjector;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {IAuthActivitySubcomponent.class, IBaseActivitySubcomponent.class,
        IComposeActivitySubcomponent.class, IContactActivitySubcomponent.class,
        IContactsDirectoryActivitySubcomponent.class, ILauncherActivitySubcomponent.class,
        IMailboxActivitySubcomponent.class, IReadActivitySubcomponent.class, ISettingsActivitySubcomponent.class})
public abstract class ActivityModule {

    @Provides
    @Singleton
    static Context provideContext(OneShotApplication application) {
        return application;
    }

    @Provides
    @Singleton
    static DatabaseReference provideDatabaseReference() {
        return FirebaseDatabase.getInstance()
                .getReference();
    }

    @Provides
    @Singleton
    static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    static SharedPreferences providesSharedPreferences(OneShotApplication application) {
        return application.getSharedPreferences(Preferences.SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    @Binds
    @IntoMap
    @ClassKey(AuthActivity.class)
    abstract AndroidInjector.Factory<?> bindAuthActivityInjectorFactory(IAuthActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ClassKey(ComposeActivity.class)
    abstract AndroidInjector.Factory<?> bindComposeActivityInjectorFactory(
            IComposeActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ClassKey(ContactActivity.class)
    abstract AndroidInjector.Factory<?> bindContactActivityInjectorFactory(
            IContactActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ClassKey(ContactsActivity.class)
    abstract AndroidInjector.Factory<?> bindContactsActivityInjectorFactory(
            IContactsDirectoryActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ClassKey(LauncherActivity.class)
    abstract AndroidInjector.Factory<?> bindLauncherActivityInjectorFactory(
            ILauncherActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ClassKey(MailboxActivity.class)
    abstract AndroidInjector.Factory<?> bindMailboxActivityInjectorFactory(
            IMailboxActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ClassKey(ReadActivity.class)
    abstract AndroidInjector.Factory<?> bindReadActivityInjectorFactory(IReadActivitySubcomponent.Builder builder);
}
