package ru.krivocraft.kbmp.tasks.compilers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.krivocraft.kbmp.Track;
import ru.krivocraft.kbmp.constants.Constants;

public class CompileFavoritesTask extends CompileTrackListsTask {

    @Override
    protected Map<String, List<Track>> doInBackground(Track... source) {
        Map<String, List<Track>> trackLists = new HashMap<>();
        List<Track> favorites = new ArrayList<>();
        for (Track track : source) {
            if (track.isLiked()) {
                favorites.add(track);
            }
        }
        if (favorites.size() > 0) {
            trackLists.put(Constants.FAVORITES_DISPLAY_NAME, favorites);
        }
        return trackLists;
    }
}
