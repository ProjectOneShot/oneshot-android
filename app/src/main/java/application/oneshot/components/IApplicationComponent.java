package application.oneshot.components;

import javax.inject.Singleton;

import application.oneshot.OneShotApplication;
import application.oneshot.modules.ActivityModule;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, ActivityModule.class})
public interface IApplicationComponent {

    void inject(OneShotApplication application);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(OneShotApplication application);

        IApplicationComponent build();
    }
}
