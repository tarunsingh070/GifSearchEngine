package tarun.example.com.gifsearchengine.ui.gifList;

import android.arch.paging.PagedListAdapter;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import tarun.example.com.gifsearchengine.R;
import tarun.example.com.gifsearchengine.data.model.giphy.AdapterGifItem;
import tarun.example.com.gifsearchengine.util.ProgressBarUtils;

/**
 * The Adapter class of type {@link PagedListAdapter} which provides pagination support while loading
 * and displaying the list of gifs to the user.
 */
public class GifsDataSourceListAdapter extends PagedListAdapter<AdapterGifItem, GifsDataSourceListAdapter.ViewHolder> {

    private static final String TAG = GifsDataSourceListAdapter.class.getSimpleName();

    private ItemClickListener itemClickListener;

    GifsDataSourceListAdapter(ItemClickListener itemClickListener) {
        super(AdapterGifItem.DIFF_CALLBACK);
        this.itemClickListener = itemClickListener;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gif, parent, false);
        return new ViewHolder(view);
    }

    // Calls the bind method of viewholder to bind the data to various views in each cell.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    /**
     * Viewholder class for RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivGif;

        ViewHolder(View itemView) {
            super(itemView);
            ivGif = itemView.findViewById(R.id.iv_gif);
        }

        /**
         * Bind the data from the gif object received into views.
         */
        void bind(AdapterGifItem gif) {
            loadGif(gif.getPreviewUrl());
            itemView.setOnClickListener(this);
        }

        /**
         * Loads the gif into the image view in the cell.
         * @param url Url from where the gif is to be loaded.
         */
        void loadGif(String url) {
            if (!TextUtils.isEmpty(url)) {
                CircularProgressDrawable progressPlaceHolder = ProgressBarUtils.getCircularProgressPlaceholder(ivGif.getContext());
                Glide.with(ivGif.getContext())
                        .asGif()
                        .load(url)
                        .apply(new RequestOptions()
                                .placeholder(progressPlaceHolder))
                        .into(ivGif);
            }
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getItem(getAdapterPosition()));
            }
        }
    }

    /**
     * {@link GifListFragment} will implement this interface in order to communicate when a gif item is clicked.
     */
    public interface ItemClickListener {
        void onItemClick(AdapterGifItem gif);
    }
}
