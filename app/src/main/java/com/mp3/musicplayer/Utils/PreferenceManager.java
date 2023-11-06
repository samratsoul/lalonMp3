package com.mp3.musicplayer.Utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mp3.musicplayer.Model.AllSong;
import com.mp3.musicplayer.Model.Artist_Genrs_Album;
import com.mp3.musicplayer.Model.Song;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mp3.musicplayer.Utils.Constant.PRESS_RATE_US;

public class PreferenceManager extends SharedPref {
    public static final String TAG = PreferenceManager.class.getSimpleName();
    private static PreferenceManager mInstance = null;
    private Gson gson = new Gson();

    public PreferenceManager(Context context) {
        super(context);
    }

    public static PreferenceManager getInstance(Context context) {
        if (mInstance == null)
            return new PreferenceManager(context);
        else
            return mInstance;
    }

    public int getLanguagePositionSet() {
        return getInt(Constant.LANGUAGE_STATE);
    }

    public void setLanguagePositionSet(int position) {
        writeIntToPref(Constant.LANGUAGE_STATE, position);
    }

    public void setSongPath(String path) {
        writeStringToPref(Constant.SONG_PATH, path);
    }

    public String getSongPath(String defaultValue) {
        return getString(Constant.SONG_PATH, defaultValue);
    }

    public void setCurrentTime(String currentTime) {
        writeStringToPref(Constant.CURRENT_TIME, currentTime);
    }

    public String getCurrentTime(String defaultValue) {
        return getString(Constant.CURRENT_TIME, defaultValue);
    }

    public void setISFirstTimeCreated(Boolean value) {
        writeBoolToPref(Constant.FIRST_CREATED, value);
    }

    public Boolean isFirstTimeCreated() {
        return getBooleanDefaultTrue(Constant.FIRST_CREATED);
    }

