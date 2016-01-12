
# compile project with
javac src/ImageEditor.java -sourcepath src/ -d bin/

# run project with
java -cp bin/ ImageEditor [input_file.ppm] [output_file.ppm] (invert | emboss | grayscale | motionblur motionblur-length)
