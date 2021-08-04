package com.example.simpleleague.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = Champion.class)
@ParseClassName("Champion")
public class Champion extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IMAGE_URL = "imageUrl";
    public static final String KEY_LORE = "lore";
    public static final String KEY_Q_NAME = "QName";
    public static final String KEY_Q_IMAGE_URL = "QImageUrl";
    public static final String KEY_W_NAME = "WName";
    public static final String KEY_W_IMAGE_URL = "WImageUrl";
    public static final String KEY_E_NAME = "EName";
    public static final String KEY_E_IMAGE_URL = "EImageUrl";
    public static final String KEY_R_NAME = "RName";
    public static final String KEY_R_IMAGE_URL = "RImageUrl";
    public static final String KEY_P_NAME = "PName";
    public static final String KEY_P_IMAGE_URL = "PImageUrl";

    // empty constructor needed by the Parceler library
    public Champion() {}

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

    public String getQName() {
        return getString(KEY_Q_NAME);
    }

    public void setQName(String QName) {
        put(KEY_Q_NAME, QName);
    }

    public String getQImageUrl() {
        return getString(KEY_Q_IMAGE_URL);
    }

    public void setQImageUrl(String QImageUrl) {
        put(KEY_Q_IMAGE_URL, QImageUrl);
    }

    public String getWName() {
        return getString(KEY_W_NAME);
    }

    public void setWName(String WName) {
        put(KEY_W_NAME, WName);
    }

    public String getWImageUrl() {
        return getString(KEY_W_IMAGE_URL);
    }

    public void setWImageUrl(String WImageUrl) {
        put(KEY_W_IMAGE_URL, WImageUrl);
    }

    public String getEName() {
        return getString(KEY_E_NAME);
    }

    public void setEName(String EName) {
        put(KEY_E_NAME, EName);
    }

    public String getEImageUrl() {
        return getString(KEY_E_IMAGE_URL);
    }

    public void setEImageUrl(String EImageUrl) {
        put(KEY_E_IMAGE_URL, EImageUrl);
    }

    public String getRName() {
        return getString(KEY_R_NAME);
    }

    public void setRName(String RName) {
        put(KEY_R_NAME, RName);
    }

    public String getRImageUrl() {
        return getString(KEY_R_IMAGE_URL);
    }

    public void setRImageUrl(String RImageUrl) {
        put(KEY_R_IMAGE_URL, RImageUrl);
    }

    public String getPName() {
        return getString(KEY_P_NAME);
    }

    public void setPName(String PName) {
        put(KEY_P_NAME, PName);
    }

    public String getPImageUrl() {
        return getString(KEY_P_IMAGE_URL);
    }

    public void setPImageUrl(String PImageUrl) {
        put(KEY_P_IMAGE_URL, PImageUrl);
    }
}