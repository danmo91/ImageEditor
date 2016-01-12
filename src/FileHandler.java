import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;

// handles ppm File IO

public class FileHandler {

  public static Image load(String [] args) {

    Image image = new Image();

    try {
      File srcFile = new File(args[0]);
      Scanner scanner = new Scanner(srcFile);
      image = parse_file(scanner);
      scanner.close();
    }
    catch (Exception e) {
      System.out.println("exception => " + e);
      System.out.println("USAGE: java ImageEditor in-file out-file (grayscale|invert|emboss|motionblur motion-blur-length)");
    }
    finally {
      return image;
    }
  }

  public static Image parse_file(Scanner scanner) {

    Image image = new Image();
    Pixel pixel = new Pixel();
    String state = "P3";
    int row = 0;
    int col = 0;

    try {

      while (scanner.hasNext()) {
        String token = scanner.next();

        if (token.startsWith("#")) {
          // skip comments
          String rest_of_line = scanner.nextLine();
        } else {
          switch(state) {
            case "P3":
                if (!token.equals("P3")) {
                  throw new Exception("missing 'P3' from file header");
                }
                state = "width";
                break;
            case "width":
                image.width = Integer.valueOf(token);
                state = "height";
                break;
            case "height":
                image.height = Integer.valueOf(token);
                image.pixels = new Pixel[image.height][image.width];
                state = "max_color_value";
                break;
            case "max_color_value":
                image.max_color_value = Integer.valueOf(token);
                state = "red";
                break;
            case "red":
                pixel = new Pixel();
                pixel.red = Integer.valueOf(token);
                state = "green";
                break;
            case "green":
                pixel.green = Integer.valueOf(token);
                state = "blue";
                break;
            case "blue":
                pixel.blue = Integer.valueOf(token);
                // pixel is complete, add to image.pixels[][]
                image.pixels[row][col] = pixel;
                // update row & col
                if (col < image.width -1) {
                  col++;
                } else {
                  col = 0;
                  row++;
                }
                state = "red";
                break;
            default:
                throw new Exception("invalid token => " + token);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Exception => " + e);
      System.out.println("USAGE: java ImageEditor in-file out-file (grayscale|invert|emboss|motionblur motion-blur-length)");
    } finally {
      return image;
    }
  }

  public static StringBuilder build_file_header(Image image, String outFile) {
    StringBuilder tmp = new StringBuilder();
    // add P3, comment about file, image width and height, max color value
    tmp.append("P3\n");
    tmp.append("# " + outFile + "\n");
    tmp.append(image.width + " " + image.height + "\n");
    tmp.append(image.max_color_value + "\n");
    return tmp;
  }

  public static void append_pixels(Image image, StringBuilder output) {
    // for each pixel
    for (int row = 0; row < image.height; row++) {
      for (int col = 0; col < image.width; col++) {
        // append to StringBuidler
        Pixel pixel = image.pixels[row][col];
        output.append(pixel.red + "\n");
        output.append(pixel.green + "\n");
        output.append(pixel.blue + "\n");
      }
    }
    return;
  }

  public static void save (Image image, String [] args) {

    try {
      // get outputs file
      String outFile = args[1];
      // build file header
      StringBuilder output = build_file_header(image, outFile);
      // add pixels
      append_pixels(image, output);

      // save StringBuilder output
      PrintWriter writer = new PrintWriter(new File(outFile));
      writer.println(output.toString());
      writer.close();

    } catch (Exception e) {
      System.out.println("Exception => " + e);
    } finally {
      return;
    }
  }


}
