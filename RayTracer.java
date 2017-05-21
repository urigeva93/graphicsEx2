import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main class for ray tracing exercise.
 */
public class RayTracer {

    public int imageWidth;
    public int imageHeight;
    public Scene scene;
    public Camera camera;
    List<Surface> surfaces;
    List<Material> materials;
    List<Light> lights;


    public RayTracer() {

        this.surfaces = new ArrayList<>();
        this.materials = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    /**
     * Runs the ray tracer. Takes scene file, output image file and image size as input.
     */
    public static void main(String[] args) {

        try {

            RayTracer tracer = new RayTracer();

            // Default values:
            tracer.imageWidth = 500;
            tracer.imageHeight = 500;

            if (args.length < 2)
                throw new RayTracerException("Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");

            String sceneFileName = args[0];
            String outputFileName = args[1];

            if (args.length > 3) {
                tracer.imageWidth = Integer.parseInt(args[2]);
                tracer.imageHeight = Integer.parseInt(args[3]);
            }

            // Parse scene file:
            tracer.parseScene(sceneFileName);

            // Render scene:
            tracer.renderScene(outputFileName);

//		} catch (IOException e) {
//			System.out.println(e.getMessage());
        } catch (RayTracerException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /*
     * Saves RGB data as an image in png format to the specified location.
     */
    public static void saveImage(int width, byte[] rgbData, String fileName) {
        try {

            BufferedImage image = bytes2RGB(width, rgbData);
            ImageIO.write(image, "png", new File(fileName));

        } catch (IOException e) {
            System.out.println("ERROR SAVING FILE: " + e.getMessage());
        }

    }

    /*
     * Producing a BufferedImage that can be saved as png from a byte array of RGB values.
     */
    public static BufferedImage bytes2RGB(int width, byte[] buffer) {
        int height = buffer.length / width / 3;
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, false,
                Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        DataBufferByte db = new DataBufferByte(buffer, width * height);
        WritableRaster raster = Raster.createWritableRaster(sm, db, null);
        BufferedImage result = new BufferedImage(cm, raster, false, null);

        return result;
    }


    //////////////////////// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT //////////////////////////////////////////

    /**
     * Parses the scene file and creates the scene. Change this function so it generates the required objects.
     */
    public void parseScene(String sceneFileName) throws IOException, RayTracerException {
        FileReader fr = new FileReader(sceneFileName);

        BufferedReader r = new BufferedReader(fr);
        String line;
        int lineNum = 0;
        System.out.println("Started parsing scene file " + sceneFileName);


        while ((line = r.readLine()) != null) {
            line = line.trim();
            ++lineNum;

            if (line.isEmpty() || (line.charAt(0) == '#')) {  // This line in the scene file is a comment
                continue;

            } else {
                String code = line.substring(0, 3).toLowerCase();
                // Split according to white space characters:
                String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

                if (code.equals("cam")) {

                    Vector position = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
                            Double.parseDouble(params[2]));
                    Vector lookAtPoint = new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]),
                            Double.parseDouble(params[5]));
                    Vector upVector = new Vector(Double.parseDouble(params[6]), Double.parseDouble(params[7]),
                            Double.parseDouble(params[8]));
                    double screenDistance = Double.parseDouble(params[9]);
                    double screenWidth = Double.parseDouble(params[10]);

                    this.camera = new Camera(position, lookAtPoint, upVector, screenDistance, screenWidth);

                    System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
                } else if (code.equals("set")) {

                    RayColor backgroundRayColor = new RayColor(Double.parseDouble(params[0]),
                            Double.parseDouble(params[1]), Double.parseDouble(params[2]));
                    Color backgroundColor = new Color();
                    backgroundColor.multiplyRayColor(backgroundRayColor);
                    int shadowRays = Integer.parseInt(params[3]);
                    int maxRecLvl = Integer.parseInt(params[4]);
                    int superSamplingLvl = 0;
                    if(params.length > 5)
                        superSamplingLvl = Integer.parseInt(params[5]);
                    this.scene = new Scene(shadowRays, maxRecLvl, superSamplingLvl, backgroundColor);

                    System.out.println(String.format("Parsed general settings (line %d)", lineNum));
                } else if (code.equals("mtl")) {

                    RayColor diffuseColor = new RayColor(Double.parseDouble(params[0]),
                            Double.parseDouble(params[1]), Double.parseDouble(params[2]));
                    RayColor specularColor = new RayColor(Double.parseDouble(params[3]),
                            Double.parseDouble(params[4]), Double.parseDouble(params[5]));
                    double phongSpecularity = Double.parseDouble(params[9]);
                    RayColor reflectionColor = new RayColor(Double.parseDouble(params[6]),
                            Double.parseDouble(params[7]), Double.parseDouble(params[8]));
                    double transparency = Double.parseDouble(params[10]);

                    Material mtl = new Material(diffuseColor, specularColor, phongSpecularity, reflectionColor, transparency);

                    this.materials.add(mtl);

                    System.out.println(String.format("Parsed material (line %d)", lineNum));
                } else if (code.equals("sph")) {

                    Vector center = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
                    double radius = Double.parseDouble(params[3]);
                    int materialIndex = Integer.parseInt(params[4]);

                    Sphere sph = new Sphere(center, radius, materialIndex);

                    this.surfaces.add(sph);

                    System.out.println(String.format("Parsed sphere (line %d)", lineNum));

                } else if (code.equals("pln")) {

                    Vector N = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
                    double offset = Double.parseDouble(params[3]);
                    int materialIndex = Integer.parseInt(params[4]);

                    Plane pln = new Plane(N, offset, materialIndex);

                    this.surfaces.add(pln);

                    System.out.println(String.format("Parsed plane (line %d)", lineNum));

                } else if (code.equals("trg")) {

                    Vector v1 = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
                    Vector v2 = new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5]));
                    Vector v3 = new Vector(Double.parseDouble(params[6]), Double.parseDouble(params[7]), Double.parseDouble(params[8]));
                    int materialIndex = Integer.parseInt(params[9]);

