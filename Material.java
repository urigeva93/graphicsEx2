public class Material {

    RayColor diffuseColor;
    RayColor specularColor;
    double phongSpecularity;
    RayColor reflectionColor;
    double transparency;

    public Material(RayColor diffuseColor, RayColor specularColor, double phongSpecularity, RayColor reflectionColor, double transparency) {
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.phongSpecularity = phongSpecularity;
        this.reflectionColor = reflectionColor;
        this.transparency = transparency;
    }
}