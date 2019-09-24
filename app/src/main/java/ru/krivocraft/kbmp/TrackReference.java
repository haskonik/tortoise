package ru.krivocraft.kbmp;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Objects;

public class TrackReference {

    private int value;

    public TrackReference(int value) {
        this.value = value;
    }

    TrackReference(Track track) {
        this.value = track.getIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackReference reference = (TrackReference) o;
        return value == reference.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public int getValue() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    String toJson() {
        return new Gson().toJson(this);
    }

    static TrackReference fromJson(String in) {
        return new Gson().fromJson(in, TrackReference.class);
    }
}
