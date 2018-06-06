package tarun.example.com.gifsearchengine.ui.gifDetails;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import tarun.example.com.gifsearchengine.R;
import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;
import tarun.example.com.gifsearchengine.util.ProgressBarUtils;
import tarun.example.com.gifsearchengine.ui.gifList.GifListFragment;

/**
 * This fragment defines the UI to show the details of the Gif selected from the {@link GifListFragment} page.
 */
public class GifDetailsFragment extends Fragment implements GifDetailsContract.View {

    public static final String TAG = GifDetailsFragment.class.getSimpleName();

    private static final String ARG_GIF = "gif";

    private AdapterGifItem gif;

    private ImageView ivGif;
    private TextView tvTitle;
    private TextView tvRating;
    private TextView tvUploader;
    private TextView tvUploadDate;
    private TextView tvHeight;
    private TextView tvWidth;
    private TextView tvSize;
    private Button rateMeButton;

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
    public void setActivityTitle(String title) {
        getActivity().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_gif_details, container, false);
        ivGif = rootView.findViewById(R.id.iv_gif);
        tvUploader = rootView.findViewById(R.id.tv_uploader);
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvRating = rootView.findViewById(R.id.tv_rating);
        tvUploadDate = rootView.findViewById(R.id.tv_upload_date);
        tvHeight = rootView.findViewById(R.id.tv_height);
        tvWidth = rootView.findViewById(R.id.tv_width);
        tvSize = rootView.findViewById(R.id.tv_size);
        rateMeButton = rootView.findViewById(R.id.button_rate);
        rateMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.takeView(this);
    }

    /**
     * Create a custom rating dialog to allow user to rate the Gif.
     */
    private void showRatingDialog() {
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
        CircularProgressDrawable progressPlaceHolder = ProgressBarUtils.getCircularProgressPlaceholder(getContext());
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
    public void populateGifDetails(String averageRating) {
        tvTitle.setText(gif.getTitle());
        tvRating.setText(averageRating);
        tvUploader.setText(gif.getUserName());
        tvUploadDate.setText(gif.getImportDate());
        tvHeight.setText(gif.getFullGif().getHeight());
        tvWidth.setText(gif.getFullGif().getWidth());
        tvSize.setText(gif.getFullGif().getSize());
    }

    @Override
    public void showInvalidRatingErrorMessage() {
        Toast.makeText(getContext(), R.string.invalid_rating_error_message, Toast.LENGTH_SHORT).show();
    }

}
