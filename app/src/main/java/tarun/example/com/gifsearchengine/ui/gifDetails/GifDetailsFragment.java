package tarun.example.com.gifsearchengine.ui.gifDetails;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tarun.example.com.gifsearchengine.R;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.util.ProgressBarUtil;
import tarun.example.com.gifsearchengine.ui.gifList.GifListFragment;

/**
 * This fragment defines the UI to show the details of the Gif selected from the {@link GifListFragment} page.
 */
public class GifDetailsFragment extends Fragment implements GifDetailsContract.View {

    public static final String TAG = GifDetailsFragment.class.getSimpleName();

    private static final String ARG_GIF = "gif";

    private AdapterGifItem gif;

    private ImageView ivGif;
    private RatingBar averageRatingBar;
    private TextView tvRatingCount;
    private TextView tvTitle;
    private TextView tvUploader;
    private TextView tvUploadDate;
    private TextView tvDimension;
    private TextView tvSize;

    // Flag to keep track of wheather the gif is currently expanded or not.
    private boolean isGifExpanded;

    private GifDetailsContract.Presenter presenter;

    public GifDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * GifDetailsFragment fragment using the provided parameters.
     *
     * @return A new instance of fragment GifDetailsFragment.
     */
    public static GifDetailsFragment newInstance(AdapterGifItem gif) {
        GifDetailsFragment fragment = new GifDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_GIF, gif);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gif = getArguments().getParcelable(ARG_GIF);
        }

        presenter = new GifDetailsPresenter(gif);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResumeCalled();
    }

    @Override
    public void setDefaultActivityTitle() {
        // Set App name as title.
        getActivity().setTitle(R.string.app_name);
    }

    @Override
    public void setActivityTitle(String title) {
        getActivity().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gif_details, container, false);
        final LinearLayout detailLayout = rootView.findViewById(R.id.detail_layout);
        ivGif = rootView.findViewById(R.id.iv_gif);
        final int originalGifViewerHeight = ivGif.getLayoutParams().height;
        ivGif.setOnClickListener(getGifClickedAnimationListener(originalGifViewerHeight, detailLayout));
        // Show an info toast to the user informing about the zoom in/zoom out feature.
        Toast.makeText(getContext(), R.string.gif_zoom_in_message, Toast.LENGTH_LONG).show();

        averageRatingBar = rootView.findViewById(R.id.average_rating_bar);
        tvRatingCount = rootView.findViewById(R.id.tv_rating_count);
        tvUploader = rootView.findViewById(R.id.tv_uploader);
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvUploadDate = rootView.findViewById(R.id.tv_upload_date);
        tvDimension = rootView.findViewById(R.id.tv_dimension);
        tvSize = rootView.findViewById(R.id.tv_size);
        final FloatingActionButton fabRateMeButton = rootView.findViewById(R.id.fab_rate);
        fabRateMeButton.setOnClickListener(getRateButtonClickedListener());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.takeView(this);
    }

    /**
     * Returns a click listener for zoom in and zoom out animations on gif being loaded.
     */
    private View.OnClickListener getGifClickedAnimationListener(final int originalGifViewerHeight, final ViewGroup layout) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zoom in and out the gif when user clicks on it.
                TransitionManager.beginDelayedTransition(layout, new TransitionSet()
                        .addTransition(new ChangeBounds())
                        .addTransition(new ChangeImageTransform()));

                ViewGroup.LayoutParams params = ivGif.getLayoutParams();
                params.height = isGifExpanded ? originalGifViewerHeight :
                        ViewGroup.LayoutParams.MATCH_PARENT;
                ivGif.setLayoutParams(params);

                ivGif.setScaleType(isGifExpanded ? ImageView.ScaleType.FIT_XY :
                        ImageView.ScaleType.CENTER_CROP);
                isGifExpanded = !isGifExpanded;
            }
        };
    }

    /**
     * Returns a click listener for rating button.
     */
    private View.OnClickListener getRateButtonClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform rotating animation when clicked.
                final Interpolator interpolator = new DecelerateInterpolator();
                ViewCompat.animate(v).
                        rotation(180f).
                        withLayer().
                        setDuration(1000).
                        setInterpolator(interpolator).
                        start();

                presenter.ratingButtonClicked();
            }
        };
    }

    /**
     * Create a custom rating dialog to allow user to rate the Gif.
     */
    @Override
    public void showRatingDialog() {
        showRatingDialog(0);
    }

    /**
     * Create a custom rating dialog to allow user to rate the Gif and pre-set the rating bar as per existing rating..
     */
    @Override
    public void showRatingDialog(int existingRating) {
        // Inflate view for Ratings Dialog.
        LayoutInflater li = LayoutInflater.from(getContext());
        View rateMeDialogView = li.inflate(R.layout.dialog_rate_me, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set our custom inflated view to alert dialog builder.
        alertDialogBuilder.setView(rateMeDialogView);

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Initialize views.
        final RatingBar ratingBar = rateMeDialogView.findViewById(R.id.rating_bar);
        Button okButton = rateMeDialogView.findViewById(R.id.button_submit);
        Button cancelButton = rateMeDialogView.findViewById(R.id.button_cancel);

        // Pre-set rating in rating bar.
        if (existingRating > 0) {
            ratingBar.setRating(existingRating);
        }

        // Bind views.
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.rateGif(gif, (int)ratingBar.getRating());
                dismissDialog(alertDialog);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog(alertDialog);
            }
        });

        alertDialog.show();
    }

    /**
     * A helper method to dismiss dialog.
     * @param alertDialog AlertDialog instance to dismiss.
     */
    private void dismissDialog(AlertDialog alertDialog) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    /**
     * Load the gif at the url provided using Glide library.
     * @param url
     */
    @Override
    public void loadGif(String url) {
        CircularProgressDrawable progressPlaceHolder = ProgressBarUtil.getCircularProgressPlaceholder(getContext());
        if (!TextUtils.isEmpty(url)) {
            Glide.with(getContext())
                    .asGif()
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(progressPlaceHolder))
                    .into(ivGif);
        }
    }

    /**
     * Populate all views with the details of the Gif opened.
     */
    @Override
    public void populateGifDetails() {
        // Set the rating with animation
        ObjectAnimator anim = ObjectAnimator.ofFloat(averageRatingBar, "rating", gif.getAverageRating());
        anim.setDuration(2000);
        anim.start();

        tvRatingCount.setText(String.valueOf(gif.getRatingCount()));
        tvTitle.setText(gif.getTitle());
        tvUploader.setText(gif.getUserName());
        tvUploadDate.setText(getFormattedDate(gif.getImportDate()));
        tvDimension.setText(getString(R.string.formatted_dimensions, gif.getFullGif().getHeight()
                , gif.getFullGif().getWidth()));
        tvSize.setText(getString(R.string.formatted_size_with_unit_kb, gif.getFullGif().getSize()));
    }

    /**
     * Convert the import date string received into a human readable format (eg. Jan 07, 2018).
     * @param date The import date string received.
     * @return The new formatted import date.
     */
    private String getFormattedDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.received_import_date_format));
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat(getString(R.string.desired_import_date_format));
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(date);
            return desiredDateFormat.format(parsedDate);
        } catch (ParseException e) {
            // Return an empty string in case of issues parsing the date string received.
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void showInvalidRatingErrorMessage() {
        Toast.makeText(getContext(), R.string.invalid_rating_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getApplicationContext() {
        return getContext().getApplicationContext();
    }
}