    public ArrayList<Artist_Genrs_Album> getPlaylistMenu() {
        String json = getString(Constant.PLAYLIST_MENU, "");
        Type type = new TypeToken<ArrayList<Artist_Genrs_Album>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setPlaylistMenu(List<Artist_Genrs_Album> playlistMenu) {
        gson = new Gson();
        String json = gson.toJson(playlistMenu);
        writeStringToPref(Constant.PLAYLIST_MENU, json);
    }

    public void setlastPlayedMenu(List<Song> playedItems) {
        gson = new Gson();
        String json = gson.toJson(playedItems);
        writeStringToPref(Constant.LAST_PLAYED_MENU, json);
    }


    public ArrayList<Song> getLastPlayedMenuItems() {
        String json = getString(Constant.LAST_PLAYED_MENU, "");
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public void setPlayListSecondPosition(int position, List<Song> items) {
        gson = new Gson();
        String json = gson.toJson(items);
        writeStringToPref(Constant.name_playlist.get(position - 2).getArtist(), json);
    }

    public void setAllSong(List<AllSong> items) {
        revoveKeyToPref(Constant.ALL_SONGS_PREF);
        gson = new Gson();
        String json = gson.toJson(items);
        writeStringToPref(Constant.ALL_SONGS_PREF, json);
    }

    public ArrayList<AllSong> getAllSong() {
        String json = getString(Constant.ALL_SONGS_PREF, "");
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<Song> getPlayListSecondPosition(int position) {
        String json = getString(Constant.name_playlist.get(position - 2).getArtist(), "");
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void removePlayListSecondPosition(int position) {
        revoveKeyToPref(Constant.name_playlist.get(position - 2).getArtist());
    }

    public void setPlayListFirstPosition(int position, List<Song> items) {
        gson = new Gson();
        String json = gson.toJson(items);
        writeStringToPref(Constant.name_playlist.get(position - 1).getArtist(), json);
    }

    public ArrayList<Song> getPlayListFirstPosition(int position) {
        String json = getString(Constant.name_playlist.get(position - 1).getArtist(), "");
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public void setPlayListByName(String name, List<Song> items) {
        gson = new Gson();
        String json = gson.toJson(items);
        writeStringToPref(name, json);
    }

    public ArrayList<Song> getPlayListByName(String name) {
        String json = getString(name, "");
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void removePlaylistByname(String name) {
        revoveKeyToPref(name);
    }

    public int getBaseProgress() {
        return getInt(Constant.BASSPROGRESS);
    }

    public void setBaseProgress(int progress) {
        writeIntToPref(Constant.BASSPROGRESS, progress);
    }

    public boolean getBase() {
        return getBoolean(Constant.BASEACTIVE);
    }

    public void setBase(boolean state) {
        writeBoolToPref(Constant.BASEACTIVE, state);
    }

    public int getVirtualizerProgress() {
        return getInt(Constant.VIRTUALIZERPROGRESS);
    }

    public void setVirtualizerProgress(int progress) {
        writeIntToPref(Constant.VIRTUALIZERPROGRESS, progress);
    }

    public boolean getVirtualizer() {
        return getBoolean(Constant.VIRTUALIZERACTIVE);
    }

    public void setVirtualizer(boolean state) {
        writeBoolToPref(Constant.VIRTUALIZERACTIVE, state);
    }

    public int getEqualizerPreset() {
        return getInt(Constant.EQUALIZERPRESET);
    }

    public void setEqualizerPreset(int preset) {
        writeIntToPref(Constant.EQUALIZERPRESET, preset);
    }

    public boolean getEqualizer() {
        return getBoolean(Constant.EQUALIZER);
    }

    public void setEqualizer(boolean state) {
        writeBoolToPref(Constant.EQUALIZER, state);
    }

    public HashMap<String, Integer> getEqualizerBand() {
        String json = getString(Constant.EQUALIZERBAND, "");
        Type type = new TypeToken<HashMap<String, Integer>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setEqualizerBand(HashMap<String, Integer> value) {
        gson = new Gson();
        String json = gson.toJson(value);
        writeStringToPref(Constant.EQUALIZERBAND, json);
    }

    public void setRateUsPressed(Boolean value) {
        writeBoolToPref(PRESS_RATE_US, value);
    }

    public Boolean isRateUsPressed() {
        return getBoolean(PRESS_RATE_US);
    }

    public void setNumberOfUses(int number) {
        writeIntToPref(Constant.SET_NUMBER_USES, number);
    }

    public int getNumberOfUses() {
        return getInt(Constant.SET_NUMBER_USES);
    }

    public int getNumberOfSongPlayedAfterAds() {
        return getInt(Constant.SONG_PLAYED_AFTER_ADS);
    }

    public void setNumberOfSongPlayedAfterAds(int number) {
        writeIntToPref(Constant.SONG_PLAYED_AFTER_ADS, number);
    }

    public boolean canShowADS() {
        return getBoolean(Constant.ADS_FLAG);
    }

    public void setImageConstant(int constant) {
        writeIntToPref(Constant.CONSTANT, constant);
    }

    public int getImageConstant() {
        return getInt(Constant.CONSTANT);
    }

    public void setShowADS(boolean flag) {
        writeBoolToPref(Constant.ADS_FLAG, flag);
    }

    public void setSuffle(boolean suffle) {
        writeBoolToPref(Constant.SUFFLE, suffle);
    }

    public boolean getSuffle() {
        return getBoolean(Constant.SUFFLE);
    }

    public void setRepeat(int repeat) {
        writeIntToPref(Constant.REPEAT, repeat);
    }

    public int getRepeat() {
        return getInt(Constant.REPEAT);
    }

    public void setPlayFlag(boolean play) {
        writeBoolToPref(Constant.PLAY_FLAG, play);
    }

    public boolean getPlayFlag() {
        return getBoolean(Constant.PLAY_FLAG);
    }


    public boolean getFirstTime() {
        return getBoolean(Constant.FIRSTIME);
    }
    public void setFirstTime(boolean set) {
        writeBoolToPref(Constant.FIRSTIME, set);
    }
}
