package JJack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class GameUtils {

    // creates an Animation from a single sprite sheet
    // with a subset of the frames, specified by an array

    public static Animation parseSpriteSheet(String fileName, int frameCols, int frameRows, int[] frameIndices,
                                             float frameDuration, PlayMode mode){
        Texture texture = new Texture(Gdx.files.internal(fileName));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        int frameWidth = texture.getWidth() / frameCols;
        int frameHeight = texture.getHeight() / frameRows;

        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[index] = temp[i][j];
                index++;
            }
        }

        Array<TextureRegion> framesArray = new Array<TextureRegion>();
        for (int n = 0; n < frameIndices.length; n++) {

            int i = frameIndices[n];
            framesArray.add(frames[i]);

        }

        return new Animation(frameDuration, framesArray, mode);
    }
}
