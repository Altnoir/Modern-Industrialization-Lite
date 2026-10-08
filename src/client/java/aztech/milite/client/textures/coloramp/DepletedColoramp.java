package aztech.milite.client.textures.coloramp;

import aztech.milite.client.textures.TextureHelper;

import java.awt.*;

public class DepletedColoramp implements Coloramp {
    private final Coloramp coloramp;

    public DepletedColoramp(Coloramp coloramp) {
        this.coloramp = coloramp;
    }

    @Override
    public int getRGB(double luminance) {
        float[] hsbval = new float[3];
        int rgb = coloramp.getRGB(luminance);
        Color.RGBtoHSB(TextureHelper.getRrgb(rgb), TextureHelper.getGrgb(rgb), TextureHelper.getBrgb(rgb), hsbval);
        return 0xFFFFFF & Color.HSBtoRGB(hsbval[0], 0.2f * hsbval[0], 0.5f * hsbval[2]);
    }

    @Override
    public int getMeanRGB() {
        float[] hsbval = new float[3];
        int rgb = coloramp.getMeanRGB();
        Color.RGBtoHSB(TextureHelper.getRrgb(rgb), TextureHelper.getGrgb(rgb), TextureHelper.getBrgb(rgb), hsbval);
        return 0xFFFFFF & Color.HSBtoRGB(hsbval[0], 0.2f * hsbval[0], 0.5f * hsbval[2]);
    }
}
