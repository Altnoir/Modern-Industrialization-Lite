package aztech.milite.client.textures.coloramp;

public class HotIngotColoramp implements Coloramp {
    private final Coloramp from;
    private final double bias, gamma;

    public HotIngotColoramp(Coloramp from, double bias, double gamma) {
        this.from = from;
        this.bias = bias;
        this.gamma = gamma;
    }

    @Override
    public int getRGB(double luminance) {
        return from.getRGB(Math.min(1.0, bias + Math.pow(luminance, gamma)));
    }

    @Override
    public int getMeanRGB() {
        return from.getMeanRGB();
    }
}
