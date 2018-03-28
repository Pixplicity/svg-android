package com.larvalabs.androidify.wallpaper;

import android.content.res.AssetManager;
import android.content.res.Resources;

import com.pixplicity.sharp.Sharp;
import com.pixplicity.sharp.SharpPicture;
import com.pixplicity.sharp.SvgParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static com.larvalabs.androidify.wallpaper.Constants.HAIR_COLORS;
import static com.larvalabs.androidify.wallpaper.Constants.MAX_HEAD_RATIO;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_ARMS_MAX_X;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_ARMS_MAX_Y;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_ARMS_MIN_X;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_ARMS_MIN_Y;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_BODY_MAX_X;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_BODY_MAX_Y;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_BODY_MIN_X;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_BODY_MIN_Y;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_HEAD_MAX_X;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_HEAD_MAX_Y;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_HEAD_MIN_X;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_HEAD_MIN_Y;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_LEGS_MAX_X;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_LEGS_MAX_Y;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_LEGS_MIN_X;
import static com.larvalabs.androidify.wallpaper.Constants.RESIZE_LEGS_MIN_Y;
import static com.larvalabs.androidify.wallpaper.Constants.SKIN_COLORS;

/**
 * Responsible for maintaining the list of available assets (clothes, hair, etc.), as well as loading the
 * corresponding SVG files.
 *
 * @author Larva Labs, LLC
 */
public class AssetDatabase {

    /**
     * Random number generator.
     */
    private static final Random RANDOM = new Random();

    public static final String ASSET_HAIR = "hair";
    public static final String ASSET_SHIRT = "shirt";
    public static final String ASSET_PANTS = "pants";
    public static final String ASSET_SHOES = "shoes";
    public static final String ASSET_GLASSES = "glasses";
    public static final String ASSET_BEARD = "beard";
    public static final String ASSET_ACCESSORIES = "accessories";

    public static final String SHIRT_ARM = "arm";
    public static final String SHIRT_BODY = "body";
    public static final String SHIRT_TOP = "top";
    public static final String PANTS_LEG = "leg";
    public static final String PANTS_TOP = "top";
    public static final String HAIR_BACK = "back";
    public static final String HAIR_FRONT = "front";

    private ArrayList<String> hairAssets;
    private ArrayList<String> shirtAssets;
    private ArrayList<String> pantsAssets;
    private ArrayList<String> shoeAssets;
    private ArrayList<String> glassesAssets;
    private ArrayList<String> beardAssets;
    private ArrayList<Accessory> accessoryAssets;

    private AssetManager assetManager;
    private Resources resources;

