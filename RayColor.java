public class RayColor {
    double red;
    double green;
    double blue;

    public RayColor(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Boolean isAllZero() {
        return this.red == 0 && this.blue == 0 && this.green == 0;
    }
}