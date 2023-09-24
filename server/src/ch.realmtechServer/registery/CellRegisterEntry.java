package ch.realmtechServer.registery;

import ch.realmtechServer.anotation.Lazy;
import ch.realmtechServer.level.cell.CellBehavior;
import ch.realmtechServer.mod.RealmTechCoreMod;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.List;

public class CellRegisterEntry implements Entry<CellRegisterEntry> {
    @Lazy(champSource = "textureRegionName")
    private TextureRegion textureRegion;
    private final String textureRegionName;
    private final CellBehavior cellBehavior;
    private Registry<CellRegisterEntry> registry;


    public static CellRegisterEntry getCellModAndCellHash(int cellRegisterHash) {
        final List<String> enfantsId = RealmTechCoreMod.CELLS.getEnfantsId();
        for (String id : enfantsId) {
            int keyHash = hashString(id);
            if (keyHash == cellRegisterHash) {
                return RealmTechCoreMod.CELLS.get(id).getEntry();
            }
        }
        return RealmTechCoreMod.GRASS_CELL;
//        throw new IllegalArgumentException("Aucun registre ne correspond au hash " + cellRegisterHash + ". La carte a été corrompue");
    }

    public static int getHash(CellRegisterEntry cellRegisterEntry) {
        int ret = -1;
        for (String id : RealmTechCoreMod.CELLS.getEnfantsId()) {
            if (RealmTechCoreMod.CELLS.get(id).getEntry() == cellRegisterEntry) {
                ret = id.hashCode();
                break;
            }
        }
        return ret;
    }

    public static int hashString(String s) {
        return s.hashCode();
    }

    public TextureRegion getTextureRegion(TextureAtlas textureAtlas) {
        if (textureRegion == null) {
            textureRegion = textureAtlas.findRegion(textureRegionName);
        }
        return textureRegion;
    }

    public CellBehavior getCellBehavior() {
        return cellBehavior;
    }

    public CellRegisterEntry(String textureRegionName, CellBehavior cellBehavior) {
        this.textureRegionName = textureRegionName;
        this.cellBehavior = cellBehavior;
    }

    @Override
    public String toString() {
        return registry != null ? findRegistryEntryToString(registry) : textureRegionName;
    }

    @Override
    public void setRegistry(Registry<CellRegisterEntry> registry) {
        this.registry = registry;
    }
}
