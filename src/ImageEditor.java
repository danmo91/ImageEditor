package ImageEditor;

import java.util.Scanner;
import java.io.File;

public class ImageEditor {

  public Image transform_image (Image image, String [] args) {

    // invert, grayscale, emboss, motionblur
    try {
      // get transformation type
      String transformation = args[2];

      // transform image
      switch(transformation) {
        case "invert":
            Effects.invert(image);
            break;
        case "grayscale":
            Effects.grayscale(image);
            break;
        case "emboss":
            Effects.emboss(image);
            break;
        case "motionblur":
            int motion_blur_length = Integer.valueOf(args[3]);
            Effects.motionblur(image, motion_blur_length);
            break;
        default:
            throw new Exception("invalid tranformation => " + transformation);

      }

    } catch (Exception e) {
      System.out.println("Exception => " + e);
      System.out.println("USAGE: java ImageEditor in-file out-file (grayscale|invert|emboss|motionblur motion-blur-length)");
    } finally {
      return image;
    }
  }




  public Image load (String [] args) {

    Image image = FileHandler.load(args);
    return image;

  }

  public static void main(String [] args) {

    // create ImageEditor object
    ImageEditor imageEditor = new ImageEditor();

    // load image
    Image original_image = imageEditor.load(args);

    // perform transformation
    Image transformed_image = imageEditor.transform_image(original_image, args);

    // save image
    FileHandler.save(transformed_image, args);

  }

}
