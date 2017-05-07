public class Ray {

	Vector start;
	Vector direction;

	public Ray(Vector start, Vector direction) {
		this.start = start;
		this.direction = direction;
		// assuming the direction is from the start
		this.direction.normalize();
	}

	public double getDestFromPoint(Vector point) {
		Vector destination = new Vector(point);
		destination.substract(this.start);
		return destination.getNorma();
	}
}
