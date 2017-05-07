public class Sphere implements Surface {
	
	Vector center;
	double r;
	int materialIndex;
	
	public Sphere(Vector center, double r, int materialIndex) {
		this.center = center; 
		this.r = r;
		this.materialIndex = materialIndex;
	}
	
	public int getMaterialIndex() {
		return this.materialIndex;
	}
	
	public Vector getItersectionPointWithRay(Ray ray) {
		//TODO
	}
	
	public Ray getReflectionRay(Vector intersectionPoint, Ray inRay) {
		//TODO
	}
	
	public Vector getNormalDirection(Vector point) {
		Vector direc = new Vector(point);
		direc.substract(this.center);
		direc.normalize();
		return direc;

	}

	
	
	
	
	

}
