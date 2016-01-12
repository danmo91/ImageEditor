
public class Image {

  // fields
  public int width;
  public int height;
  public int max_color_value;
  public Pixel[][] pixels;

  public void print() {
    System.out.println("Image { width: " + width + ", height: " + height + ", max_color_value: " + max_color_value + "}");

    for (int row = 0; row < height; row++) {
      System.out.println("");
      System.out.println("row => " + row);
      for (int col = 0; col < width; col++) {
        pixels[row][col].print();
      }
    }


  }

}
