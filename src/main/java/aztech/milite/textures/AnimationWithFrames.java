package aztech.milite.textures;

import java.util.List;

public class AnimationWithFrames extends Animation {
    public final List<Integer> frames;

    public AnimationWithFrames(int frametime, List<Integer> frames) {
        super(frametime);
        this.frames = frames;
    }
}
