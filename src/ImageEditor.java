import imageEditor.Image;
import imageEditor.Pixel;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;

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

  public void invert (Image image) {
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

    return;
  }

  public void grayscale(Image image) {

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


    return;
  }

  public void emboss(Image image) {

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
    return;
  }

  public void motionblur(Image image, int motion_blur_length) {

    try {
      // check valid blur length
      if (motion_blur_length <= 0) {
        throw new Exception("invalid motion blur length => " + motion_blur_length );
      }
      // get average for each color
      for (int row = 0; row < image.height; row++) {
        for (int col = 0; col < image.width; col++) {
          // get pixel
          Pixel pixel = image.pixels[row][col];

          // get sum of each color
          int red_sum = 0;
          int green_sum = 0;
          int blue_sum = 0;
          int actual_length = 1;
          for (int i = col; (i < col + motion_blur_length - 1) && (i < image.width); i++) {
            red_sum += image.pixels[row][i].red;
            green_sum += image.pixels[row][i].green;
            blue_sum += image.pixels[row][i].blue;
            actual_length++;
          }

          // calculate average
          int red_average = red_sum/actual_length;
          int green_average = green_sum/actual_length;
          int blue_average = blue_sum/actual_length;

          // transform each color
          pixel.red = red_average;
          pixel.green = green_average;
          pixel.blue = blue_average;

          // save
          image.pixels[row][col] = pixel;
        }
      }


    } catch (Exception e) {
      System.out.println("Exception => " + e);
    } finally {
      return;
    }
  }

  public Image transform (Image image, String [] args) {

    // invert, grayscale, emboss, motionblur

    try {
      // get transformation type
      String transformation = args[2];

      // transform image
      if (transformation.equals("invert")) {
        invert(image);
      } else if (transformation.equals("grayscale")) {
        grayscale(image);
      } else if (transformation.equals("emboss")) {
        emboss(image);
      } else if (transformation.equals("motionblur")) {
        int motion_blur_length = Integer.valueOf(args[3]);
        motionblur(image, motion_blur_length);
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

  public void append_pixels(Image image, StringBuilder output) {
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

  public void save (Image image, String [] args) {

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
