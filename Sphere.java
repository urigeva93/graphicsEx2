public class Sphere implements Surface {

    Vector center;
    double radius;
    int materialIndex;

    public Sphere(Vector center, double r, int materialIndex) {
        this.center = center;
        this.radius = r;
        this.materialIndex = materialIndex;
    }

    public int getMaterialIndex() {
        return this.materialIndex;
    }


    //implements as shown in presentation "LS_Raycasting" geometric version
    public Vector getItersectionPointWithRay(Ray ray) {

        // check if the ray starts from inside the sphere
        // if True, then the ray won't intersect with it

        Vector start = new Vector(ray.start);
        start.substract(this.center);
        if (start.getNorma() < this.radius - (this.radius / 100)) {
            return null;
        }

        Vector L = new Vector(this.center);
        L.substract(ray.start); // L = O - P0
        double t_ca = ray.direction.dotProduct(L); //t_ca = L * V
        if (t_ca < 0)
            return null;

        double d2 = L.dotProduct(L) - Math.pow(t_ca, 2); // d^2 = L * L - t_ca^2
        if(d2 > Math.pow(this.radius, 2))
            return null;

        double t_hc = Math.sqrt(Math.pow(this.radius, 2) - d2); // t_hc = sqrt(r^2 - d^2)
        double t = t_ca - t_hc;

        // P = P0 + tV
        Vector intersectionP = new Vector(ray.direction);
        intersectionP.multiplyByScalar(t);
        intersectionP.add(ray.start);

        return intersectionP;

    }

    public Ray getReflectionRay(Vector intersectionP, Ray inRay) {

        // normal to the sphere at intersection point
        //R = inRay - 2(inRay * N)N
        Vector N = this.getNormalDirection(intersectionP);
        Vector reflectionDirec = new Vector(N);
        reflectionDirec.multiplyByScalar(2 * N.dotProduct(inRay.direction));
        reflectionDirec.substract(inRay.direction);
        reflectionDirec.negative();
        Ray reflection = new Ray(intersectionP, reflectionDirec);

        return reflection;

    }

    public Vector getNormalDirection(Vector point) {
        Vector direc = new Vector(point);
        direc.substract(this.center);
        direc.normalize();
        return direc;

    }


}
