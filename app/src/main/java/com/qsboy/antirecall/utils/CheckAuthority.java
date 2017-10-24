package com.qsboy.antirecall.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.util.Log;

import java.lang.reflect.Method;

/*
 * Created by JasonQS
*/
@SuppressWarnings("unused")

public class CheckAuthority {

    private static String TAG = "check";

    private Context context;

    public CheckAuthority(Context context) {
        this.context = context;
    }

    public boolean checkAlertWindowPermission() {
        return checkOp(context, OP_SYSTEM_ALERT_WINDOW);
    }

    private boolean checkOp(Context context, int op) {
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            Method method = AppOpsManager.class.getDeclaredMethod("checkOp", int.class, int.class, String.class);
            return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }


    // No operation specified.
    int OP_NONE = -1;

    //Access to coarse location information.
    int OP_COARSE_LOCATION = 0;

    // Access to fine location information.
    int OP_FINE_LOCATION = 1;

    // Causing GPS to run.
    int OP_GPS = 2;

    int OP_VIBRATE = 3;

    int OP_READ_CONTACTS = 4;

    int OP_WRITE_CONTACTS = 5;

    int OP_READ_CALL_LOG = 6;

    int OP_WRITE_CALL_LOG = 7;

    int OP_READ_CALENDAR = 8;

    int OP_WRITE_CALENDAR = 9;

    int OP_WIFI_SCAN = 10;

    int OP_POST_NOTIFICATION = 11;

    int OP_NEIGHBORING_CELLS = 12;

    int OP_CALL_PHONE = 13;

    int OP_READ_SMS = 14;

    int OP_WRITE_SMS = 15;

    int OP_RECEIVE_SMS = 16;

    int OP_RECEIVE_EMERGECY_SMS = 17;

    int OP_RECEIVE_MMS = 18;

    int OP_RECEIVE_WAP_PUSH = 19;

    int OP_SEND_SMS = 20;

    int OP_READ_ICC_SMS = 21;

    int OP_WRITE_ICC_SMS = 22;

    int OP_WRITE_SETTINGS = 23;

    int OP_SYSTEM_ALERT_WINDOW = 24;

    int OP_ACCESS_NOTIFICATIONS = 25;

    int OP_CAMERA = 26;

    int OP_RECORD_AUDIO = 27;

    int OP_PLAY_AUDIO = 28;

    int OP_READ_CLIPBOARD = 29;

    int OP_WRITE_CLIPBOARD = 30;

    int OP_TAKE_MEDIA_BUTTONS = 31;

    int OP_TAKE_AUDIO_FOCUS = 32;

    int OP_AUDIO_MASTER_VOLUME = 33;

    int OP_AUDIO_VOICE_VOLUME = 34;

    int OP_AUDIO_RING_VOLUME = 35;

    int OP_AUDIO_MEDIA_VOLUME = 36;

    int OP_AUDIO_ALARM_VOLUME = 37;

    int OP_AUDIO_NOTIFICATION_VOLUME = 38;

    int OP_AUDIO_BLUETOOTH_VOLUME = 39;

    int OP_WAKE_LOCK = 40;
    // Continually monitoring location data.
    int OP_MONITOR_LOCATION = 41;
    // Continually monitoring location data with a relatively high power request.
    int OP_MONITOR_HIGH_POWER_LOCATION = 42;
    // Retrieve current usage stats via {@link UsageStatsManager}.
    int OP_GET_USAGE_STATS = 43;

    int OP_MUTE_MICROPHONE = 44;

    int OP_TOAST_WINDOW = 45;

    // Capture the device's display contents and/or audio
    int OP_PROJECT_MEDIA = 46;

    // Activate a VPN connection without user intervention.
    int OP_ACTIVATE_VPN = 47;

    // Access the WallpaperManagerAPI to write wallpapers.
    int OP_WRITE_WALLPAPER = 48;

    // Received the assist structure from an app.
    int OP_ASSIST_STRUCTURE = 49;

    // Received a screenshot from assist.
    int OP_ASSIST_SCREENSHOT = 50;

    // Read the phone state.
    int OP_READ_PHONE_STATE = 51;

    // Add voicemail messages to the voicemail content provider.
    int OP_ADD_VOICEMAIL = 52;

    // Access APIs for SIP calling over VOIP or WiFi.
    int OP_USE_SIP = 53;

    // Intercept outgoing calls.
    int OP_PROCESS_OUTGOING_CALLS = 54;

    // User the fingerprint API.
    int OP_USE_FINGERPRINT = 55;

    // Access to body sensors such as heart rate, etc.
    int OP_BODY_SENSORS = 56;

    // Read previously received cell broadcast messages.
    int OP_READ_CELL_BROADCASTS = 57;

    // Inject mock location into the system.
    int OP_MOCK_LOCATION = 58;

    // Read external storage.
    int OP_READ_EXTERNAL_STORAGE = 59;

    // Write external storage.
    int OP_WRITE_EXTERNAL_STORAGE = 60;

    // Turned on the screen.
    int OP_TURN_SCREEN_ON = 61;

    // Get device accounts.
    int OP_GET_ACCOUNTS = 62;

    // Control whether an application is allowed to run in the background.
    int OP_RUN_IN_BACKGROUND = 63;

    int _NUM_OP = 64;
}
