package imageEditor;

// this class holds functions for transforming ppm images

public class Effects {

  public static void invert (Image image) {
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

  public static void grayscale(Image image) {

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

  public static void emboss(Image image) {

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

  public static void motionblur(Image image, int motion_blur_length) {

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

}
