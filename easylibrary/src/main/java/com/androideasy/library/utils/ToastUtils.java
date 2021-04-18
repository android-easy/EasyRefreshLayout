package com.androideasy.library.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast封装，避免Toast消息覆盖，替换系统Toast
 * 使Toast变得简单 ,避免Toast多次打印一直显示
 * 第二次显示会覆盖之前的
 * @author Easy
 * QQ 1400100300
 */
public class ToastUtils {

	private static Toast mToast;

	public static void showToast(Context context, String msg, int duration) {
		if(context!=null){
			context = context.getApplicationContext();
		}else{
			return;
		}
		if (mToast == null) {
			mToast = Toast.makeText(context, msg, duration);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

	public static void showToast(Context context, String msg) {// , int duration
		showToast(context, msg,Toast.LENGTH_SHORT);
	}

	public static void shortLong(Context context,String msg) {// , int duration
		showToast(context, msg,Toast.LENGTH_LONG);
	}

	public static void shortToast(Context context,String msg) {// , int duration
		showToast(context, msg,Toast.LENGTH_SHORT);
	}
}