                    Triangle trg = new Triangle(v1, v2, v3, materialIndex);

                    this.surfaces.add(trg);

                    System.out.println(String.format("Parsed triangle (line %d)", lineNum));

                } else if (code.equals("lgt")) {

                    Vector position = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
                    RayColor color = new RayColor(Double.parseDouble(params[3]), Double.parseDouble(params[4]), Double.parseDouble(params[5]));

                    Double specularIntens = Double.parseDouble(params[6]);
                    double shadowIntens = Double.parseDouble(params[7]);
                    double lightRadius = Double.parseDouble(params[8]);

                    Light lgt = new Light(position, color, specularIntens, shadowIntens, lightRadius);

                    this.lights.add(lgt);

                    System.out.println(String.format("Parsed light (line %d)", lineNum));

                } else {
                    System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
                }
            }
        }

        // It is recommended that you check here that the scene is valid,
        // for example camera settings and all necessary materials were defined.

        if (this.camera == null || this.materials.isEmpty() || this.lights.isEmpty() || this.surfaces.isEmpty() || this.scene == null)
            throw new RayTracerException("ERROR: one or more necessary scene parameters weren't inserted or recognized");


        System.out.println("Finished parsing scene file " + sceneFileName);

    }

    /**
     * Renders the loaded scene and saves it to the specified file location.
     */
    public void renderScene(String outputFileName) throws RayTracerException {
        long startTime = System.currentTimeMillis();

        // Create a byte array to hold the pixel data:
        byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];
        Ray ray;
        Color pixel;

        for (int i = 0; i < this.imageWidth; i++) {
            for (int j = 0; j < this.imageHeight; j++) {
                ray = constructRayThroughPixel(camera, i, j);
                pixel = getPixelColor(ray, this.scene.maxRecLvl, null);
                rgbData[(j * this.imageWidth + i) * 3] = (byte) pixel.red;
                rgbData[(j * this.imageWidth + i) * 3 + 1] = (byte) pixel.green;
                rgbData[(j * this.imageWidth + i) * 3 + 2] = (byte) pixel.blue;
            }
        }

        long endTime = System.currentTimeMillis();
        Long renderTime = endTime - startTime;

        // The time is measured for your own convenience, rendering speed will not affect your score
        // unless it is exceptionally slow (more than a couple of minutes)
        System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

        // This is already implemented, and should work without adding any code.
        saveImage(this.imageWidth, rgbData, outputFileName);

        System.out.println("Saved file " + outputFileName);
    }

    private Ray constructRayThroughPixel(Camera camera, int i, int j) {

        Vector pixel = camera.getCenterOfScreen();

        double pixelWidth = camera.screenWidth / this.imageWidth;
        double deltaX = ((double) this.imageWidth / 2 - i) * pixelWidth;
        double deltaY = ((double) this.imageHeight / 2 - j) * pixelWidth;

        Vector yVec = new Vector(camera.upVector);
        yVec.normalize();
        yVec.multiplyByScalar(deltaY);
        pixel.add(yVec);

        Vector xVec = new Vector(camera.direction);
        xVec.crossProduct(camera.upVector);
        xVec.normalize();
        xVec.multiplyByScalar(deltaX);
        pixel.add(xVec);

        Vector pixelDirec = new Vector(pixel);
        pixelDirec.substract(camera.position);
        pixelDirec.normalize();

        Ray ray = new Ray(pixel, pixelDirec);
        return ray;
    }

    private Color getPixelColor(Ray ray, int maxRecLvl, Surface originSurface) throws RayTracerException {

        if (maxRecLvl == 0)
            return new Color(0, 0, 0);

        double minDestFromSurface = 0, minDestFromLight = 0;
        Vector intersectionPointWithSurface = null, intersectionPointWithLight = null;
        SurfaceIntersection surfaceInter = this.findClosestIntersectionWithSurface(ray, originSurface);
        LightIntersection lightInter = this.findClosestIntersectionWithLight(ray);

        if (surfaceInter != null) {
            minDestFromSurface = surfaceInter.dist;
            intersectionPointWithSurface = surfaceInter.intersection;
        }

        if (lightInter != null) {
            minDestFromLight = lightInter.distance;
            intersectionPointWithLight = lightInter.intersection;
        }

        Boolean ifLight = (intersectionPointWithLight != null) && (intersectionPointWithSurface == null || minDestFromLight < minDestFromSurface);
        //Boolean ifSurface = (intersectionPointWithSurface != null) && (intersectionPointWithLight == null || minDestFromSurface < minDestFromLight);
        Boolean ifSurface = intersectionPointWithSurface != null;

        //if (ifLight == ifSurface)
           // throw new RayTracerException("ERROR: intersection both with surface and light");

        if (ifSurface) {

            double transparency = this.materials.get(surfaceInter.surface.getMaterialIndex() - 1).transparency;
            RayColor materialReflectionColor = this.materials.get(surfaceInter.surface.getMaterialIndex() - 1).reflectionColor;

            Color diffuseColor = this.getDiffuseColor(surfaceInter);
            Color specularColor = this.getSpecularColor(surfaceInter, ray.direction);

            Color c = new Color(diffuseColor);
            c.add(specularColor);
            c.multiplyScalar(1 - transparency);
            Color res = new Color(c);

            if (!materialReflectionColor.isAllZero()) {
                Ray reflectionRay = surfaceInter.surface.getReflectionRay(surfaceInter.intersection, ray);
                Color reflectionColor = this.getPixelColor(reflectionRay, maxRecLvl - 1, surfaceInter.surface);
                reflectionColor.multiplyRayColor(materialReflectionColor);
                res.add(reflectionColor);
            }

            if (transparency != 0) {
                Ray transparencyRay = surfaceInter.surface.getReflectionRay(surfaceInter.intersection, ray);
                Color transparencyColor = this.getPixelColor(transparencyRay, maxRecLvl - 1, surfaceInter.surface);
                transparencyColor.multiplyScalar(transparency);
                res.add(transparencyColor);
            }
            res.normalizeColor();
            return res;

        } else {

            //if (ifLight)
                System.out.println("intersect with light");
            //else
                return new Color(this.scene.backgroundColor);
        }
    }

    private SurfaceIntersection findClosestIntersectionWithSurface(Ray ray, Surface originSurface) {
        double minDestFromSurface = Double.POSITIVE_INFINITY;
        Surface intersectionSurface = null;
        Vector IntersectionCand, IntersectionPoint = null;

        for (int i = 0; i < this.surfaces.size(); i++) {

            if (originSurface == this.surfaces.get(i))
                continue;

            IntersectionCand = this.surfaces.get(i).getItersectionPointWithRay(ray);
            if (IntersectionCand != null && minDestFromSurface > ray.getDestFromPoint(IntersectionCand)) {
                IntersectionPoint = IntersectionCand;
                minDestFromSurface = ray.getDestFromPoint(IntersectionPoint);
                intersectionSurface = this.surfaces.get(i);
            }
        }


        if (intersectionSurface == null) {
            return null;
        }
        return new SurfaceIntersection(intersectionSurface, IntersectionPoint, minDestFromSurface);
    }

    public LightIntersection findClosestIntersectionWithLight(Ray ray) {
        double minDestFromLight = Double.POSITIVE_INFINITY;
        Light intersectionLight = null;
        Vector IntersectionCand, IntersectionPoint = null;

        for (int i = 0; i < this.lights.size(); i++) {

            if (this.lights.get(i).hasItersectionPointWithRay(ray)) {
                IntersectionCand = new Vector(this.lights.get(i).position);
                IntersectionCand.substract(ray.start);
                if (IntersectionCand != null && minDestFromLight > IntersectionCand.getNorma() && IntersectionCand.getNorma() > 0) {
                    minDestFromLight = IntersectionCand.getNorma();
                    IntersectionPoint = this.lights.get(i).position;
                    intersectionLight = this.lights.get(i);
                }
            }

        }

        if (intersectionLight == null) {
            return null;
        }
        return new LightIntersection(intersectionLight, IntersectionPoint, minDestFromLight);
    }

    private Color getDiffuseColor(SurfaceIntersection surfaceInter) {
        Color diffuseColor = new Color(0, 0, 0);
        Material material = this.materials.get(surfaceInter.surface.getMaterialIndex() - 1);
        for (int i = 0; i < this.lights.size(); i++) {
            Color light = new Color(255, 255, 255);
            light.multiplyRayColor(this.lights.get(i).color);
            light.multiplyRayColor(material.diffuseColor);
            Vector direction = new Vector(this.lights.get(i).position);
            direction.substract(surfaceInter.intersection);
            direction.normalize();
            light.multiplyScalar(Math.abs(surfaceInter.surface.getNormalDirection(surfaceInter.intersection).dotProduct(direction)));
            double hits = this.getLightHits(surfaceInter, direction, this.lights.get(i));
            light.multiplyScalar(hits);
            diffuseColor.add(light);
        }

        diffuseColor.normalizeColor();
        return diffuseColor;
    }

    private Color getSpecularColor(SurfaceIntersection surfaceInter, Vector camDirection) {
        Color specularColor = new Color(0, 0, 0);
        Material material = this.materials.get(surfaceInter.surface.getMaterialIndex() - 1);

        for (int i = 0; i < this.lights.size(); i++) {
            Color light = new Color(255, 255, 255);
            light.multiplyRayColor(this.lights.get(i).color);
            light.multiplyRayColor(material.specularColor);
            light.multiplyScalar(this.lights.get(i).specularIntens);
            Vector direction = new Vector(this.lights.get(i).position);
            direction.substract(surfaceInter.intersection);
            direction.normalize();

            Ray inRay = new Ray(this.lights.get(i).position, direction);
            Ray r = surfaceInter.surface.getReflectionRay(surfaceInter.intersection, inRay);
            r.direction.normalize();

            //TODO check it
            if (surfaceInter.surface.getClass().getName().equals("Plane"))
                r.direction.negative();

            Vector k = new Vector(camDirection);
            k.normalize();
            double angle = r.direction.dotProduct(k);
            if (angle < 0)
                light.multiplyScalar(0);

            light.multiplyScalar(Math.pow(Math.abs(angle), material.phongSpecularity));
            double hits = this.getLightHits(surfaceInter, direction, this.lights.get(i));
            light.multiplyScalar(hits);
            specularColor.add(light);
        }
        specularColor.normalizeColor();
        return specularColor;
    }

    private double getLightHits(SurfaceIntersection surfaceInter, Vector direction, Light light) {
        Random rnd = new Random();

        double hits = 0;
        Vector u = new Vector(this.camera.upVector);
        u.crossProduct(direction);
        u.normalize();
        Vector w = new Vector(direction);
        w.crossProduct(u);
        w.normalize();
        assert w.dotProduct(u) == w.dotProduct(direction) && w.dotProduct(u) == u.dotProduct(direction) && w.dotProduct(u) == 0;
        u.multiplyByScalar(light.lightRadius);
        w.multiplyByScalar(light.lightRadius);
        Vector uStep = new Vector(u);
        uStep.multiplyByScalar(1 / (double) this.scene.shadowRays);
        Vector wStep = new Vector(w);
        wStep.multiplyByScalar(1 / (double) this.scene.shadowRays);
        Vector lightPosition = light.position;
        Vector topLeftCorner = new Vector(lightPosition);
        u.multiplyByScalar(0.5);
        topLeftCorner.substract(u);
        u.multiplyByScalar(2);
        w.multiplyByScalar(0.5);
        topLeftCorner.substract(w);
        w.multiplyByScalar(2);
        for (int j = 0; j < this.scene.shadowRays; j++) {
            for (int k = 0; k < this.scene.shadowRays; k++) {
                Vector randUStep = new Vector(uStep);
                Vector randWStep = new Vector(wStep);
                randUStep.multiplyByScalar(rnd.nextDouble());
                randWStep.multiplyByScalar(rnd.nextDouble());
                topLeftCorner.add(randUStep);
                topLeftCorner.add(randWStep);
                Surface closerSurface = this.isVisible(null, surfaceInter.intersection, topLeftCorner);
                if (closerSurface == null)
                    hits++;
                else {
                    double transparency = getTransparency(null, surfaceInter.intersection, topLeftCorner);
                    hits += transparency;
                }
                topLeftCorner.substract(randUStep);
                topLeftCorner.substract(randWStep);
                topLeftCorner.add(wStep);
            }
            topLeftCorner.add(uStep);
            topLeftCorner.substract(w);
        }
        double fraction = hits / (Math.pow(this.scene.shadowRays, 2));
        hits = (fraction * light.shadowIntens) + (1 - light.shadowIntens);
        return hits;

    }

    private double getTransparency(Surface surface, Vector start, Vector end) {
        double transparency = 1;
        Vector direction = new Vector(end);
        Vector startCopy = new Vector(start);
        direction.substract(startCopy);
        Vector e = new Vector(direction);
        e.normalize();
        e.multiplyByScalar(0.001);
        startCopy.add(e);
        Ray ray = new Ray(startCopy, new Vector(direction));

        Vector intersectionPointCand;
        for (int i = 0; i < this.surfaces.size(); i++) {
            if (surface == this.surfaces.get(i)) {
                continue;
            }
            intersectionPointCand = this.surfaces.get(i).getItersectionPointWithRay(ray);
            if (intersectionPointCand != null) {
                transparency *= this.materials.get(this.surfaces.get(i).getMaterialIndex() - 1).transparency;
            }
        }
        return transparency;
    }

    private Surface isVisible(Surface surface, Vector start, Vector end) {
        Vector direction = new Vector(end);
        Vector startCopy = new Vector(start);
        direction.substract(startCopy);

        Vector e = new Vector(direction);
        e.normalize();
        e.multiplyByScalar(0.001);
        startCopy.add(e);
        Ray ray = new Ray(startCopy, direction);
        SurfaceIntersection surfaceInter = this.findClosestIntersectionWithSurface(ray, surface);

        direction = new Vector(end);
        direction.substract(startCopy);
        double dist = direction.getNorma();

        if (surfaceInter != null) {
            if (surfaceInter.dist < dist)
                return surfaceInter.surface;
        }
        return null;
    }

    public static class RayTracerException extends Exception {
        public RayTracerException(String msg) {
            super(msg);
        }
    }
}
