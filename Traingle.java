
public class Traingle implements Surface {

    public Vector v1, v2, v3, N;
    public int materialIndex;

    public Traingle(Vector v1, Vector v2, Vector v3, int index) {
        this.materialIndex = index;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;

        //compute Norma
        Vector v1Copy = new Vector(this.v1);
        Vector v2Copy = new Vector(this.v2);
        Vector v3Copy = new Vector(this.v3);
        v2Copy.substract(v1Copy);
        v3Copy.substract(v1Copy);
        v2Copy.crossProduct(v3Copy);
        Vector N = new Vector(v2Copy);
        N.normalize();
        this.N = N;

    }

    public Vector getItersectionPointWithRay(Ray ray){

        Plane pl = new Plane(N, 0, this.materialIndex);
        Vector intersectionP = pl.getItersectionPointWithRay(ray);
        if(intersectionP == null)
            return null;

        //check if inside Traingle

        //side 1 - v1, v2
        Vector v1Copy = new Vector(this.v1);
        Vector v2Copy = new Vector(this.v2);
        v1Copy.substract(ray.start);
        v2Copy.substract(ray.start);
        v2Copy.crossProduct(v1Copy);
        Vector N = new Vector(v2Copy);
        N.normalize();

        Vector tmp = new Vector(intersectionP);
        tmp.substract(ray.start);
        if (tmp.dotProduct(N) < 0)
            return null;

        //side 2 - v2, v3
        v2Copy = new Vector(this.v2);
        Vector v3Copy = new Vector(this.v3);
        v2Copy.substract(ray.start);
        v3Copy.substract(ray.start);
        v3Copy.crossProduct(v1Copy);
        N = new Vector(v3Copy);
        N.normalize();

        tmp = new Vector(intersectionP);
        tmp.substract(ray.start);
        if (tmp.dotProduct(N) < 0)
            return null;

        //side 3 - v1, v3
        v1Copy = new Vector(this.v1);
        v3Copy = new Vector(this.v3);
        v1Copy.substract(ray.start);
        v3Copy.substract(ray.start);
        v3Copy.crossProduct(v1Copy);
        N = new Vector(v3Copy);
        N.normalize();

        tmp = new Vector(intersectionP);
        tmp.substract(ray.start);
        if (tmp.dotProduct(N) < 0)
            return null;

        //the insersectionP is inside
        return intersectionP;
    }

    public Ray getReflectionRay(Vector intersectionPoint, Ray inRay) {
        //identical to sphere
        Vector No = new Vector(this.N);
        No.normalize(); // normalize the normal
        No.multiplyByScalar(2 * (No.dotProduct(inRay.direction)));
        Vector reflectionDirec = new Vector(inRay.direction);
        reflectionDirec.substract(No);
        reflectionDirec.negative();
        Ray reflection = new Ray(intersectionPoint, reflectionDirec);
        return reflection;
    }
    public int getMaterialIndex() {
        return this.materialIndex;
    }
    public Vector getNormalDirection(Vector point) {
        return this.N;
    }
}
