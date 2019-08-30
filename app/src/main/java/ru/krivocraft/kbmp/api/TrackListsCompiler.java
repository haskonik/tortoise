package ru.krivocraft.kbmp.api;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.krivocraft.kbmp.Track;
import ru.krivocraft.kbmp.TrackList;
import ru.krivocraft.kbmp.Tracks;
import ru.krivocraft.kbmp.constants.Constants;
import ru.krivocraft.kbmp.tasks.compilers.CompileByAuthorTask;
import ru.krivocraft.kbmp.tasks.compilers.CompileByTagsTask;
import ru.krivocraft.kbmp.tasks.compilers.CompileFavoritesTask;
import ru.krivocraft.kbmp.tasks.compilers.CompileTrackListsTask;

public class TrackListsCompiler {
    private Context context;

    public TrackListsCompiler(Context context) {
        this.context = context;
    }

    public void compileByAuthors(OnTrackListsCompiledCallback callback) {
        CompileByAuthorTask task = new CompileByAuthorTask();
        compile(task, Constants.TRACK_LIST_BY_AUTHOR, callback);
    }

    public void compileFavorites(OnTrackListsCompiledCallback callback) {
        CompileFavoritesTask task = new CompileFavoritesTask();
        compile(task, Constants.TRACK_LIST_CUSTOM, callback);
    }

    public void compileByTags(OnTrackListsCompiledCallback callback) {
        CompileByTagsTask task = new CompileByTagsTask();
        compile(task, Constants.TRACK_LIST_BY_TAG, callback);
    }

    private void compile(CompileTrackListsTask task, int trackListType, OnTrackListsCompiledCallback callback) {
        List<TrackList> list = new LinkedList<>();
        task.setListener(trackLists -> new Thread(() -> parseMap(callback, list, trackLists, trackListType)).start());
        task.execute(Tracks.getTrackStorage(context).toArray(new Track[0]));
    }

    private void parseMap(OnTrackListsCompiledCallback callback, List<TrackList> list, Map<String, List<Track>> trackLists, int trackListByTag) {
        for (Map.Entry<String, List<Track>> entry : trackLists.entrySet()) {
            TrackList trackList = new TrackList(entry.getKey(), Tracks.getReferences(context, entry.getValue()), trackListByTag);
            list.add(trackList);
        }
        callback.onTrackListsCompiled(list);
    }

}