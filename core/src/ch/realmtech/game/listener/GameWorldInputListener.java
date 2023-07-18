package ch.realmtech.game.listener;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InfMapComponent;
import ch.realmtech.game.ecs.system.ItemBarManager;
import ch.realmtech.game.ecs.system.MapSystem;
import ch.realmtech.input.InputMapper;
import ch.realmtech.observer.Subcriber;
import ch.realmtech.screen.ScreenType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class GameWorldInputListener implements Subcriber<InputMapper.PointerMapper> {
    private final RealmTech context;

    public GameWorldInputListener(RealmTech context) {
        this.context = context;
    }

    @Override
    public void receive(InputMapper.PointerMapper pointerMapper) {
        if (context.getScreen().getClass() == ScreenType.GAME_SCREEN.screenClass) {
            // supprime ou place cellule sur la carte
            if (pointerMapper.isPressed) {
                Vector3 gameCoordinate = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                if (pointerMapper.pointer == 0) {
                    if (pointerMapper.button == InputMapper.leftClick.button) {
                        context.getSystem(MapSystem.class).breakTopCell(context.getEcsEngine().getPlayerId(), pointerMapper.button, context.getEcsEngine().getWorld().getMapper(InfMapComponent.class).get(context.getEcsEngine().getMapId()).infChunks, gameCoordinate.x, gameCoordinate.y);
                    }
                    if (pointerMapper.button == InputMapper.rightClick.button) {
                        context.getSystem(MapSystem.class).interagieClickDroit(context.getEcsEngine().getPlayerId(), pointerMapper.button, context.getEcsEngine().getWorld().getMapper(InfMapComponent.class).get(context.getEcsEngine().getMapId()).infChunks, gameCoordinate.x, gameCoordinate.y, context.getSystem(ItemBarManager.class).getSelectItem());
                    }
                }
            }
        }
    }
}
