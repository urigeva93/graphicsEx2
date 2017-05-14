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

/**
 * Main class for ray tracing exercise.
 */
public class RayTracer {

    public int imageWidth;
    public int imageHeight;
    public int shadowRays;
    public int maxRecLvl;
    public int superSamplingLvl;
    List<Surface> surfaces;
    List<Material> materials;
    List<Light> lights;
    public Color backgroundColor;
    public Camera camera;

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
        String line = null;
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
                    this.backgroundColor = new Color();
                    this.backgroundColor.multiplyRayColor(backgroundRayColor);
                    this.shadowRays = Integer.parseInt(params[3]);
                    this.maxRecLvl = Integer.parseInt(params[4]);
                    this.superSamplingLvl = Integer.parseInt(params[5]);

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

                    Traingle trg = new Traingle(v1, v2, v3, materialIndex);

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

        if (this.camera == null || this.materials.isEmpty() || this.lights.isEmpty() || this.surfaces.isEmpty() ||
            this.shadowRays == 0 || this.superSamplingLvl == 0 || this.maxRecLvl == 0 || this.backgroundColor == null) {
            throw new RayTracerException("ERROR: one or more necessary scene parameters weren't inserted");
        }

        System.out.println("Finished parsing scene file " + sceneFileName);

    }

    /**
     * Renders the loaded scene and saves it to the specified file location.
     */
    public void renderScene(String outputFileName) {
        long startTime = System.currentTimeMillis();

        // Create a byte array to hold the pixel data:
        byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];


        // Put your ray tracing code here!
        //
        // Write pixel color values in RGB format to rgbData:
        // Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
        //            green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
        //             blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
        //
        // Each of the red, green and blue components should be a byte, i.e. 0-255


        long endTime = System.currentTimeMillis();
        Long renderTime = endTime - startTime;

        // The time is measured for your own conveniece, rendering speed will not affect your score
        // unless it is exceptionally slow (more than a couple of minutes)
        System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

        // This is already implemented, and should work without adding any code.
        saveImage(this.imageWidth, rgbData, outputFileName);

        System.out.println("Saved file " + outputFileName);

    }

    public static class RayTracerException extends Exception {
        public RayTracerException(String msg) {
            super(msg);
        }
    }


}
