package application.oneshot;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.View;

import application.oneshot.adapters.OnboardingFragmentPagerAdapter;
import application.oneshot.constants.Bundles;
import application.oneshot.constants.Extras;
import application.oneshot.fragments.OnboardingFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnboardingActivity
        extends BaseActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private OnboardingFragmentPagerAdapter mOnboardingFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        ButterKnife.bind(this);

        mOnboardingFragmentPagerAdapter = new OnboardingFragmentPagerAdapter(getSupportFragmentManager());

        addOnboardingFragments();
        addKeyguardOnboardingFragment();

        viewPager.setAdapter(mOnboardingFragmentPagerAdapter);
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        final Intent intent = new Intent(this, AuthActivity.class);

        startActivity(intent);

        finish();
    }

    private void addKeyguardOnboardingFragment() {
        final KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        // Display a warning if a device PIN, pattern, password,
        // or biometric authentication has not been set.
        if (!keyguardManager.isDeviceSecure()) {
            mOnboardingFragmentPagerAdapter.add(OnboardingFragment.newInstance(setBundle(Bundles.OB_SL)));
        }
    }

    private void addOnboardingFragments() {
        mOnboardingFragmentPagerAdapter.add(OnboardingFragment.newInstance(setBundle(Bundles.OB_FOS)));
        mOnboardingFragmentPagerAdapter.add(OnboardingFragment.newInstance(setBundle(Bundles.OB_SECURE)));
        mOnboardingFragmentPagerAdapter.add(OnboardingFragment.newInstance(setBundle(Bundles.OB_ANONYMOUS)));
    }

    private Bundle setBundle(int position) {
        final Bundle bundle = new Bundle();

        final int drawable;
        final String title;
        final String subtitle;

        switch (position) {
            case Bundles.OB_FOS:
                drawable = R.drawable.ob_fos;
                title = getString(R.string.onboarding_title_fos);
                subtitle = getString(R.string.onboarding_fos);
                break;
            case Bundles.OB_SECURE:
                drawable = R.drawable.ob_secure;
                title = getString(R.string.onboarding_title_secure);
                subtitle = getString(R.string.onboarding_secure);
                break;
            case Bundles.OB_ANONYMOUS:
                drawable = R.drawable.ob_anonymous;
                title = getString(R.string.onboarding_title_anonymous);
                subtitle = getString(R.string.onboarding_anonymous);
                break;
            case Bundles.OB_SL:
                drawable = R.drawable.ob_sl;
                title = getString(R.string.onboarding_title_keyguard);
                subtitle = getString(R.string.onboarding_keyguard);
                break;
            default:
                drawable = 0;
                title = null;
                subtitle = null;
                break;
        }

        bundle.putInt(Extras.ARGUMENT_IMAGE_DRAWABLE, drawable);
        bundle.putString(Extras.ARGUMENT_TITLE, title);
        bundle.putString(Extras.ARGUMENT_SUBTITLE, subtitle);

        return bundle;
    }
}
