import imageEditor.Image;
import imageEditor.Pixel;
import java.util.Scanner;
import java.io.*;

public class ImageEditor {

  public Image parse_file(Scanner scanner) {

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
        } else if (state.equals("P3")) {
          if (!token.equals("P3")) {
            throw new Exception("missing 'P3' from file header");
          }
          state = "width";
        } else if (state.equals("width")) {
          image.width = Integer.valueOf(token);
          state = "height";
        } else if (state.equals("height")) {
          image.height = Integer.valueOf(token);
          image.pixels = new Pixel[image.height][image.width];
          state = "max_color_value";
        } else if (state.equals("max_color_value")) {
          image.max_color_value = Integer.valueOf(token);
          state = "red";
        } else if (state.equals("red")) {
          pixel = new Pixel();
          pixel.red = Integer.valueOf(token);
          state = "green";
        } else if (state.equals("green")) {
          pixel.green = Integer.valueOf(token);
          state = "blue";
        } else if (state.equals("blue")) {
          pixel.blue = Integer.valueOf(token);

          // pixel is complete, add to image.pixels[][]
          image.pixels[row][col] = pixel;

          // update state, row & col
          state = "red";
          if (col < image.width -1) {
            col++;
          } else {
            col = 0;
            row++;
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

  public Image load_image (String [] args) {

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

  public Image invert (Image image) {
    // inverted_value = max_color_value - original_value
    for (int row = 0; row < image.height; row++) {
      for (int col = 0; col < image.width; col++) {
        // get pixel
        Pixel pixel = image.pixels[row][col];
        // transform each color
        pixel.red = image.max_color_value - pixel.red;
        pixel.green = image.max_color_value - pixel.green;
        pixel.blue = image.max_color_value - pixel.blue;
        // save
        image.pixels[row][col] = pixel;
      }
    }

    return image;
  }

  public Image grayscale(Image image) {

    // new_value = (red + green + blue)/3
    for (int row = 0; row < image.height; row++) {
      for (int col = 0; col < image.width; col++) {
        // get pixel
        Pixel pixel = image.pixels[row][col];
        // transform each color
        int gray_color = (pixel.red + pixel.green + pixel.blue)/3;
        pixel.red = gray_color;
        pixel.green = gray_color;
        pixel.blue = gray_color;
        // save
        image.pixels[row][col] = pixel;
      }
    }


    return image;
  }

  public Image emboss(Image image) {

    // for each pixel
    for (int row = image.height-1; row > 0; row--) {
      for (int col = image.width-1; col > 0; col--) {

        // get pixel
        Pixel pixel = image.pixels[row][col];
        int emboss_value = 0;
        int maxDiff = 0;

        // calculate emboss value
        if (row-1 >= 0 && col-1 >= 0) {

          // get color diffs
          int redDiff = pixel.red - image.pixels[row-1][col-1].red;
          int greenDiff = pixel.green - image.pixels[row-1][col-1].green;
          int blueDiff = pixel.blue - image.pixels[row-1][col-1].blue;

          // find max diff
          maxDiff = redDiff;
          if (Math.abs(maxDiff) < Math.abs(greenDiff)) {
            maxDiff = greenDiff;
          } else if (Math.abs(maxDiff) < Math.abs(blueDiff)) {
            maxDiff = blueDiff;
          }

          // set emboss value
          emboss_value = 128 + maxDiff;

          // scale emboss value
          if (emboss_value < 0) {
            emboss_value = 0;
          } else if (emboss_value > 255) {
            emboss_value = 255;
          }

        } else {
          emboss_value = 128;
        }

        // transform each color
        pixel.red = emboss_value;
        pixel.green = emboss_value;
        pixel.blue = emboss_value;

        // save
        image.pixels[row][col] = pixel;

      }
    }
    return image;
  }

  public Image transform (Image image, String [] args) {

    // invert, grayscale, emboss, motionblur

    try {
      // get transformation type
      String transformation = args[2];

      // transform image
      if (transformation.equals("invert")) {
        image = invert(image);
      } else if (transformation.equals("grayscale")) {
        image = grayscale(image);
      } else if (transformation.equals("emboss")) {
        image = emboss(image);
      }


    } catch (Exception e) {
      System.out.println("Exception => " + e);
      System.out.println("USAGE: java ImageEditor in-file out-file (grayscale|invert|emboss|motionblur motion-blur-length)");
    } finally {
      return image;
    }
  }

  public StringBuilder build_file_header(Image image, String outFile) {
    StringBuilder tmp = new StringBuilder();
    // add P3, comment about file, image width and height, max color value
    tmp.append("P3\n");
    tmp.append("# " + outFile + "\n");
    tmp.append(image.width + " " + image.height + "\n");
    tmp.append(image.max_color_value + "\n");
    return tmp;
  }

  public StringBuilder append_pixels(Image image, StringBuilder output) {
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
    return output;
  }

  public void save (Image image, String [] args) {

    try {
      // get outputs file
      String outFile = args[1];
      // build file header
      StringBuilder output = build_file_header(image, outFile);
      // add pixels
      output = append_pixels(image, output);

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

  public static void main(String [] args) {

    // create ImageEditor object
    ImageEditor imageEditor = new ImageEditor();

    // load image into memory
    Image original_image = imageEditor.load_image(args);

    // perform transformation
    Image transformed_image = imageEditor.transform(original_image, args);

    // save image
    imageEditor.save(transformed_image, args);

  }

}
