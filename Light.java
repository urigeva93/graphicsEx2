public class Light {
    Vector position;
    RayColor color;
    double specularIntens;
    double shadowIntens;
    double lightRadius;

    public Light(Vector position, RayColor color, double specularIntens, double shadowIntens, double lightRadius) {
        this.position = position;
        this.color = color;
        this.specularIntens = specularIntens;
        this.shadowIntens = shadowIntens;
        this.lightRadius = lightRadius;
    }

    public Boolean hasItersectionPointWithRay(Ray r) {

        Vector positionNorm = new Vector(this.position);
        positionNorm.substract(r.start);
        if (positionNorm.dotProduct(r.direction) < 0)
            return false;

        double x = r.direction.x * positionNorm.y * positionNorm.z;
        double y = r.direction.y * positionNorm.x * positionNorm.z;
        double z = r.direction.z * positionNorm.x * positionNorm.y;
        Boolean flag = (x == y && x == z);
        return flag;
    }

}