package aztech.milite.util;

public record Rectangle(int x, int y, int w, int h) {
    public boolean contains(double x, double y) {
        return this.x <= x && x <= this.x + this.w && this.y <= y && y < this.y + this.h;
    }
}
