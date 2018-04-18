package com.softwinner.TvdVideo.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.softwinner.TvdVideo.config.AppConfig;
import com.softwinner.TvdVideo.model.FileVideoItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.MediaStore.Video;

/**
 * 作者：lixiang on 2015/12/16 10:55 
 * 邮箱：xiang.li@spreadwin.com
 */
@SuppressLint("NewApi")
public class VideoUtils {
	//
	public static final String LEFT_STATUS_ID = "LEFT_STATUS_ID";
	// 右边屏状态值
	public static final String RIGHT_STATUS_ID = "RIGHT_STATUS_ID";
	// 全屏
	public static final int MW_MAX_STACK_WINDOW = 1;
	// 隐藏
	public static final int MW_MIN_STACK_WINDOW = 2;
	// 分屏
	public static final int MW_NORMAL_STACK_WINDOW = 0;
	// 无效
	public static final int MW_INVALID_STACK_WINDOW = -1;

	/**
	 * 获取视频第一帧图片
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap createVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		// FFmpegMediaMetadataRetriever FFmpeg = new
		// FFmpegMediaMetadataRetriever();
		MediaMetadataRetriever media = new MediaMetadataRetriever();
		try {
			media.setDataSource(filePath);
			bitmap = media.getFrameAtTime(600000,
					MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (RuntimeException ex) {
		} finally {
			try {
				media.release();
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			}
		}
		return ImageCompressUtil.compressBySize(bitmap, 100, 100);
	}

	/**
	 * @TODO 获取某一帧的视频图片
	 * @param filePath
	 * @param currentPosition
	 * @return
	 */
	public static Bitmap setVideoImage(String filePath, int currentPosition) {
		Bitmap bitmap = null;
		MediaMetadataRetriever media = new MediaMetadataRetriever();
		try {
			media.setDataSource(filePath);
			bitmap = media.getFrameAtTime(currentPosition * 1000,
					MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (RuntimeException ex) {
		} finally {
			try {
				media.release();
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			}
		}
		return bitmap;
	}

	private static Bitmap compressImage(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > 100) {
			// 重置baos即清空baos
			baos.reset();
			// 这里压缩options%，把压缩后的数据存放到baos中
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
			// 每次都减少10
			options -= 10;
		}
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		// 把ByteArrayInputStream数据生成图片
		Bitmap mBitmap = BitmapFactory.decodeStream(isBm, null, null);
		return mBitmap;
	}

	public static Bitmap getBitmap(String myJpgPath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
		return bm;
	}

//	// 将毫秒转化为时间
//	public static String getTime(int time) {
//		Date date = new Date();// 获取当前时间
//		SimpleDateFormat hms = new SimpleDateFormat("mm:ss");
//		date.setTime(-8 * 60 * 60 * 1000 + time);
//		String data = hms.format(date);
//		return data;
//	}
	public static String getTime(long duration) {  
		long min = (duration / 1000) / 60;  
		long sec = (duration / 1000) % 60;  
		return getType(min) + ":" + getType(sec);  
		}  
	public static String getType(long time) {  
		return time<10 ? "0" + time : String.valueOf(time);  
		}  
	public static FileVideoItem Uri2File2Uri(Uri videoUri, Context mContext,
			String realPath) {
		FileVideoItem item = new FileVideoItem();
		String scheme = videoUri.getScheme();
		String mPathName = null;
		String mTotalTime = null;
		String mTitle = null;
		if (scheme == null) {
			return item;

		}
		if (scheme.equals("content")) {
			// String path = null;
			Cursor c = null;
			// IContentProvider mMediaProvider =
			// mContext.getContentResolver().acquireProvider("media");
			IContentProvider mMediaProvider = (IContentProvider) mContext
					.getContentResolver().acquireContentProviderClient("media");
			String[] VIDEO_PROJECTION = new String[] { Video.Media.DATA,
					Video.Media.TITLE, Video.Media.DURATION };
			/* get video file */
			try {
				c = mMediaProvider.query(null, videoUri, VIDEO_PROJECTION,
						null, null, null, null);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (c != null) {
				try {
					while (c.moveToNext()) {
						mPathName = c.getString(0);
						mTitle = c.getString(1);
						SimpleDateFormat formatter = new SimpleDateFormat(
								"mm:ss");
						mTotalTime = formatter.format(c.getLong(2));
					}
				} finally {
					c.close();
					c = null;
				}
			}
			/*
			 * if (path != null) { return Uri.fromFile(new File(path)); } else {
			 * Log.w(TAG, "************ Uri2File2Uri failed ***************");
			 * return videoUri; }
			 */
		} else if (scheme.equals("file")) {
			if (realPath == null || realPath.trim().length() == 0) {
				mPathName = videoUri.getPath();
			} else {
				mPathName = realPath;
			}
			// Log.v("---->>>", "_2Uri___mPathName___" + mPathName);
		}
		item.setmPath(mPathName);
		item.setmTitle(mTitle);
		item.setmTotalTime(mTotalTime);
		// Log.v("----->>>>", "00-----Uri2File2Uri-----Uri2File2Uri-----");
		return item;
	}

	public static String replaceAll(String path) {
		return path.replaceAll(".*/", "").replaceAll("\\..*", "");
	}

	/**
	 * @TODO 外部文件列表
	 */

	public static List<FileVideoItem> getData(FileVideoItem item) {
		// String mDuration = null;
		String mTitle = null;
		if (item.getmTitle() != null) {
			mTitle = item.getmTitle();
		} else {
			mTitle = VideoUtils.replaceAll(item.getmPath());
		}
		List<FileVideoItem> list = new ArrayList<FileVideoItem>();
		list.add(new FileVideoItem(item.getmPath(), mTitle, item
				.getmTotalTime()));
		return list;
	}

	//
	public static void getStatus(Activity context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		int leftStackId = manager.getLeftStackId();
		int rightStackId = manager.getRightStackId();
		int stackBoxId = context.getWindow().getStackBoxId();
		if (leftStackId > 0 && stackBoxId > 0 && rightStackId > 0) {
			manager.setWindowSize(rightStackId, AppConfig.MW_MIN_STACK_WINDOW);
			manager.setWindowSize(leftStackId, AppConfig.MW_MAX_STACK_WINDOW);
		}
	}

	public static void getBootStatus(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		PreferencesUtils.putInt(context, AppConfig.LEFT_STATUS_ID,
				manager.getWindowSizeStatus(manager.getLeftStackId()));
		PreferencesUtils.putInt(context, AppConfig.RIGHT_STATUS_ID,
				manager.getWindowSizeStatus(manager.getRightStackId()));
	}

	public static void setExitStatus(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
//		if (PreferencesUtils.getInt(context, AppConfig.RIGHT_STATUS_ID)!= AppConfig.MW_MIN_STACK_WINDOW) {
//			context.sendBroadcast(new Intent().setAction("WINDOW_EXIT").putExtra("state", 0));
//		}else{
//			context.sendBroadcast(new Intent().setAction("WINDOW_EXIT").putExtra("state", 1));
//		}
		int leftStackId = manager.getLeftStackId();
		int rightStackId = manager.getRightStackId();
		if (leftStackId > 0 && rightStackId > 0) {
			manager.setWindowSize(manager.getLeftStackId(),
					PreferencesUtils.getInt(context, AppConfig.LEFT_STATUS_ID));
			manager.setWindowSize(manager.getRightStackId(),
					PreferencesUtils.getInt(context, AppConfig.RIGHT_STATUS_ID));
		}
	}
	
	public static int getNavigationBarWidth(Context context){
		final boolean splitscreen = SystemProperties.getBoolean("persist.sys.splitscreen", false);
		String naw = "navigation_bar_visibility";
		if(splitscreen){
			//是否异形屏
			naw = "navigation_bar_visibility_splitscreen";
		}
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier(naw, "dimen", "android");
		if (resourceId > 0) {
			return resources.getDimensionPixelSize(resourceId);
		}
		return 0;
	}
}
