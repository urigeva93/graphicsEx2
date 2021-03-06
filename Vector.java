
public class Vector {
	double x;
	double y;
	double z;

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(Vector v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public void add(Vector other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
	}

	public void substract(Vector other) {
		this.x -= other.x;
		this.y -= other.y;
		this.z -= other.z;
	}

	public void negative() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
	}

	public double getNormSquared() {
		return ((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
	}

	public double getNorma() {
		return Math.sqrt(this.getNormSquared());
	}

	public void normalize() {
		double length = this.getNorma();
		if (length == 0) {
			return;
		}
		this.x /= length;
		this.y /= length;
		this.z /= length;
	}

	public void multiplyByScalar(double scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
	}

	public double dotProduct(Vector other) {
		return ((this.x * other.x) + (this.y * other.y) + (this.z * other.z));
	}

	public void crossProduct(Vector other) {
		// working on temp vars, changing THIS vector
		double x = (this.y * other.z) - (this.z * other.y);
		double y = (this.z * other.x) - (this.x * other.z);
		double z = (this.x * other.y) - (this.y * other.x);

		this.x = x;
		this.y = y;
		this.z = z;
	}

}
