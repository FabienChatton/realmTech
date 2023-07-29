package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;

public class SoundManager extends Manager {
    @Wire(name = "context")
    RealmTech context;

    public final static String OPEN_INVENTORY = "sound/effects/inventory/cloth-inventory.wav";
    public final static String FOOT_STEP_GRASS_1 = "sound/effects/level/foot-step/sfx_step_grass_l.mp3";
    public final static String FOOT_STEP_GRASS_2 = "sound/effects/level/foot-step/sfx_step_grass_r.mp3";
    public final static String FOOT_STEP_WATER_1 = "sound/effects/level/foot-step/splash1.wav";
    public final static String FOOT_STEP_SAND_1 = "sound/effects/level/foot-step/Fantozzi-SandR1.ogg";
    public final static String ITEM_PICK_UP = "sound/effects/inventory/click.mp3";
    public final static String ITEM_DROP = "sound/effects/inventory/pop3.ogg";
    public final static String CELL_BREAK1 = "sound/effects/level/break/click_sound_1.mp3";
    public final static String CELL_BREAK2 = "sound/effects/level/break/click_sound_5.mp3";
    public float soundVolume = 1f;
    private HashMap<String, Long> soundLoop;
    private AssetManager assetManager;

    @Override
    protected void initialize() {
        super.initialize();
        assetManager = context.getAssetManager();
        soundLoop = new HashMap<>();
        soundLoop.put("footStep", System.currentTimeMillis());
        soundLoop.put("cellBreak", System.currentTimeMillis());
    }

    public void playOpenInventory() {
        assetManager.get(OPEN_INVENTORY, Sound.class).play(soundVolume - 0.5f);
    }

    public static void initAsset(AssetManager assetManager) {
        assetManager.load(OPEN_INVENTORY, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_1, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_2, Sound.class);
        assetManager.load(FOOT_STEP_WATER_1, Sound.class);
        assetManager.load(FOOT_STEP_SAND_1, Sound.class);
        assetManager.load(ITEM_PICK_UP, Sound.class);
        assetManager.load(ITEM_DROP, Sound.class);
        assetManager.load(CELL_BREAK1, Sound.class);
        assetManager.load(CELL_BREAK2, Sound.class);
    }

    public void playFootStep(Sound playerWalkSound, float volume) {
        if (System.currentTimeMillis() - soundLoop.get("footStep") >= 300) {
            soundLoop.put("footStep", System.currentTimeMillis());
            playerWalkSound.play(soundVolume * volume);
        }
    }

    public void playItemPickUp() {
        assetManager.get(ITEM_PICK_UP, Sound.class).play(soundVolume);
    }

    public void playItemDrop() {
        assetManager.get(ITEM_DROP, Sound.class).play(soundVolume);
    }

    public boolean playCellBreak() {
        if (System.currentTimeMillis() - soundLoop.get("cellBreak") >= 300) {
            soundLoop.put("cellBreak", System.currentTimeMillis());
            int random = MathUtils.random(0, 1);
            switch (random) {
                case 0 -> assetManager.get(CELL_BREAK1, Sound.class).play(1);
                case 1 -> assetManager.get(CELL_BREAK2, Sound.class).play(1);
            }
            return true;
        } else {
            return false;
        }
    }
}
