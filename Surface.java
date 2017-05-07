public interface Surface {
	
	public Vector getItersectionPointWithRay(Ray r);
	public Ray getReflectionRay(Vector intersectionPoint, Ray inRay);
	public int getMaterialIndex();
	public Vector getNormalDirection(Vector point);
}
