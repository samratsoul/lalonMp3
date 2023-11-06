package com.mp3.musicplayer.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.mp3.musicplayer.Utils.Constant.PREFERENCE;

public class SharedPref {

    public SharedPreferences mSharedPreference = null;
    private Context mContext = null;

    public SharedPref(Context context) {
        this.mContext = context;
        mSharedPreference = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
    }

    /***
     * @param key : key for shared preference
     * @param value : String value for respective key
     * @return return true if sucessfully write to preference
     */
    public boolean writeStringToPref(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /***
     * @param key : key for preference
     * @return return true if sucessfully write to preference
     */
    public boolean revoveKeyToPref(String key) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.remove(key);
        return editor.commit();
    }


    /***
     * @param key : key for shared preference
     * @param value : int value for respective key
     * @return return true if sucessfully write to preference
     */
    public boolean writeIntToPref(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /***
     * @param key : key for shared preference
     * @param value : bool value for respective key
     * @return return true if sucessfully write to preference
     */
    public boolean writeBoolToPref(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /***
     * @param key : key of respective value
     * @param default_value : default string if value is null
     * @return return default string if value is null
     */
    public String getString(String key, String default_value) {
        return mSharedPreference.getString(key, default_value);
    }

    /***
     * @param key : key of respective value
     * @return return false if value is null
     */
    public boolean getBoolean(String key) {
        return mSharedPreference.getBoolean(key, false);
    }

    /***
     * @param key : key of respective value
     * @return return false if value is null
     */
    public boolean getBooleanDefaultTrue(String key) {
        return mSharedPreference.getBoolean(key, true);
    }

    /***
     * @param key : key of respective value
     * @return return -1 if value is null
     */
    public int getInt(String key) {
        return mSharedPreference.getInt(key, 0);
    }
}
