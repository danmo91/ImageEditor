import services.Image;
import services.Pixel;
import java.util.Scanner;
import java.io.*;

public class ImageEditor {

  public Image set_width_and_height(String token) {
    Image image = new Image();

    String width = token.substring(0, token.indexOf(" "));
    String height = token.substring(token.indexOf(" ")+1, token.length());
    image.width = Integer.valueOf(width);
    image.height = Integer.valueOf(height);
    image.pixels = new Pixel[image.width][image.height];

    return image;
  }

  public Image parse_file(Scanner scanner) {

    Image image = new Image();
    Pixel pixel = new Pixel();
    String state = "P3";
    int row = 0;
    int col = 0;

    try {

      while (scanner.hasNext()) {

        String token = scanner.next();
        System.out.println("token =>" + token);

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
          image.pixels = new Pixel[image.width][image.height];
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

          // pixel is complete, add to image pixels[][]
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
    }

    return image;
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
    }

    finally {
      return image;
    }

  }

  public static void main(String [] args) {

    // create ImageEditor object
    ImageEditor imageEditor = new ImageEditor();

    // load image into memory
    Image image = imageEditor.load_image(args);

    image.print();
    // perform transformation

    // save image

  }

}
