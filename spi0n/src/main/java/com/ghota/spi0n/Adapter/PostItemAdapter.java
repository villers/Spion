/**
 * PostItemAdapter.java
 * 
 * Adapter Class which configs and returns the View for ListView
 * 
 */
package com.ghota.spi0n.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ghota.spi0n.R;
import com.ghota.spi0n.model.PostData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

public class PostItemAdapter extends ArrayAdapter<PostData> {
    private final DisplayImageOptions options;
    private final ImageLoader imageLoader;
    private LayoutInflater inflater;
	private ArrayList<PostData> datas;

	public PostItemAdapter(Context context, int textViewResourceId, ArrayList<PostData> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		inflater = ((Activity) context).getLayoutInflater();
		datas = objects;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();

        imageLoader = ImageLoader.getInstance();
	}

    private static class ViewHolder {
		TextView postTitleView;
		TextView postCategoryView;
        TextView postCommentView;
		ImageView postThumbView;
        ProgressBar progressBar;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

		if (convertView == null) {
            view  = inflater.inflate(R.layout.postitem, null);

            holder = new ViewHolder();
            holder.postThumbView = (ImageView) view.findViewById(R.id.postThumb);
            holder.postTitleView = (TextView) view.findViewById(R.id.postTitleLabel);
            holder.postCategoryView = (TextView) view.findViewById(R.id.postCategoryLabel);
            holder.postCommentView = (TextView) view.findViewById(R.id.postCommentLabel);
            holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            view.setTag(holder);
		} else {
            holder = (ViewHolder) view.getTag();
		}

        holder.postTitleView.setText(datas.get(position).postTitle);
        holder.postCategoryView.setText(datas.get(position).postCategory);
        holder.postCommentView.setText(datas.get(position).postComment);

        imageLoader.displayImage(datas.get(position).postThumbUrl, holder.postThumbView, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.progressBar.setProgress(0);
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current,
                                                 int total) {
                        holder.progressBar.setProgress(Math.round(100.0f * current / total));
                    }
                });

		return view;
	}

}
