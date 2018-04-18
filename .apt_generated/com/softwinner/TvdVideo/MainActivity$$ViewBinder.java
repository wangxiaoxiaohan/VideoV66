// Generated code from Butter Knife. Do not modify!
package com.softwinner.TvdVideo;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.softwinner.TvdVideo.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296270, "field 'mVideoInfoList'");
    target.mVideoInfoList = finder.castView(view, 2131296270, "field 'mVideoInfoList'");
    view = finder.findRequiredView(source, 2131296271, "field 'mVideoList'");
    target.mVideoList = finder.castView(view, 2131296271, "field 'mVideoList'");
    view = finder.findRequiredView(source, 2131296263, "field 'mNextSong'");
    target.mNextSong = finder.castView(view, 2131296263, "field 'mNextSong'");
    view = finder.findRequiredView(source, 2131296265, "field 'mVideoCurrentTime'");
    target.mVideoCurrentTime = finder.castView(view, 2131296265, "field 'mVideoCurrentTime'");
    view = finder.findRequiredView(source, 2131296268, "field 'mOnSong'");
    target.mOnSong = finder.castView(view, 2131296268, "field 'mOnSong'");
    view = finder.findRequiredView(source, 2131296272, "field 'mImageNotVideoList'");
    target.mImageNotVideoList = finder.castView(view, 2131296272, "field 'mImageNotVideoList'");
    view = finder.findRequiredView(source, 2131296261, "field 'mController'");
    target.mController = finder.castView(view, 2131296261, "field 'mController'");
    view = finder.findRequiredView(source, 2131296267, "field 'mSeekBar'");
    target.mSeekBar = finder.castView(view, 2131296267, "field 'mSeekBar'");
    view = finder.findRequiredView(source, 2131296266, "field 'mVideoTotalTime'");
    target.mVideoTotalTime = finder.castView(view, 2131296266, "field 'mVideoTotalTime'");
    view = finder.findRequiredView(source, 2131296256, "field 'mDrawerLayout'");
    target.mDrawerLayout = finder.castView(view, 2131296256, "field 'mDrawerLayout'");
    view = finder.findRequiredView(source, 2131296262, "field 'mStateText'");
    target.mStateText = finder.castView(view, 2131296262, "field 'mStateText'");
    view = finder.findRequiredView(source, 2131296260, "field 'mSpende'");
    target.mSpende = finder.castView(view, 2131296260, "field 'mSpende'");
    view = finder.findRequiredView(source, 2131296258, "field 'mImage'");
    target.mImage = finder.castView(view, 2131296258, "field 'mImage'");
    view = finder.findRequiredView(source, 2131296259, "field 'mSurfaceView'");
    target.mSurfaceView = finder.castView(view, 2131296259, "field 'mSurfaceView'");
    view = finder.findRequiredView(source, 2131296269, "field 'mStateText1'");
    target.mStateText1 = finder.castView(view, 2131296269, "field 'mStateText1'");
  }

  @Override public void unbind(T target) {
    target.mVideoInfoList = null;
    target.mVideoList = null;
    target.mNextSong = null;
    target.mVideoCurrentTime = null;
    target.mOnSong = null;
    target.mImageNotVideoList = null;
    target.mController = null;
    target.mSeekBar = null;
    target.mVideoTotalTime = null;
    target.mDrawerLayout = null;
    target.mStateText = null;
    target.mSpende = null;
    target.mImage = null;
    target.mSurfaceView = null;
    target.mStateText1 = null;
  }
}
