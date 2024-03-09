package ch.realmtech.core.game.ecs.system;

import ch.realmtech.server.ecs.component.MouvementComponent;
import ch.realmtech.server.ecs.component.TextureAnimationComponent;
import ch.realmtech.server.ecs.component.TextureComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static ch.realmtech.server.ecs.system.PlayerMouvementSystemServer.*;

@All({TextureComponent.class, TextureAnimationComponent.class, MouvementComponent.class})
public class FourDirectionTextureAnimated extends IteratingSystem {
    @Wire(name = "gameStage")
    private Stage gameStage;
    private ComponentMapper<TextureComponent> mTexture;
    private ComponentMapper<MouvementComponent> mMouvement;
    private ComponentMapper<TextureAnimationComponent> mTextureAnimation;

    @Override
    protected void process(int entityId) {
        MouvementComponent mouvementComponent = mMouvement.get(entityId);
        TextureAnimationComponent textureAnimation = mTextureAnimation.get(entityId);
        if (mouvementComponent.oldPoss.get(0).equals(mouvementComponent.oldPoss.get(1))) {
            textureAnimation.cooldown = 0.0f;
        } else {
            textureAnimation.cooldown -= Gdx.graphics.getDeltaTime();
        }
        if (textureAnimation.cooldown <= 0) {
            TextureComponent textureComponent = mTexture.get(entityId);
            updateAnimation(textureAnimation, mouvementComponent, textureComponent);
            textureAnimation.cooldown = textureAnimation.laps;
        }
    }

    private void updateAnimation(TextureAnimationComponent textureAnimation, MouvementComponent mouvementComponent, TextureComponent textureComponent) {
        final TextureRegion textureRegion;
        if (++textureAnimation.animationIndex >= textureAnimation.animationFront.length) {
            textureAnimation.animationIndex = 1;
        }
        if (isInputKeysUp(mouvementComponent.lastDirection)) {
            textureRegion = textureAnimation.animationBack[textureAnimation.animationIndex];
        } else if (isInputKeysLeft(mouvementComponent.lastDirection)) {
            textureRegion = textureAnimation.animationLeft[textureAnimation.animationIndex];
        } else if (isInputKeysDown(mouvementComponent.lastDirection)) {
            textureRegion = textureAnimation.animationFront[textureAnimation.animationIndex];
        } else if (isInputKeysRight(mouvementComponent.lastDirection)) {
            textureRegion = textureAnimation.animationRight[textureAnimation.animationIndex];
        } else {
            textureRegion = textureAnimation.animationFront[0];
        }
        textureComponent.texture = textureRegion;
    }
}
