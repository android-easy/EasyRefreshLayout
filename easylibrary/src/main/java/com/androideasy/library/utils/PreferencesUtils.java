package com.androideasy.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class  PreferencesUtils {

	public static final String NAME = "USER_PREFERENCES";
	public static String getPreferences(Context context, String name) {
		SharedPreferences preferences = context.getSharedPreferences(
				NAME, Context.MODE_PRIVATE);
		return preferences.getString(name, "");
	}

	public static void setPreferences(Context context, String name, String value) {
		Editor sharedata = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE).edit();
		sharedata.putString(name, value);
		sharedata.commit();
	}

	public static int getIntPreferences(Context context, String name) {
		SharedPreferences preferences = context.getSharedPreferences(
				NAME, Context.MODE_PRIVATE);
		return preferences.getInt(name, 0);
	}

	public static void setIntPreferences(Context context, String name, int value) {
		Editor sharedata = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE).edit();
		sharedata.putInt(name, value);
		sharedata.commit();
	}

	public static boolean getBooleanPreferences(Context context, String name) {
		SharedPreferences preferences = context.getSharedPreferences(
				NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(name, false);
	}

	public static void setBooleanPreferences(Context context, String name,
			boolean value) {
		Editor sharedata = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE).edit();
		sharedata.putBoolean(name, value);
		sharedata.commit();
	}

	public static long getLongPreferences(Context context, String name) {

		SharedPreferences preferences = context.getSharedPreferences(
				NAME, Context.MODE_PRIVATE);
		return preferences.getLong(name, 0L);
	}

	public static void setLongPreferences(Context context, String name,
			long value) {

		Editor sharedata = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE).edit();
		sharedata.putLong(name, value);
		sharedata.commit();
	}
}
