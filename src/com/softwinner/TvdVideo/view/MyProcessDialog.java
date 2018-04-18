
package com.softwinner.TvdVideo.view;

import com.softwinner.TvdVideo.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

/**
 * 等待对话框
 * 
 * @author
 */
public class MyProcessDialog extends Dialog {
	private Context mContext;
	private TextView txt_info;

	public MyProcessDialog(Context context) {
		super(context, R.style.MyProgressDialog);
		this.mContext = context;
		this.setContentView(R.layout.progress_dialog);
		txt_info = (TextView) this.findViewById(R.id.txt_wait);
	}

	public void setMsg(int msg) {
		setMsg(mContext.getResources().getString(msg));
	}

	public void setMsg(String msg) {
		if (null != txt_info) {
			txt_info.setText(msg);
		}
	}
}
