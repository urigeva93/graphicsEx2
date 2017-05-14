import java.awt.*;

public class RayColor {
    double red;
    double green;
    double blue;

    public RayColor(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RayColor(double red, double green, double blue, double factor) {
        this.red = red * factor;
        this.green = green * factor;
        this.blue = blue * factor;
    }

    public RayColor(RayColor color, double specularIntens) {
        this.red = color.red * specularIntens;
        this.green = color.green * specularIntens;
        this.blue = color.blue * specularIntens;
    }

    public Boolean isAllZero() {
        return this.red == 0 && this.blue == 0 && this.green == 0;
    }
}