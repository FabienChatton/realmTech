package ch.realmtech.game.registery;

import ch.realmtech.RealmTech;
import ch.realmtech.game.level.cell.CellBehavior;
import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.helper.SetContext;
import com.artemis.EntityEdit;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.List;
import java.util.function.Consumer;

public class CellRegisterEntry implements SetContext, Entry {
    private final TextureRegion textureRegion;
    private final CellBehavior cellBehavior;
    public static RealmTech context;
    private Consumer<EntityEdit> editEntity;

//    /**
//     * Trouve un registre via le hash le nom de son mod + le nom de la cellule.
//     *
//     * @param cellRegisterHash La hash de clé (mod + nom cellule) que l'ont souhait connaitre le registre.
//     * @return Le registre qui correspond au hash sinon null
//     */
//    public static CellRegisterEntry getCellModAndCellHash(int cellRegisterHash) {
//        CellRegisterEntry ret = null;
//        for (String id : RealmTechCoreMod.CELLS.getEnfantsId()) {
//            int keyHash = id.hashCode();
//            if (keyHash == cellRegisterHash) {
//                ret = RealmTechCoreMod.CELLS.get(id).getEntry();
//                break;
//            }
//        }
//        return ret;
//    }

    public static CellRegisterEntry getCellModAndCellHash(int cellRegisterHash) {
        final List<String> enfantsId = RealmTechCoreMod.CELLS.getEnfantsId();
        for (String id : enfantsId) {
            int keyHash = hashString(id);
            if (keyHash == cellRegisterHash) {
                return RealmTechCoreMod.CELLS.get(id).getEntry();
            }
        }
        throw new IllegalArgumentException("Aucun registre ne correspond au hash " + cellRegisterHash + ". La carte a été corrompue");
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

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public CellBehavior getCellBehavior() {
        return cellBehavior;
    }

    public Consumer<EntityEdit> getEditEntity() {
        return editEntity;
    }

    public CellRegisterEntry(String textureRegionName, CellBehavior cellBehavior) {
        this.textureRegion = context.getTextureAtlas().findRegion(textureRegionName);
        this.cellBehavior = cellBehavior;
    }

    public CellRegisterEntry(Consumer<EntityEdit> editEntity, String textureRegionName, CellBehavior cellBehavior) {
        this(textureRegionName, cellBehavior);
        this.editEntity = editEntity;
    }
}
