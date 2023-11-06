package com.mp3.musicplayer.Utils;

import android.content.Context;

import com.mp3.musicplayer.BuildConfig;
import com.mp3.musicplayer.Model.AllSong;
import com.mp3.musicplayer.Model.Artist_Genrs_Album;
import com.mp3.musicplayer.Model.Song;

import java.util.ArrayList;
import java.util.List;

public class Constant {

    public static final int ADD_INTERVAL = 30;
    public static final String PREFERENCE = BuildConfig.APPLICATION_ID + ".MUSIC_PLAYER.PREFERENCE";
    public static final String CUSTOMEQUALIZERNAME = "Custom";
    //some constant for shared preference
    public static final String PLAYLIST_MENU = "PLAYLIST_MENU";
    public static final String LAST_PLAYED_MENU = "LAST_PLAYED_MENU";
    public final static String LANGUAGE_STATE = "LANGUAGE_STATE";
    public static final String FIRST_CREATED = "FIRST_CREATED";
    public static final String SONG_PATH = "SONG_PATH";
    public static final String CURRENT_TIME = "CURRENT_TIME";
    public static final String BASSPROGRESS = "BASSPROGRESS";
    public static final String BASEACTIVE = "BASEACTIVE";
    public static final String VIRTUALIZERPROGRESS = "VIRTUALIZERPROGRESS";
    public static final String VIRTUALIZERACTIVE = "VIRTUALIZERACTIVE";
    public static final String EQUALIZERPRESET = "EQUALIZERPRESET";
    public static final String EQUALIZER = "EQUALIZER";
    public static final String EQUALIZERBAND = "EQUALIZERBAND";
    public static final String PLAYSTORELINK = "https://play.google.com/store/apps/details?id=";
    public static final String SHARETITTLE = "Share using";
    public static final String ALBUM_NAME = "ALBUM_NAME";
    public static final String TOTAL_TRACK_TEXT = "Total tracks: ";
    public static final String RECENTLY_ADDED="RECENTLY_ADDED";
    public static final String ISPLAYLIST="ISPLAYLIST";
    public static final String PLALISTNAME="PLALISTNAME";

    public static final String EQUALIZER_OFF="Equalizer OFF";
    public static final String EQUALIZER_ON="Equalizer ON";

    public static final String UNKNOWN="<unknown>";
    public static final String EXTERNAL="external";
    public static final String LAST_PLAYED_TEXT= "Last Played";
    public static final String RECENTLY_ADDED_TEXT="Recently Added";

    public static final String ALBUM_FAVOURITE="Favourite";
    public static final String ALBUM_HOME ="Home";
    public static final String ALBUM_OFFICE = "Office";
    public static final String ALBUM_TRAVEL="Travel";
    public static final String ALBUM_NOW_PLAYING= "Now Playing";
    public static final String ALBUM_CREATE_NEW="Create New";
    public static final String CAN_NOT_SET_FONT= "can not set font";

    public static final String SIMPLEDATEFORMATE="hh:mm:ss";
    public static final String TIMEZONE="UTC";

    public static final String BY="by ";
    public static final String SETDURATIONLABEL="0:00";

    public static final String SUFFLE_OFF="Shuffle is OFF";
    public static final String SUFFLE_ON="Shuffle is ON";

    public static final String REPEAT_ONE="Repeat one";
    public static final String REPEAT_ALL="Repeat all";
    public static final String REPEAT_OFF="Repeat none";

    public static final String PLAYER_NAME= "</b> on <b> Di Tune Music Player</b>";
    public static final String LISTHEN_TO_TEXT= "Listening to <b>";
    public static final String FILE_DELETED_TEXT= "File deleted.";

    public static final String CHANNEL_ID= "MUSIC_APP_NOTIFICATION_CHANNEL_ID";
    public static final String CHANNEL_NAME= "MUSIC_APP_NOTIFICATION_CHANNEL.";

    public static final String PRESS_RATE_US = "PRESS_RATE_US";

    public static final String MUSIC_APP_Tittle= "Music App image";
    public static final String SHAREIMAGE_TITTLE= "Share Image";

    public static final String CREAT_NEW_TEXT= "Create New";
    public static final String PERMISSION_DENY = "To continue you should provide permission.";
    public static final String SET_NUMBER_USES ="SET_NUMBER_USES" ;
    public static final String SONG_PLAYED_AFTER_ADS = "SONG_PLAYED_AFTER_ADS" ;
    public static final String RINGTONE_NOTE_SECCESSFULL = "Ringtone setting unsuccessful";
    public static final String RINGTONE_SECCESSFULL = "Successfully added as ringtone";
    public static final String RINGTONE_SETTING = "Please enable system setting and try again";
    public static final String APP_NAME = "Lalon Music Player" ;
    public static final String RECODED_ALBUM = "Recording";
    public static final String RECORDED_ARTIST = "Unknown";
    public static final String ALL_SONGS_PREF = "ALL_SONGS_PREF";
    public static final String ADS_FLAG = "ADS_FLAG";
    public static final String CONSTANT = "CONSTANT";
    public static final String SUFFLE = "SUFFLE";
    public static final String REPEAT = "REPEAT";
    public static final String FIRSTIME = "FIRSTTIME";
    public static final String ERROR_REVIEW = "Error in review, Please try again later";
    public static final String PLAYLIST_BLANK_INFO = "Currently no item in this list";
    public static int LANGUAGE_POSITION = 0;
    public static boolean equalizerON = false;
    public static String filterTime = "00:00";
    public static String songPath = "";
    public static String totalSong = "0";
    public static boolean firstCreated = true;
    public static int songPosition = 0;
    public static int photo = 0;
    public static int photosize=6;
    public static boolean nothingSelected = false;
    public static boolean languageChange = false;
    public static final String PLAY_FLAG = "PLAY_FLAG";
    //reapeat all for 0 for none, 1 for repeat once and 2 for repeat all
    public static int reapeatAll = 2;
    //this for check the song is coming from song menu
    public static boolean comeFromSongMenu = false;
    //In this list all the song id, and other information are held this excute in main activity
    public static List<AllSong> allSongs = new ArrayList<AllSong>();
    //this is for current playlist which for now playing save them
    public static List<Song> currentPlaylist = new ArrayList<Song>();
    //this is for playlist name
    public static List<Artist_Genrs_Album> name_playlist = new ArrayList<Artist_Genrs_Album>();


    public static boolean musicPlaying = false;

    //this action is for notification button and control
    public interface ACTION {
        public static String MAIN_ACTION = "com.mp3.dj.music.player.action.main";
        public static String INIT_ACTION = "com.mp3.dj.music.player.action.init";
        public static String PREV_ACTION = "com.mp3.dj.music.player.action.prev";
        public static String PLAY_ACTION = "com.mp3.dj.music.player.action.play";
        public static String NEXT_ACTION = "com.mp3.dj.music.player.action.next";
        public static String STARTFOREGROUND_ACTION = "com.mp3.dj.music.player.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.mp3.dj.music.player.action.stopforeground";
        public static String CLOSE_APP = "com.mp3.dj.music.player.action.closeapp";

    }


    //the notification id
    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    /*
    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.round_image, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }
*/

    public static void incConstant(Context context) {
        Constant.photo=PreferenceManager.getInstance(context).getImageConstant();
        Constant.photo++;
        if (Constant.photo >= Constant.photosize) {
            Constant.photo = 0;
        }
        PreferenceManager.getInstance(context).setImageConstant(Constant.photo);
    }

}
