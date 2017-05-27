
public class Triangle implements Surface {

    public Vector v1, v2, v3, N;
    public int materialIndex;

    public Triangle(Vector v1, Vector v2, Vector v3, int index) {
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

    public Vector getItersectionPointWithRay(Ray ray) {
        Plane pl = new Plane(this.N, this.N.dotProduct(this.v1), this.materialIndex);
        Vector intersectionP = pl.getItersectionPointWithRay(ray);
        if(intersectionP == null)
            return null;

        //first side
        Vector edge1 = new Vector(this.v2);
        edge1.substract(this.v1);

        Vector r1 = new Vector(intersectionP);
        r1.substract(this.v1);
        edge1.crossProduct(r1);
        if(this.N.dotProduct(edge1) < 0)
            return null;

        //second side
        Vector edge2 = new Vector(this.v3);
        edge2.substract(this.v2);

        Vector r2 = new Vector(intersectionP);
        r2.substract(this.v2);
        edge2.crossProduct(r2);
        if(this.N.dotProduct(edge2) < 0)
            return null;

        //third side
        Vector edge3 = new Vector(this.v1);
        edge3.substract(this.v3);

        Vector r3 = new Vector(intersectionP);
        r3.substract(this.v3);
        edge3.crossProduct(r3);
        if(this.N.dotProduct(edge3) < 0)
            return null;

        //point is inside triangle
        return intersectionP;

    }

    public Ray getReflectionRay(Vector intersectionPoint, Ray inRay) {
        //identical to sphere
        Vector No = new Vector(this.N);
        No.normalize(); //
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
