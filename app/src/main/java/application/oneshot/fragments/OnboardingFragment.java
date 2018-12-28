package application.oneshot.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import application.oneshot.R;
import application.oneshot.constants.Extras;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OnboardingFragment
        extends Fragment {

    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.text_view_title)
    TextView textViewTitle;
    @BindView(R.id.text_view_subtitle)
    TextView textViewSubtitle;

    private Unbinder mUnbinder;

    public static OnboardingFragment newInstance(Bundle bundle) {
        final OnboardingFragment fragment = new OnboardingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        final Drawable drawable = ContextCompat.getDrawable(getActivity().getApplicationContext(),
                getArguments().getInt(Extras.ARGUMENT_IMAGE_DRAWABLE));

        imageView.setImageDrawable(drawable);
        textViewTitle.setText(getArguments().getString(Extras.ARGUMENT_TITLE));
        textViewSubtitle.setText(getArguments().getString(Extras.ARGUMENT_SUBTITLE));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
    }
}
