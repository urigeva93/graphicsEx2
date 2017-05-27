public class Plane implements Surface {

    public Vector N;
    public double offset;
    public int materialIndex;
    public Vector pointPlane;

    public Plane(Vector N, double offset, int index) {
        this.N = new Vector(N);
        this.N.normalize();
        this.offset = offset;
        this.materialIndex = index;

        if (this.N.x != 0)
            this.pointPlane = new Vector(this.offset / this.N.x, 0, 0);
        else if (this.N.y != 0)
            this.pointPlane = new Vector(0, this.offset / this.N.y, 0);
        else if (this.N.z != 0)
            this.pointPlane = new Vector(0, 0, this.offset / this.N.z);
    }


    public Vector getItersectionPointWithRay(Ray ray) {
        if (ray.direction.dotProduct(this.N) == 0) {
            // The ray and the plane are parallel (because N is vertical to plane)
            return null;
        }
        Vector temp = new Vector(this.pointPlane);
        temp.substract(ray.start);

        double t = temp.dotProduct(this.N) / (ray.direction.dotProduct(this.N));
        if (t < 0)
            return null;
        else {
            Vector intersectionP = new Vector(ray.direction);
            intersectionP.multiplyByScalar(t);
            intersectionP.add(ray.start);
            return intersectionP;
        }
    }

    public Ray getReflectionRay(Vector intersectionP, Ray inRay) {

        //identical to sphere
        Vector No = new Vector(this.N);
        No.normalize(); // normalize the normal
        No.multiplyByScalar(2 * (No.dotProduct(inRay.direction)));
        Vector reflectionDirec = new Vector(inRay.direction);
        reflectionDirec.substract(No);
        reflectionDirec.negative();
        Ray reflection = new Ray(intersectionP, reflectionDirec);
        return reflection;
    }

    public int getMaterialIndex() {
        return this.materialIndex;
    }

    public Vector getNormalDirection(Vector point) {
        return this.N;
    }

}