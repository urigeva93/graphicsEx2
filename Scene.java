

public class Scene {

    public int shadowRays;
    public int maxRecLvl;
    public int superSamplingLvl;
    public Color backgroundColor;

    public Scene(int shadowRays, int maxRecLvl, int superSamplingLvl, Color backgroundColor) {
        this.shadowRays = shadowRays;
        this.maxRecLvl = maxRecLvl;
        this.superSamplingLvl = superSamplingLvl;
        this.backgroundColor = backgroundColor;
    }
}
