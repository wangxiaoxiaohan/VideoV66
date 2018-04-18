package com.softwinner.TvdVideo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class mHomeKeyEventRecever extends BroadcastReceiver {
	String SYSTEM_REASON = "reason";
	String SYSTEM_HOME_KEY = "homekey";
	String SYSTEM_HOME_KEY_LONG = "recentapps";
	private String ACTION_CAMERA_LOSS = "com.spreadwin.camera.loss.transient";
	private String EXTRA_VIDEO_FOCUS = "video_focus";
	private int VIDEO_PLAYER = 2; // 播放
	private int VIDEO_PAUSE = 1; // 暂停
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
			String reason = intent.getStringExtra(SYSTEM_REASON);
			if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
				context.sendBroadcast(new Intent().setAction(ACTION_CAMERA_LOSS)
						.putExtra(EXTRA_VIDEO_FOCUS, VIDEO_PLAYER));
			} else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
				// 表示长按home键,显示最近使用的程序列表
			}
		}
	}

}
