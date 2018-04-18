package com.softwinner.TvdVideo;

import java.util.ArrayList;
import java.util.List;

import com.softwinner.TvdVideo.adapter.VideoAdapter;
import com.softwinner.TvdVideo.config.AppConfig;
import com.softwinner.TvdVideo.model.FileVideo;
import com.softwinner.TvdVideo.model.FileVideoItem;
import com.softwinner.TvdVideo.utils.PreferencesUtils;
import com.softwinner.TvdVideo.utils.SplitUtil;
import com.softwinner.TvdVideo.utils.VideoData;
import com.softwinner.TvdVideo.utils.VideoUtils;
import com.softwinner.TvdVideo.view.CustomDialog;
import com.softwinner.TvdVideo.view.CustomToast;
import com.softwinner.TvdVideo.view.MyProcessDialog;
import com.softwinner.TvdVideo.view.SurfaceView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements OnLayoutChangeListener, OnClickListener, OnSeekBarChangeListener {
	@Bind(R.id.swipe_list)
	ListView mVideoList;
	@Bind(R.id.video_list)
	LinearLayout mVideoInfoList;
	@Bind(R.id.controller)
	LinearLayout mController;
	@Bind(R.id.isState)
	TextView mStateText;
	@Bind(R.id.isState1)
	TextView mStateText1;

	@Bind(R.id.image_spende)
	ImageView mSpende;
	@Bind(R.id.image_next)
	ImageView mNextSong;
	@Bind(R.id.image_on)
	ImageView mOnSong;
	@Bind(R.id.image_bj)
	ImageView mImage;
	@Bind(R.id.surface)
	SurfaceView mSurfaceView;
	@Bind(R.id.seekBar)
	SeekBar mSeekBar;
	@Bind(R.id.video_total_time)
	TextView mVideoTotalTime;
	@Bind(R.id.video_current_time)
	TextView mVideoCurrentTime;
	@Bind(R.id.img_not_videolist)
	ImageView mImageNotVideoList;

	@Bind(R.id.drawer_layout)
	DrawerLayout mDrawerLayout;

	private CustomDialog.Builder builder;
	private VideoAdapter adapter;
	private MediaPlayer mPlayer = null;
	private SurfaceHolder mSurfaceHolder;
	private String videoName;

	private int mWhat = 0;
	private int mPosition = 0;
	private int currentPosition = 0;
	private boolean PasueFlag;
	private boolean isClose;
	private boolean isStart;

	private List<FileVideo> mVideo = new ArrayList<FileVideo>();
	private String mPath = null;
	private FileVideoItem item;
	private String mTime = "00:00";
	private MyProcessDialog mProcessDialog;
	private int mCheckoutId = 0;
	private int mFile = 0;
	private AudioManager mAudioManager = null;

	private boolean isType;
	
	private static final String ACTION_SPLIT_WINDOW_HAS_CHANGED = "android.intent.action.SPLIT_WINDOW_HAS_CHANGED";

	// 语音发送广播
	private static final String ACTION_VIDEO_VOICE = "android.intent.action.SPREADWIN.VIDEO";

	private static final String TAG = "VIDEO";
	private Handler mSeekBarSyncHandler = new Handler();

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			mhandler.sendEmptyMessage(1);
		}
	};

	private Handler mTimeHandler = new Handler();
	private Runnable mTimeRunnable = new Runnable() {
		@Override
		public void run() {
			if (mController.getVisibility() == View.VISIBLE) {
				mController.setVisibility(View.GONE);
			}
		}
	};

	private Handler mHintPlayHnadler = new Handler();
	private Runnable mHintPlayRunnable = new Runnable() {

		@Override
		public void run() {
			mProcessDialog.dismiss();
			CustomToast.show(getApplicationContext(), R.string.not_stop);
			finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		isType = true;
		mReceiver();
		initView();
		registerListener();
	}

	private void initView() {
		mOnSong.setOnClickListener(this);
		mSpende.setOnClickListener(this);
		adapter = new VideoAdapter(this);
		mNextSong.setOnClickListener(this);
		mController.setOnClickListener(this);
		mSurfaceView.setOnClickListener(this);
		mSeekBar.setOnSeekBarChangeListener(this);
		mProcessDialog = new MyProcessDialog(this);

		mProcessDialog.setCancelable(false);
		mProcessDialog.setMsg(R.string.down_camera);
		mVideoCurrentTime.setText(mTime);
		mVideoTotalTime.setText(mTime);

		mPlayer = new MediaPlayer();
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceView.getHolder().setKeepScreenOn(true);
		mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		mDrawerLayout.addOnLayoutChangeListener(this);
		builder = new CustomDialog.Builder(this);
		builder.setMessage(R.string.play_stop);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				sendBroadcast(new Intent().setAction(AppConfig.STOP_VIDEO));
				Log.d(TAG, "停止录像");
				isClose = true;
				dialog.dismiss();
				mProcessDialog.show();
				mHintPlayHnadler.postDelayed(mHintPlayRunnable, 10000);
				sendBroadcast(new Intent().setAction(AppConfig.ACTION_REQUEST_STATUS));
			}
		});
		builder.setNegativeButton(R.string.cancel, new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				isStart = false;
				finish();
				dialog.dismiss();
			}
		});
		isDisplay(false);
		surfaceCallback();
	}

	private void mReceiver() {
		Log.d(TAG, "发送广播:" + AppConfig.ACTION_REQUEST_STATUS);
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConfig.ACTION_REPLY_STATUS);
		filter.addAction("android.intent.action.SPLIT_WINDOW_HAS_CHANGED");
		filter.addAction("WINDOW_STATUS_CHANGED");
		filter.addAction(ACTION_VIDEO_VOICE);
		registerReceiver(MyReceiver, filter);
		sendBroadcast(new Intent().setAction(AppConfig.ACTION_REQUEST_STATUS));
		isPause = true;
	}

	private BroadcastReceiver MyReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(AppConfig.ACTION_REPLY_STATUS)) {
				isPause = false;
				boolean isFlags = intent.getBooleanExtra("isrecording", false);
				if (isFlags == false) {
					mProcessDialog.dismiss();
					mHintPlayHnadler.removeCallbacks(mHintPlayRunnable);
					realPlay();
				} else {
					if (!isClose) {
						isStart = true;
						builder.crater().show();
					} else {
						sendBroadcast(new Intent().setAction(AppConfig.ACTION_REQUEST_STATUS));
					}
				}
			}

			if (intent.getAction().equals("android.intent.action.SPLIT_WINDOW_HAS_CHANGED")) {
				// ActivityManager am = (ActivityManager)
				// getSystemService(Context.ACTIVITY_SERVICE);
				// int leftStackId = am.getLeftStackId();
				// if (leftStackId > 0 && am.getWindowSizeStatus(leftStackId) ==
				// 3) {
				// setListVideMargirns();
				// }
			}
			if (intent.getAction().equals("WINDOW_STATUS_CHANGED")) {
				int size = intent.getIntExtra("windowStatus", -1);
				boolean isOnLeft = intent.getBooleanExtra("isOnLeft", true);
				int stackBoxId = getWindow().getStackBoxId();
				if (SplitUtil.getStackPostition(getApplicationContext(), stackBoxId) == 0) {
					if (!isOnLeft) {
						if (size == 2) {
							Log.d(TAG, size + "");
							Log.d(TAG, isOnLeft + "");
							// setSpende();
						}
					}
				}
			}

			if (intent.getAction().equals(ACTION_VIDEO_VOICE)) {
				String state = intent.getStringExtra("state");
				if ("video_play".equals(state)) {
					if (!mPlayer.isPlaying())
						setPlay();
				}
			}
		}
	};

	private void isDisplay(boolean isFlags) {
		mController.setVisibility(View.GONE);
	}

	private void setmHnadler() {
		mTimeHandler.removeCallbacks(mTimeRunnable);
		mTimeHandler.postDelayed(mTimeRunnable, 5000);
	}

	boolean isPause = false;

	public void surfaceCallback() {
		mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (mPlayer != null) {
					if (mPlayer.isPlaying()) {
						currentPosition = mPlayer.getCurrentPosition();
						mSeekBarSyncHandler.removeCallbacks(runnable);
						mPlayer.pause();
						isPause = true;
						mAudioManager.abandonAudioFocus(mAudioFocusListener);
					}
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if (mPlayer != null) {
					mPlayer.setDisplay(holder);
					if (isPause) {
						realPlay();
					}
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

			}
		});
	}

	private void realPlay() {
		isDisplay(true);
		isPause = false;
		if (getIntent().getData() != null) {
			mFile = 0;
			// 从文件管理视频文件 点击进入
			mPath = getIntent().getStringExtra("VideoPath000");
			item = VideoUtils.Uri2File2Uri(getIntent().getData(), getApplicationContext(), mPath);
			// mVideoList.setVisibility(View.GONE);
			mNextSong.setEnabled(false);
			mOnSong.setEnabled(false);
			initData(-1, item.getmPath(), currentPosition);
			mController.setVisibility(View.GONE);
			currentPosition = 0;
			adapter.setFileData(VideoUtils.getData(item));
			mVideoList.setAdapter(adapter);
		} else {
			mFile = 1;
			// 从本地视频点击进入播放
			new VideoData(FileVideoCallback, getApplicationContext()).run();
		}
	}

	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (mPlayer != null && mPlayer.isPlaying()) {
					try {
						mSeekBar.setMax(mPlayer.getDuration());
						mVideoTotalTime.setText(VideoUtils.getTime(mPlayer.getDuration()));
						currentPosition = mPlayer.getCurrentPosition();
						mSeekBar.setProgress(currentPosition);
						mVideoCurrentTime.setText(VideoUtils.getTime(currentPosition));
						mSeekBarSyncHandler.postDelayed(runnable, 1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
	};

	@SuppressWarnings("deprecation")
	private void registerListener() {

		mStateText.setOnClickListener(this);
		mStateText1.setOnClickListener(this);

		mVideoList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (PasueFlag == true) {
					PasueFlag = false;
				}
				mPosition = position;
				mSpende.setVisibility(View.GONE);
				currentPosition = 0;
				mVideoCurrentTime.setText(mTime);
				mVideoTotalTime.setText(mTime);
				if (getIntent().getData() != null) {
					initData(-1, item.getmPath(), currentPosition);
				} else {
					initData(position, null, currentPosition);
				}
				mSeekBar.setProgress(0);
				mDrawerLayout.closeDrawer(mVideoInfoList);
			}
		});
		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub
				mDrawerLayout.bringChildToFront(arg0);
				mDrawerLayout.requestLayout();
			}

			@Override
			public void onDrawerOpened(View arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initData(int position, String path, int currentPosition) {
		mSpende.setVisibility(View.GONE);
		if (PasueFlag && mPlayer != null) {
			mPlayer.start();
			mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,
					AudioManager.AUDIOFOCUS_GAIN);
			mSeekBarSyncHandler.post(runnable);
			PasueFlag = false;
		} else {
			try {
				if (mPlayer != null) {
					mPlayer.reset();
					mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					if (getIntent().getData() == null || mVideo.size() != 0) {
						Log.d("mplayer", mVideo.get(position).getUrl());
						adapter.setPositionSelector(position);
						mPlayer.setDataSource(mVideo.get(position).getUrl());
						videoName = mVideo.get(position).getUrl();
						mPlayer.setOnCompletionListener(MediaPlayerCompletionListener);
					} else {
						mPlayer.setDataSource(path);
						mPlayer.setLooping(true);
					}
					mPlayer.setOnPreparedListener(new MediaPlayerPreparedListener(currentPosition));
					mPlayer.setOnErrorListener(new OnErrorListener() {
						@Override
						public boolean onError(MediaPlayer mp, int what, int extra) {
							mWhat = what;
							Log.d("=====error_what", mWhat + "");
							Log.d("------------", extra + "");
							return false;
						}
					});
					try {
						mPlayer.prepareAsync();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				CustomToast.show(getApplicationContext(), R.string.cannot_play);
				e.printStackTrace();
			}
		}
	}

	/**
	 * @TODO 准备播放回调
	 * @author Administrator
	 * 
	 */
	private final class MediaPlayerPreparedListener implements OnPreparedListener {
		private int position;

		public MediaPlayerPreparedListener(int position) {
			this.position = position;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			PasueFlag = false;
			mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,
					AudioManager.AUDIOFOCUS_GAIN);
			mPlayer.start();
			if (position > 0)
				mPlayer.seekTo(position);
			mSeekBarSyncHandler.post(runnable);
			Log.d("mPlayer.getDuration", mPlayer.getDuration() + "");
		}
	}

	/**
	 * @TODO 播放完成回调
	 */
	MediaPlayer.OnCompletionListener MediaPlayerCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			Log.d("======>>mPlayer.getDuration", mPlayer.getDuration() + "");
			Log.d("======>>mSeekBar.getProgress", mSeekBar.getProgress() + "");
			Log.d("======>>what", mWhat + "");
			if (mWhat != -1004 && mWhat != -38 && mWhat != 900) {
				setNextSong();
			}
			mWhat = 0;
		}
	};

	/**
	 * @TODO 回调返回sd卡视频文件
	 */
	public VideoData.Callback FileVideoCallback = new VideoData.Callback() {
		@Override
		public void onSuccess(List<FileVideo> mVideoInfo) {
			if (mVideoInfo.size() != 0) {
				currentPosition = PreferencesUtils.getInt(getApplicationContext(), "currentPosition");
				videoName = PreferencesUtils.getString(getApplicationContext(), "videoName");
				mImageNotVideoList.setVisibility(View.GONE);
				mVideoList.setVisibility(View.VISIBLE);
				mController.setVisibility(View.GONE);
				mVideo = mVideoInfo;
				mSurfaceView.setEnabled(true);
				mNextSong.setEnabled(true);
				mOnSong.setEnabled(true);
				mVideoList.setBackgroundResource(R.color.gray);
				adapter.setData(mVideoInfo);
				mVideoList.setAdapter(adapter);
				for (int i = 0; i < mVideo.size(); i++) {
					if (videoName != null && videoName.contains(mVideo.get(i).getUrl())) {
						mPosition = i;
					}
				}
				initData(mPosition, null, currentPosition);
			} else {
				mImageNotVideoList.setVisibility(View.VISIBLE);
				mVideoList.setVisibility(View.GONE);
				mOnSong.setEnabled(false);
				mNextSong.setEnabled(false);
				mSurfaceView.setEnabled(false);
				mController.setVisibility(View.VISIBLE);
				mSurfaceView.setBackgroundResource(R.color.gray);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_spende:
			setPlay();
			mController.setVisibility(View.GONE);
			break;
		case R.id.surface:
			if (mController.getVisibility() == View.GONE) {
				mController.setVisibility(View.VISIBLE);
				setmHnadler();
			} else if (mController.getVisibility() == View.VISIBLE) {
				if (PasueFlag == false) {
					setSpende();
				} else {
					setPlay();
					mController.setVisibility(View.GONE);
				}
			}
			break;
		case R.id.image_next:
			setmHnadler();
			setLastSong();
			break;
		case R.id.image_on:
			setmHnadler();
			setNextSong();
			break;
		case R.id.isState:
		case R.id.isState1:
			mDrawerLayout.openDrawer(mVideoInfoList);
		}
	}

	/**
	 * TODO 暂停
	 */
	public void setSpende() {
		mSeekBarSyncHandler.removeCallbacks(runnable);
		mSpende.setVisibility(View.VISIBLE);
		if (mPlayer != null && mPlayer.isPlaying()) {
			mPlayer.pause();
			PasueFlag = true;
			mAudioManager.abandonAudioFocus(mAudioFocusListener);
		}
		isPause = true;
	}

	/**
	 * TODO 播放
	 */
	public void setPlay() {
		if (getIntent().getData() != null) {
			initData(-1, item.getmPath(), currentPosition);
		} else {
			initData(mPosition, null, currentPosition);
		}
	}

	/**
	 * TODO 下一个
	 */
	public void setLastSong() {
		if (mPosition > 0) {
			mPosition -= 1;
		} else {
			mPosition = mVideo.size() - 1;
		}
		if (PasueFlag == true) {
			PasueFlag = false;
			mSpende.setVisibility(View.GONE);
		}
		currentPosition = 0;
		mVideoCurrentTime.setText(mTime);
		mVideoTotalTime.setText(mTime);
		mSeekBar.setProgress(0);
		initData(mPosition, null, currentPosition);
	}

	/**
	 * TODO 上一个
	 */
	public void setNextSong() {
		if (mPosition < mVideo.size() - 1) {
			mPosition += 1;
		} else {
			mPosition = 0;
		}
		if (PasueFlag == true) {
			PasueFlag = false;
			mSpende.setVisibility(View.GONE);
		}
		currentPosition = 0;
		mVideoCurrentTime.setText(mTime);
		mVideoTotalTime.setText(mTime);
		mSeekBar.setProgress(0);
		initData(mPosition, null, currentPosition);
	}

	/**
	 * TODO //当用户结束对滑块滑动时,调用该方法（即松开鼠标）
	 */

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (mVideo.size() != 0) {
			setPlay();
		} else {
			mVideoTotalTime.setText(mTime);
		}
		setmHnadler();
	}

	/**
	 * TODO 当用户开始滑动滑块时调用该方法（即按下鼠调用一次）
	 */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		setSpende();
	}

	/**
	 * TODO 当拖动条发生变化时调用该方法
	 */

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser == true) {
			setmHnadler();
			mSpende.setVisibility(View.GONE);
			currentPosition = mSeekBar.getProgress();
			Log.d("mPlayer.getDuration", currentPosition + "");
			if (mPlayer != null) {
				if (currentPosition > 0) {
					mPlayer.seekTo(currentPosition);
				}
				mVideoCurrentTime.setText(VideoUtils.getTime(progress));
			}
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		release();
		isPause = true;
		if (isStart)
			sendBroadcast(new Intent().setAction(AppConfig.START_VIDEO));
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mPlayer != null) {
			if (mFile == 1) {
				PreferencesUtils.putInt(getApplicationContext(), "currentPosition", mPlayer.getCurrentPosition());
				PreferencesUtils.putInt(getApplicationContext(), "checkoutId", mCheckoutId);
				PreferencesUtils.putString(getApplicationContext(), "videoName", videoName);
			}
		}
	}

	@Override
	protected void onResume() {
//		if (isType) {
//			if (!SystemProperties.getBoolean("persist.sys.screen_full", false)) {
//				int stackBoxId = getWindow().getStackBoxId();
//				if (SplitUtil.getStackPostition(getApplicationContext(), stackBoxId) == 1) {
//					VideoUtils.getStatus(MainActivity.this);
//				}
//			}
//		}
//		isType = false;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mSeekBarSyncHandler.removeCallbacks(runnable);
		unregisterReceiver(MyReceiver);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		release();
		if (isStart) {
			Log.d(TAG, "开始录像");
			sendBroadcast(new Intent().setAction(AppConfig.START_VIDEO));
		}
		super.onBackPressed();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			switch (focusChange) {
			case AudioManager.AUDIOFOCUS_LOSS:
				if (mPlayer.isPlaying()) {
					setSpende();
				}
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				if (mPlayer.isPlaying()) {
					mPlayer.setVolume(0.2f, 0.2f);
				}
				break;
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
			case AudioManager.AUDIOFOCUS_GAIN:
				mPlayer.setVolume(1.0f, 1.0f);
				break;
			}
		}
	};

	private void release() {
		mAudioManager.abandonAudioFocus(mAudioFocusListener);
		if (mPlayer != null) {
			if (mFile == 1) {
				PreferencesUtils.putInt(getApplicationContext(), "currentPosition", mPlayer.getCurrentPosition());
				PreferencesUtils.putInt(getApplicationContext(), "checkoutId", mCheckoutId);
				PreferencesUtils.putString(getApplicationContext(), "videoName", videoName);
			}
			if (mPlayer != null) {
				mPlayer.stop();
				mPlayer.release();
				mPlayer = null;
			}
		}
	}


	public void setListVideMargirns() {
		mStateText.setVisibility(View.GONE);
		mStateText1.setVisibility(View.VISIBLE);
		DrawerLayout.LayoutParams lp = new DrawerLayout.LayoutParams(380, DrawerLayout.LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.END;
		mVideoInfoList.setLayoutParams(lp);
		adapter.isFullSuccer(true);
	}

	private void RightShow() {
		mStateText.setVisibility(View.VISIBLE);
		mStateText1.setVisibility(View.GONE);
		DrawerLayout.LayoutParams lp = new DrawerLayout.LayoutParams(200, DrawerLayout.LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.START;
		mVideoInfoList.setLayoutParams(lp);
		adapter.isFullSuccer(false);
	}

	private void mLeftShow() {
		mStateText.setVisibility(View.GONE);
		mStateText1.setVisibility(View.VISIBLE);
		DrawerLayout.LayoutParams lp = new DrawerLayout.LayoutParams(330, DrawerLayout.LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.END;
		mVideoInfoList.setLayoutParams(lp);
		adapter.isFullSuccer(false);
	}

	@Override
	protected void onActivityMove(boolean isToLeft) {
		super.onActivityMove(isToLeft);
		if (isToLeft) {
			mLeftShow();
		}
	}

	@Override
	public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
			int oldBottom) {
		if (left == 0 && right == 0 && top == 0 && bottom == 0) {
			return;
		}
		if (left == oldLeft && right == oldRight) {
			return;
		}
		int width = right - left;
		Log.i("RecorderReplay", "onGlobalLayout " + width);
		String model = SystemProperties.get("ro.product.model");
//		if ("XFRm8".equals(model)) {
//			mLeftShow();
//			if (width == 480) {
//				onBackPressed();
//			}
//		} else {
			if (width == 600|| width == 1000) {
				mLeftShow();
			} else if (width == 540) {
				RightShow();
			} else if (width == 1280) {
				setListVideMargirns();
			} else if (width == 480) {
				onBackPressed();
			}
//		}
	}

}
