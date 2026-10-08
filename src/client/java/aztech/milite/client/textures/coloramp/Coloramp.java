package aztech.milite.client.textures.coloramp;

import static aztech.milite.client.textures.TextureHelper.*;

import com.mojang.blaze3d.platform.NativeImage;

public interface Coloramp {
    int getRGB(double luminance);

    int getMeanRGB();

    default NativeImage bakeAsImage() {
        NativeImage image = new NativeImage(256, 256, true);
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                double luminance = (i) / 255.0;
                int rgb = getRGB(luminance);
                int r = getRrgb(rgb);
                int g = getGrgb(rgb);
                int b = getBrgb(rgb);
                image.setPixelRGBA(i, j, fromArgb(255, r, g, b));
            }
        }
        return image;
    }
}
