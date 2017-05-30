public class Camera {

    Vector position;
    Vector lookAtPoint;
    Vector direction;
    Vector upVector;
    double screenDistance;
    double screenWidth;

    public Camera(Vector position, Vector lookAtPoint, Vector upVector,
                  double screenDistance, double screenWidth) {
        this.position = position;
        this.lookAtPoint = lookAtPoint;

        // find the direction vector
        this.direction = new Vector(this.lookAtPoint);
        this.direction.substract(position);
        this.direction.normalize();

        // fix the up vector to be perpendicular to the direction
        this.upVector = new Vector(upVector);
        Vector tmp = new Vector(this.direction);
        tmp.multiplyByScalar(tmp.dotProduct(upVector));
        this.upVector.substract(tmp);
        this.screenDistance = screenDistance;
        this.screenWidth = screenWidth;
    }

    public Vector getCenterOfScreen() {
        Vector center = new Vector(this.direction);
        center.normalize();
        center.multiplyByScalar(this.screenDistance);
        center.add(this.position);
        return center;
    }
}
