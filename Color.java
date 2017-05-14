public class Color {
    public int red;
    public int green;
    public int blue;

    public Color() {
        this.red = 255;
        this.green = 255;
        this.blue = 255;
    }

    public Color(int red, int blue, int green) {
        this.red = red;
        this.blue = blue;
        this.green = green;
    }

    public Color(Color other) {
        this.red = other.red;
        this.green = other.green;
        this.blue = other.blue;
    }

    public void add(Color other) {
        this.red += other.red;
        this.blue += other.blue;
        this.green += other.green;
    }

    public void multiplyRayColor(RayColor raycolor) {
        this.red *= raycolor.red;
        this.blue *= raycolor.blue;
        this.green *= raycolor.green;
    }

    public void multiplyScalar(double scalar) {
        this.red *= scalar;
        this.blue *= scalar;
        this.green *= scalar;
    }

    public void normalizeColor() {
        if (this.red > 255)
            this.red = 255;
        if (this.green > 255)
            this.green = 255;
        if (this.blue > 255)
            this.blue = 255;
    }
}