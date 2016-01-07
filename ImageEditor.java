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
    int line_number = 1;
    int row = 0;
    int col = 0;

    try {

      while (scanner.hasNextLine()) {

        String token = scanner.nextLine();

        // if line starts with '#' then ignore
        if (!token.startsWith("#")) {
          // check for 'P3'
          if (state.equals("P3")) {
            state = "width";
            if (!token.equals("P3")) {
              throw new Exception("missing 'P3' from file header");
            }
          }
          // get width and height
          else if (state.equals("width")) {
            state = "max_value";
            image = set_width_and_height(token);
          }
          // get max color value
          else if (state.equals("max_value")) {
            state = "red";
            image.max_color_value = Integer.valueOf(token);
          }
          // get pixels
          else if (state.equals("red")) {
            pixel = new Pixel();
            pixel.red = Integer.valueOf(token);
            state = "green";
          } else if (state.equals("green")) {
            pixel.green = Integer.valueOf(token);
            state = "blue";
          } else if (state.equals("blue")) {
            pixel.blue = Integer.valueOf(token);

            image.pixels[row][col] = pixel;
            state = "red";

            // update row and col
            if (col == image.width-1 && row < image.height) {
              row++;
            }

            if (col < image.width-1) {
              col++;
            } else {
              col = 0;
            }
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
    return image;
  }

  public static void main(String [] args) {

    // create ImageEditor object
    ImageEditor ie = new ImageEditor();

    // load image
    Image image = new Image();
    image = ie.load_image(args);

    image.print();

    // perform transformation

    // save image

  }

}
