package ru.krivocraft.kbmp;

import android.content.Context;
import android.content.SharedPreferences;

import ru.krivocraft.kbmp.constants.Constants;

import static android.content.Context.MODE_PRIVATE;

public class OldStuffCollector {
    private Context context;

    public OldStuffCollector(Context context) {
        this.context = context;
    }

    public void execute() {
        removeOldCache();
    }

    private void removeOldCache() {
        SharedPreferences preferences = context.getSharedPreferences(Constants.STORAGE_TRACK_LISTS, MODE_PRIVATE);
        String identifier = "all_tracks";
        if (preferences.getString(identifier, null) != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(identifier);
            editor.apply();
        }
        SharedPreferences settings = context.getSharedPreferences(Constants.STORAGE_SETTINGS, MODE_PRIVATE);
        if (Utils.getOption(settings, Constants.KEY_OLD_TRACK_LISTS_EXIST, true)) {
            Utils.clearCache(preferences);
            Utils.putOption(settings, Constants.KEY_OLD_TRACK_LISTS_EXIST, false);
        }
    }

}