package aztech.milite.definition;

import aztech.milite.textures.Animation;
import aztech.milite.textures.AnimationWithFrames;
import aztech.milite.textures.MCMetaInfo;

import java.util.List;

public enum FluidTexture {
    LAVA_LIKE("lava_like", new MCMetaInfo(new AnimationWithFrames(2, List.of(0,
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)))),

    PLASMA_LIKE("plasma_like", new MCMetaInfo(new Animation(1))),
    STEAM_LIKE("steam_like", new MCMetaInfo(new Animation(2))),
    WATER_LIKE("water_like", new MCMetaInfo(new Animation(2)));

    public final String path;
    public final MCMetaInfo mcMetaInfo;

    FluidTexture(String path, MCMetaInfo mcMetaInfo) {
        this.path = path;
        this.mcMetaInfo = mcMetaInfo;
    }
}
