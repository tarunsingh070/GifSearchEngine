package tarun.example.com.gifsearchengine.ui.gifList;

import android.content.Context;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import tarun.example.com.gifsearchengine.R;
import tarun.example.com.gifsearchengine.data.model.AdapterGifItem;

public class GifsListAdapter extends RecyclerView.Adapter<GifsListAdapter.ViewHolder> {

    private List<AdapterGifItem> gifs;
    private Context context;
    private ItemClickListener itemClickListener;

    GifsListAdapter(Context context, ItemClickListener itemClickListener) {
        this.context = context;
        gifs = new ArrayList<>();
        this.itemClickListener = itemClickListener;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gif, parent, false);
        return new ViewHolder(view);
    }

    // Calls the bind method of viewholder to bind the data to various views in each cell.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(gifs.get(position));
    }

    // total number of cells.
    @Override
    public int getItemCount() {
        return gifs.size();
    }

    /**
     * Helper method to update the dataset of adapter.
     * @param gifs Latest list of Gifs.
     */
    public void setGifs(List<AdapterGifItem> gifs) {
        this.gifs = gifs;
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
                getProgressPlaceHolderView();
                Glide.with(context)
                        .asGif()
                        .load(url)
                        .apply(new RequestOptions()
                                .placeholder(getProgressPlaceHolderView()))
                        .into(ivGif);
            }
        }

        /**
         * A placeholder progress bar for each of the cell until the gif loads.
         * @return
         */
        private CircularProgressDrawable getProgressPlaceHolderView() {
            CircularProgressDrawable progressPlaceHolder = new CircularProgressDrawable(context);
            progressPlaceHolder.setStrokeWidth(5f);
            progressPlaceHolder.setCenterRadius(30f);
            progressPlaceHolder.start();
            return progressPlaceHolder;
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(gifs.get(getAdapterPosition()));
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
