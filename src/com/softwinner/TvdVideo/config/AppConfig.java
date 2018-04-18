package com.softwinner.TvdVideo.config;

public class AppConfig {
	
    public static final String ACTION_REQUEST_STATUS = "com.spreadwin.camera.requeststatus";
    
    public static final String ACTION_REPLY_STATUS = "com.spreadwin.camera.replystatus";
    // 停止录像
    public static final String STOP_VIDEO = "com.spreadwin.camera.stopvideo";
    // 开始录像
    public static final String START_VIDEO = "com.spreadwin.camera.startvideo";
    
    //全屏
    public static final int MW_MAX_STACK_WINDOW = 1;
    //隐藏
    public static final int MW_MIN_STACK_WINDOW = 2;
    //分屏
    public static final int MW_NORMAL_STACK_WINDOW = 0;
    //无效
    public static final int MW_INVALID_STACK_WINDOW = -1;
    //左边状态ID
    public static final String LEFT_STATUS_ID = "left";
    //右边状态ID
    public static final String RIGHT_STATUS_ID = "right";
}

