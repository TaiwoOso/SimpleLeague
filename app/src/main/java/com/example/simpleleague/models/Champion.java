package com.example.simpleleague.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Champion")
public class Champion extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IMAGE_URL = "imageUrl";
    public static final String KEY_LORE = "lore";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public String getImageUrl() {
        return getString(KEY_IMAGE_URL);
    }

    public void setImageUrl(String imageUrl) {
        put(KEY_IMAGE_URL, imageUrl);
    }

    public String getLore() {
        return getString(KEY_LORE);
    }

    public void setLore(String lore) {
        put(KEY_LORE, lore);
    }
}
