import imageEditor.Image;
import imageEditor.Effects;
import imageEditor.FileHandler;
import java.util.Scanner;
import java.io.File;

public class ImageEditor {

  public Image load_image (String [] args) {

    Image image = new Image();

    try {

      File srcFile = new File(args[0]);
      Scanner scanner = new Scanner(srcFile);
      image = FileHandler.parse_file(scanner);
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

  public Image transform (Image image, String [] args) {

    // invert, grayscale, emboss, motionblur

    try {
      // get transformation type
      String transformation = args[2];

      // transform image
      if (transformation.equals("invert")) {
        Effects.invert(image);
      } else if (transformation.equals("grayscale")) {
        Effects.grayscale(image);
      } else if (transformation.equals("emboss")) {
        Effects.emboss(image);
      } else if (transformation.equals("motionblur")) {
        int motion_blur_length = Integer.valueOf(args[3]);
        Effects.motionblur(image, motion_blur_length);
      }


    } catch (Exception e) {
      System.out.println("Exception => " + e);
      System.out.println("USAGE: java ImageEditor in-file out-file (grayscale|invert|emboss|motionblur motion-blur-length)");
    } finally {
      return image;
    }
  }

  public static void main(String [] args) {

    // create ImageEditor object
    ImageEditor imageEditor = new ImageEditor();

    // load image
    Image original_image = imageEditor.load_image(args);

    // perform transformation
    Image transformed_image = imageEditor.transform(original_image, args);

    // save image
    FileHandler.save(transformed_image, args);

  }

}
