
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

	// compute the distance from this to p
	public double distToPoint(Vector p) {
		Vector delta = new Vector(p);
		delta.substract(this);
		return delta.getNorma();
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
	
	public void multiplyByVector(Vector other) {
		this.x *= other.x;
		this.y *= other.y;
		this.z *= other.z;
	}
	
	public void multiplyByMatrix(Matrix M)
	{
		double[] temp = new double[3];
		
		//compute multiplication using homogenic cordinates
		for(int i = 0; i < 3; i ++)
			temp[i] = M.mat[i][0] * this.x + M.mat[i][1] * this.y + M.mat[i][2] * this.z + M.mat[i][3] * 1;
		
		this.x = temp[0];
		this.y = temp[1];
		this.z = temp[2];
	}
	
	public void rotateVector(int dim, double deg) {
		Matrix rotateMat = new Matrix(dim, deg);
		this.multiplyByMatrix(rotateMat);
	}

}
