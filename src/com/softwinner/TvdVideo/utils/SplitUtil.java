package com.softwinner.TvdVideo.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SplitUtil {
    public static final String TAG = "SplitUtil";
    
    static public int getLeftStackId(Context ct) {
        int ret = -1;
        if (ct == null) {
            return ret;
        }
        try {
            Object am = (ActivityManager) ct.getSystemService(Context.ACTIVITY_SERVICE);
            Class<?> activityManager = Class.forName("android.app.ActivityManager");
            Method myGetStackBoxId = null;
            if (am != null) {
                myGetStackBoxId = activityManager.getMethod("getLeftStackId");
                myGetStackBoxId.setAccessible(true);
                ret = Integer.parseInt(myGetStackBoxId.invoke(am).toString());
            }
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "getLeftStackId reflect failed");
            ret = -40;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            ret = -40;
        }
        return ret;
    }
    
    static public int getRightStackId(Context ct) {
        int ret = -1;
        if (ct == null) {
            return ret;
        }
        try {
            Object am = (ActivityManager) ct.getSystemService(Context.ACTIVITY_SERVICE);
            Class<?> activityManager = Class.forName("android.app.ActivityManager");
            Method myGetStackBoxId = null;
            if (am != null) {
                myGetStackBoxId = activityManager.getMethod("getRightStackId");
                myGetStackBoxId.setAccessible(true);
                ret = Integer.parseInt(myGetStackBoxId.invoke(am).toString());
            }
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "getLeftStackId reflect failed");
            ret = -40;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            ret = -40;
        }
        return ret;
    }
    
    static public Bitmap getTaskTopThumbnail(Context ct, int stackId) {
        Log.d(TAG, "getTaskTopThumbnail stackId=" + stackId);
        Bitmap ret = null;
        if (ct == null) {
            return ret;
        }
        try {
            Object am = (ActivityManager) ct.getSystemService(Context.ACTIVITY_SERVICE);
            Class<?> activityManager = Class.forName("android.app.ActivityManager");
            Method getTaskTopThumbnail = null;
            if (am != null) {
                getTaskTopThumbnail = activityManager.getMethod("getTaskTopThumbnail", int.class);
                getTaskTopThumbnail.setAccessible(true);
                ret = (Bitmap) getTaskTopThumbnail.invoke(am, stackId);
            }
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "getTaskTopThumbnail reflect failed");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return ret;
    }
    
    static public int getStackId(RecentTaskInfo ti) {
        int ret = -1;
        try {
            Class<?> myClass = Class.forName("android.app.ActivityManager$RecentTaskInfo");
            Field stackId = null;
            if (ti != null) {
                stackId = myClass.getField("stackId");
                stackId.setAccessible(true);
                ret = Integer.parseInt(stackId.get(ti).toString());
            }
        } catch (NoSuchFieldException e) {
            Log.d(TAG, "getStackId reflect failed");
            ret = -40;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            ret = -40;
        }
        return ret;
    }
    
    static public int getStackBoxId(Activity act) {
        int ret = -1;
        try {
            Object window = act.getWindow();
            Class<?> myWindow = Class.forName("android.view.Window");
            Method myGetStackBoxId = null;
            if (window != null) {
                myGetStackBoxId = myWindow.getMethod("getStackBoxId");
                myGetStackBoxId.setAccessible(true);
                ret = Integer.parseInt(myGetStackBoxId.invoke(window).toString());
            }
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "getLeftStackId reflect failed");
            ret = -40;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            ret = -40;
        }
        return ret;
    }
    
    static public int getWindowSizeStatus(Context ct, int stackId) {
        int ret = -1;
        try {
            Object am = (ActivityManager) ct.getSystemService(Context.ACTIVITY_SERVICE);
            Class<?> activityManager = Class.forName("android.app.ActivityManager");
            Method myGetWindowSizeStatus = null;
            if (am != null) {
                myGetWindowSizeStatus = activityManager.getMethod("getWindowSizeStatus", int.class);
                myGetWindowSizeStatus.setAccessible(true);
                ret = Integer.parseInt(myGetWindowSizeStatus.invoke(am, stackId).toString());
            }
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "getWindowSizeStatus reflect failed");
            ret = -40;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            ret = -40;
        }
        return ret;
    }
    
    static public void setWindowSize(Context ct, int stackId, int size) {
        try {
            Object am = (ActivityManager) ct.getSystemService(Context.ACTIVITY_SERVICE);
            Class<?> activityManager = Class.forName("android.app.ActivityManager");
            Method mySetWindowSize = null;
            if (am != null) {
                mySetWindowSize = activityManager.getMethod("setWindowSize", int.class, int.class);
                mySetWindowSize.setAccessible(true);
                mySetWindowSize.invoke(am, stackId, size);
            }
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "setWindowSize reflect failed");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return;
    }
    
    static public RecentTaskInfo getTopTaskOfStack(Context ct, int stackId) {
        RecentTaskInfo ret = null;
        if (ct == null) {
            return ret;
        }
        try {
            Object am = (ActivityManager) ct.getSystemService(Context.ACTIVITY_SERVICE);
            Class<?> activityManager = Class.forName("android.app.ActivityManager");
            Method myGetTopTaskOfStack = null;
            if (am != null) {
            	myGetTopTaskOfStack = activityManager.getMethod("getTopTaskOfStack", int.class);
            	myGetTopTaskOfStack.setAccessible(true);
                ret = (RecentTaskInfo) myGetTopTaskOfStack.invoke(am, stackId);
            }
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "getTopTaskOfStack reflect failed");
            ret = null;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            ret = null;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            ret = null;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            ret = null;
        }
        return ret;
    }
    
    static public int getStackPostition(Context ct, int stackId) {
        int ret = -1;
        if (ct == null) {
            return ret;
        }
        try {
            Object am = (ActivityManager) ct.getSystemService(Context.ACTIVITY_SERVICE);
            Class<?> activityManager = Class.forName("android.app.ActivityManager");
            Method myGetStackPostition = null;
            if (am != null) {
                myGetStackPostition = activityManager.getMethod("getStackPostition", int.class);
                myGetStackPostition.setAccessible(true);
                ret = Integer.parseInt(myGetStackPostition.invoke(am, stackId).toString());
            }
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "getStackPostition reflect failed");
            ret = -40;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            ret = -40;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            ret = -40;
        }
        return ret;
    }
    
    static public void setSplitButtonVisibility(Context ct, boolean isVisible) {
        if (ct == null) {
            return;
        }

        try {
            Object service =  ct.getSystemService("statusbar");
            Method setSplitButtonVisibility = null;
            if (service != null) {
                setSplitButtonVisibility = service.getClass().getMethod("setSplitButtonVisibility", boolean.class);
                if (setSplitButtonVisibility != null) {
                    setSplitButtonVisibility.setAccessible(true);
                    setSplitButtonVisibility.invoke(service, isVisible);
                }
            }
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "setSplitButtonVisibility reflect failed");
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}
