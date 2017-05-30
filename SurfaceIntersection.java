public class SurfaceIntersection {
    public Surface surface;
    public Vector intersection;
    public double dist;

    public SurfaceIntersection(Surface surface, Vector intersection, double distance) {
        this.surface = surface;
        this.intersection = intersection;
        this.dist = distance;
    }
}