    /**
     * Construct a new asset database.
     *
     * @param assetManager the Android application asset manager.
     * @param resources    the Android application resources database.
     */
    public AssetDatabase(AssetManager assetManager, Resources resources) {
        this.assetManager = assetManager;
        this.resources = resources;
        try {
            scanAssets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Scans and builds lists of all available assets, by type.
     *
     * @throws IOException if something goes wrong with the filesystem during the scan.
     */
    private void scanAssets() throws IOException {
        if (hairAssets == null) {
            hairAssets = new ArrayList<>(loadElements(ASSET_HAIR));
            Collections.sort(hairAssets);
        }
        if (shirtAssets == null) {
            shirtAssets = new ArrayList<>(loadElements(ASSET_SHIRT));
            Collections.sort(shirtAssets);
        }
        if (pantsAssets == null) {
            pantsAssets = new ArrayList<>(loadElements(ASSET_PANTS));
            Collections.sort(pantsAssets);
        }
        if (shoeAssets == null) {
            shoeAssets = new ArrayList<>(loadElements(ASSET_SHOES));
            Collections.sort(shoeAssets);
        }
        if (glassesAssets == null) {
            glassesAssets = new ArrayList<>(loadElements(ASSET_GLASSES));
            Collections.sort(glassesAssets);
        }
        if (beardAssets == null) {
            beardAssets = new ArrayList<>(loadElements(ASSET_BEARD));
            Collections.sort(beardAssets);
        }
        if (accessoryAssets == null) {
            accessoryAssets = new ArrayList<>(loadAccessories(ASSET_ACCESSORIES));
            Collections.sort(accessoryAssets);
        }
    }

    /**
     * Builds up a list of accessories.
     *
     * @param path where to scan for accessories on the filesystem.
     * @return the set of accessories.
     * @throws IOException if something goes wrong with the filesystem during the scan.
     */
    private HashSet<Accessory> loadAccessories(String path) throws IOException {
        HashSet<Accessory> elements = new HashSet<>();
        String[] files = assetManager.list(path);
        int idx = 0;
        for (String file : files) {
            int breakIndex = file.lastIndexOf('_');
            if (breakIndex == -1) {
                Util.debug("Invalid accessory asset found (no '_'): '" + path + "/" + file + "'.");
                continue;
            }
            int endIndex = file.lastIndexOf('.');
            if (endIndex == -1) {
                Util.debug("** Malformed file in assets: " + path + "/" + file);
                continue;
            } else {
                String s = file.substring(0, breakIndex);
                String type = file.substring(breakIndex + 1, endIndex);
                Util.debug("** Adding: " + s + " of type " + type + ".");
                elements.add(new Accessory(idx, s, type));
            }
            idx++;
        }
        return elements;
    }

    /**
     * Load a list of assets of a type.
     *
     * @param path the path containing the assets.
     * @return a list of the asset names.
     * @throws IOException if something goes wrong with the filesystem during the scan.
     */
    private HashSet<String> loadElements(String path) throws IOException {
        HashSet<String> elements = new HashSet<>();
        String[] files = assetManager.list(path);
        for (String file : files) {
            int breakIndex = file.lastIndexOf('_');
            if (breakIndex == -1) {
                breakIndex = file.lastIndexOf('.');
            }
            if (breakIndex == -1) {
                Util.debug("** Malformed file in assets: " + path + "/" + file);
            } else {
                String s = file.substring(0, breakIndex);
                elements.add(s);
            }
        }
        return elements;
    }

    /**
     * Loads an SVG file for a given asset.
     * @param path the path to load the SVG file.
     * @param name the name of the SVG file.
     * @param suffix the suffix, if necessary (ie. hair has _top and _back SVG layers).
     * @return the parsed SVG object.
     */
    public SharpPicture getSVGForAsset(String path, String name, String suffix) {
        return getSVGForAsset(path, name, suffix, null, null);
    }

    /**
     * Loads an SVG file for a given asset.
     *
     * @param path   the path to load the SVG file.
     * @param name   the name of the SVG file.
     * @param suffix the suffix, if necessary (ie. hair has _top and _back SVG layers).
     * @return the parsed SVG object.
     */
    public SharpPicture getSVGForAsset(String path, String name, String suffix, Integer searchColor, Integer replaceColor) {
        String file = path + "/" + name;
        if (suffix != null) {
            file += "_" + suffix;
        }
        file += ".svg";
        try {
            Sharp sharp = Sharp.loadAsset(assetManager, file);
            if (searchColor != null) {
//                sharp.addColorReplacement(searchColor, replaceColor);
            }
            return sharp.getSharpPicture();
        } catch (SvgParseException e) {
            return null;
        }
    }

    /**
     * Loads an SVG file for a given resource.
     * @param resource the resource ID.
     * @return the parsed SVG object.
     */
    public SharpPicture getSVGForResource(int resource) {
        return getSVGForResource(resource, null, null);
    }

    /**
     * Loads an SVG file for a given resource.
     * @param resource the resource ID.
     * @return the parsed SVG object.
     * @param searchColor optionally a color to replace.
     * @param replaceColor the replacement color.
     */
    public SharpPicture getSVGForResource(int resource, Integer searchColor, Integer replaceColor) {
        try {
            Sharp sharp = Sharp.loadResource(resources, resource);
            if (searchColor != null) {
//                sharp.addColorReplacement(searchColor, replaceColor);
            }
            return sharp.getSharpPicture();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a list of all accessory assets.
     */
    public List<Accessory> getAccessoryAssets() {
        return accessoryAssets;
    }

    /**
     * Loads the SVG for an accessory.
     *
     * @param a the accessory.
     * @return the parsed SVG object.
     */
    public SharpPicture loadAccessory(Accessory a) {
        return getSVGForAsset(ASSET_ACCESSORIES, a.getName(), a.getType());
    }

    private float getRandomScale(float minValue, float maxValue) {
        return minValue + RANDOM.nextFloat() * (maxValue - minValue);
    }

    /**
     * Creates a random android configuration.
     *
     * @return the randomized android config.
     */
    public AndroidConfig getRandomConfig() {
        AndroidConfig config = new AndroidConfig();
        if (RANDOM.nextInt(100) < 25) {
            config.setBeard(beardAssets.get(RANDOM.nextInt(beardAssets.size())));
        }
        if (RANDOM.nextInt(100) < 33) {
            config.setGlasses(glassesAssets.get(RANDOM.nextInt(glassesAssets.size())));
        }
        config.setShirt(shirtAssets.get(RANDOM.nextInt(shirtAssets.size())));
        config.setHair(hairAssets.get(RANDOM.nextInt(hairAssets.size())));
        config.setPants(pantsAssets.get(RANDOM.nextInt(pantsAssets.size())));
        config.setShoes(shoeAssets.get(RANDOM.nextInt(shoeAssets.size())));
        config.setBodyScaleX(getRandomScale(RESIZE_BODY_MIN_X, RESIZE_BODY_MAX_X));
        config.setBodyScaleY(getRandomScale(RESIZE_BODY_MIN_Y, RESIZE_BODY_MAX_Y));
        config.setLegScaleX(getRandomScale(RESIZE_LEGS_MIN_X, RESIZE_LEGS_MAX_X));
        config.setLegScaleY(getRandomScale(RESIZE_LEGS_MIN_Y, RESIZE_LEGS_MAX_Y));
        // Constrain legs so they don't go outside of body
        if (config.getLegScaleX() > config.getBodyScaleX()) {
            config.setLegScaleX(config.getBodyScaleX());
        }
        float headX = getRandomScale(RESIZE_HEAD_MIN_X, RESIZE_HEAD_MAX_X);
        float headY = getRandomScale(RESIZE_HEAD_MIN_Y, RESIZE_HEAD_MAX_Y);
        // Constrain width/height ratio so it isn't too extreme
        if (headX / headY > MAX_HEAD_RATIO) {
            headY = headX / MAX_HEAD_RATIO;
        } else if (headY / headX > MAX_HEAD_RATIO) {
            headX = headY / MAX_HEAD_RATIO;
        }
        config.setHeadScaleX(headX);
        config.setHeadScaleY(headY);

        config.setArmScaleX(getRandomScale(RESIZE_ARMS_MIN_X, RESIZE_ARMS_MAX_X));
        config.setArmScaleY(getRandomScale(RESIZE_ARMS_MIN_Y, RESIZE_ARMS_MAX_Y));
        config.setSkinColor(SKIN_COLORS[RANDOM.nextInt(SKIN_COLORS.length)]);
        config.setHairColor(HAIR_COLORS[RANDOM.nextInt(HAIR_COLORS.length)]);
        while (RANDOM.nextInt(100) < 25) {
            config.addAccessory(accessoryAssets.get(RANDOM.nextInt(accessoryAssets.size())));
        }
        return config;
    }

}
