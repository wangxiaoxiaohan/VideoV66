package com.softwinner.TvdVideo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.softwinner.TvdVideo.R;
import com.softwinner.TvdVideo.model.FileVideo;
import com.softwinner.TvdVideo.model.FileVideoItem;
import com.softwinner.TvdVideo.utils.AsynImageLoader;
import com.softwinner.TvdVideo.utils.AsyncImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VideoAdapter extends BaseAdapter {
	private Context context;
	private List<FileVideo> list = new ArrayList<FileVideo>();
	private List<FileVideoItem> mVideoLists = new ArrayList<FileVideoItem>();
	private int prositionSelector = -1;
	private AsynImageLoader imageLoader;
	private AsyncImageLoader asyncImageLoader;
	public boolean isFull;

	public VideoAdapter(Context context) {
		this.context = context;
		this.imageLoader = new AsynImageLoader();
		this.asyncImageLoader = new AsyncImageLoader(context);
	}

	public void setData(List<FileVideo> mList) {
		this.mVideoLists.clear();
		this.list.clear();
		this.list = mList;
		notifyDataSetChanged();
	}

	public void setFileData(List<FileVideoItem> mList) {
		this.mVideoLists.clear();
		this.list.clear();
		this.mVideoLists = mList;
		this.prositionSelector = 0;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (list.size() != 0) {
			return list.size();
		} else {
			return mVideoLists.size();
		}
	}

	public void isFullSuccer(boolean isFull) {
		this.isFull = isFull;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		if (list != null) {
			return list.get(position);
		} else {
			return mVideoLists.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setPositionSelector(int position) {
		this.prositionSelector = position;
		notifyDataSetChanged();
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
			holder = new ViewHolder();
			holder.mThumbnail = (ImageView) convertView.findViewById(R.id.iv_video);
			holder.mTitle = (TextView) convertView.findViewById(R.id.tv_title);
			holder.mTotaltime = (TextView) convertView.findViewById(R.id.tv_size);
			holder.mMain = (LinearLayout) convertView.findViewById(R.id.main);
			holder.imagePlay = (ImageView) convertView.findViewById(R.id.image_play);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (isFull) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,
					LinearLayout.LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.CENTER | Gravity.LEFT;
			holder.mTotaltime.setLayoutParams(params);
		} else {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.CENTER | Gravity.LEFT;
			holder.mTotaltime.setLayoutParams(params);
		}

		holder.mThumbnail.setImageResource(R.drawable.ic_loading);
		if (list.size() != 0) {
			// 预设一个图片
			FileVideo video = list.get(position);
			holder.mThumbnail.setTag(list.get(position).getUrl());
			if (!TextUtils.isEmpty(video.getUrl())) {
				Bitmap bitmap = asyncImageLoader.loadImage(holder.mThumbnail, video.getUrl());
				if (bitmap != null) {
					holder.mThumbnail.setImageBitmap(bitmap);
				}
			}
			holder.mTitle.setText(video.getmTilte());
			holder.mTotaltime.setText(video.getmTotalTime());
		} else {
			FileVideoItem videoItems = mVideoLists.get(position);
			if (videoItems != null) {
				imageLoader.showImageAsyn(holder.mThumbnail, videoItems.getmPath(), R.drawable.ic_loading);
				holder.mTitle.setText(videoItems.getmTitle());
				if (videoItems.getmTotalTime() != null) {
					holder.mTotaltime.setText(videoItems.getmTotalTime());
				} else {
					holder.mTotaltime.setVisibility(View.GONE);
				}
			}
		}

		if (prositionSelector == position) {
			holder.mMain.setBackgroundResource(R.color.blue);
			holder.imagePlay.setVisibility(View.GONE);
			holder.mTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
			holder.mTitle.setSingleLine(true);
			holder.mTitle.setMarqueeRepeatLimit(10);
		} else {
			holder.mMain.setBackgroundResource(R.color.gray);
			holder.mTitle.setSingleLine(true);
			holder.imagePlay.setVisibility(View.VISIBLE);
			holder.mTitle.setEllipsize(TextUtils.TruncateAt.valueOf("MIDDLE"));
		}

		// if (isWidth) {
		// //满屏时
		// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
		// LinearLayout.LayoutParams.MATCH_PARENT,0.2f);
		// lp.setMargins(0, 0, 70, 0);
		// holder.mTitle.setLayoutParams(lp);
		// }else{
		// //半屏和全屏时
		// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
		// LinearLayout.LayoutParams.MATCH_PARENT,0.2f);
		// lp.setMargins(0, 0, 10, 0);
		// holder.mTitle.setLayoutParams(lp);
		// }
		return convertView;
	}

	static class ViewHolder {
		ImageView mThumbnail;
		TextView mTitle;
		TextView mTotaltime;
		LinearLayout mMain;
		ImageView imagePlay;
	}

}
