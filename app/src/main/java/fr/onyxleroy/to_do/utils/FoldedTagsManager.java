package fr.onyxleroy.to_do.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FoldedTagsManager {
    private static final String PREFS_NAME = "folded_tags_prefs";
    private static final String KEY_FOLDED_TAGS = "folded_tags";

    public static Set<String> loadFoldedTags(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return new HashSet<>(prefs.getStringSet(KEY_FOLDED_TAGS, new HashSet<>()));
    }

    public static void saveFoldedTags(Context context, Set<String> foldedTagIds) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putStringSet(KEY_FOLDED_TAGS, foldedTagIds).apply();
    }

    public static void toggleFoldedTag(Context context, String tagId) {
        Set<String> foldedTags = loadFoldedTags(context);
        if (foldedTags.contains(tagId)) {
            foldedTags.remove(tagId);
        } else {
            foldedTags.add(tagId);
        }
        saveFoldedTags(context, foldedTags);
    }

    public static void setFolded(Context context, String tagId, boolean folded) {
        Set<String> foldedTags = loadFoldedTags(context);
        if (folded) {
            foldedTags.add(tagId);
        } else {
            foldedTags.remove(tagId);
        }
        saveFoldedTags(context, foldedTags);
    }

    public static boolean isFolded(Context context, String tagId) {
        return loadFoldedTags(context).contains(tagId);
    }
}
