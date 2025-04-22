import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
public class Steganography {
    /**
    * Clear the lower (rightmost) two bits in a pixel.
    */
    public static void clearLow( Pixel p ){ 
        Color OldColor = p.getColor();
        p.setRed(4 * (OldColor.getRed()/4));
        p.setGreen(4 * (OldColor.getGreen()/4)); 
        p.setBlue(4 * (OldColor.getBlue()/4));
    }
    public static Picture testClearLow( Picture pic ){
        Picture pix = new Picture(pic);
        for(Pixel p : pix.getPixels()){
            clearLow(p);
        }
        return pix;
    }
    /**
    * Set the lower 2 bits in a pixel to the highest 2 bits in c
    */
   public static void setLow (Pixel p, Color c){
        clearLow(p);
        Color OldColor = p.getColor();
        p.setRed(OldColor.getRed() + c.getRed()/64);
        p.setGreen(OldColor.getGreen() + c.getGreen()/64);
        p.setBlue(OldColor.getBlue() + c.getBlue()/64);
        
    }   
    public static Picture testSetLow( Picture pic , Color col){
        for(Pixel p : pic.getPixels()){
            setLow(p, col);
        }
        return pic;
    }
    /**
    * Determines whether secret can be hidden in source, which is
    * true if source and secret are the same dimensions.
    * @param source is not null
    * @param secret is not null
    * @return true if secret can be hidden in source, false otherwise.
    */
    public static boolean canHide(Picture source, Picture secret){
        boolean canHide = false;
        if (source.getHeight() == secret.getHeight() && source.getWidth() == secret.getWidth()){
            canHide = true;
        }
        return canHide;
    }
    /**
    * Creates a new Picture with data from secret hidden in data from source
    * @param source is not null
    * @param secret is not null
    * @return combined Picture with secret hidden in source
    * precondition: source is same width and height as secret
    */
    public static Picture hidePicture(Picture source, Picture secret, int startX, int startY){
        Picture hide = new Picture(source);
        Pixel[][] hpixels = hide.getPixels2D();
        Pixel[][] secretPixels = secret.getPixels2D();
        for (int r = startX, strtX = 0; r < hpixels.length && strtX < secretPixels.length; r++, strtX++){
            for (int c = startY, strtY = 0; c < hpixels[0].length && strtY < secretPixels[0].length; c++, strtY++){
                Pixel s = secretPixels[strtX][strtY];
                Color col = s.getColor();
                setLow(hpixels[r][c], col);
            }
        }
        return hide;
    }
    public static Picture revealPicture(Picture hidden) {
        Picture copy = new Picture(hidden);
        Pixel[][] pixels = copy.getPixels2D();
        Pixel[][] source = hidden.getPixels2D();
        for (int r = 0; r < pixels.length; r++){
            for (int c = 0; c < pixels[0].length; c++){
                Color col = source[r][c].getColor();
                Pixel p = pixels[r][c];
                p.setRed((col.getRed() % 4 * 64));
                p.setBlue((col.getBlue() % 4 * 64));
                p.setGreen((col.getGreen()% 4 * 64));
            }
        }
        return copy;
    }
    public static Boolean isSame(Picture pic1, Picture pic2){
        Boolean isSame = false;
        if (pic1.getHeight() == pic2.getHeight() && pic1.getWidth() == pic2.getWidth()){
            isSame = true;
        }
        return isSame;
    }
    public static ArrayList findDifference(Picture pic1, Picture pic2){
        ArrayList<Point> diff = new ArrayList<Point>();
        if (isSame(pic1, pic2) == true){
            for (int r = 0; r < pic1.getHeight(); r++){
                for (int c = 0; c < pic1.getWidth(); c++){
                    if (!pic1.getPixel(c, r).getColor().equals(pic2.getPixel (c, r).getColor())){
                        Point p = new Point(c, r);
                        diff.add(p);
                    }
                }
            }   
        }
        return diff;
    }
    public static Picture showDifferentArea(Picture Pic, ArrayList<Point> Diff) {
        Picture NewPic = new Picture(Pic);
        
        if (Diff == null || Diff.isEmpty()) {
        return NewPic;
        }
        
        int minX = (int) Diff.get(0).getX();
        int minY = (int) Diff.get(0).getY();
        int maxX = minX;
        int maxY = minY;
        
        for (Point p : Diff) {
            int x = (int) p.getX();
            int y = (int) p.getY();
            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        
        Color MakeRed = Color.RED;
        
        for (int x = minX; x <= maxX; x++) {
        NewPic.getPixel(x, minY).setColor(MakeRed);
        NewPic.getPixel(x, maxY).setColor(MakeRed);
        }
        
        for (int y = minY; y <= maxY; y++) {
        NewPic.getPixel(minX, y).setColor(MakeRed);
        NewPic.getPixel(maxX, y).setColor(MakeRed);
        }
        
        return NewPic;
    }
    /**
    * Takes a string consisting of letters and spaces and
    * encodes the string into an arraylist of integers.
    * The integers are 1-26 for A-Z, 27 for space, and 0 for end of
    * string. The arraylist of integers is returned.
    * @param s string consisting of letters and spaces
    * @return ArrayList containing integer encoding of uppercase
    * version of s
    */
    public static ArrayList<Integer> encodeString(String s) {
        s = s.toUpperCase();
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < s.length(); i++) {
            if (s.substring(i,i+1).equals(" ")) {
            result.add(27);
            } else {
            result.add(alpha.indexOf(s.substring(i,i+1))+1);
            }
        }
        result.add(0);
        return result;
    } 
    /**
    * Returns the string represented by the codes arraylist.
    * 1-26 = A-Z, 27 = space
    * @param codes encoded string
    * @return decoded string
    */
    public static String decodeString(ArrayList<Integer> codes)
    {
    String result="";
    String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    for (int i=0; i < codes.size(); i++) {
        if (codes.get(i) == 27) {
        result = result + " ";
        } else {
        result = result +
        alpha.substring(codes.get(i)-1,codes.get(i));
        }
    }
    return result;
    }
    /**
    * Given a number from 0 to 63, creates and returns a 3-element
    * int array consisting of the integers representing the
    * pairs of bits in the number from right to left.
    * @param num number to be broken up
    * @return bit pairs in number
    */
    private static int[] getBitPairs(int num) {
    int[] bits = new int[3];
    int code = num;
    for (int i = 0; i < 3; i++) {
        bits[i] = code % 4;
        code = code / 4;
    }
    return bits;
    }
    /**
    * Hide a string (must be only capital letters and spaces) in a
    * picture.
    * The string always starts in the upper left corner.
    * @param source picture to hide string in
    * @param s string to hide
    * @return picture with hidden string
    */
    public static boolean hideText(Picture source, String s) {
        ArrayList<Integer> codes = encodeString(s); // Includes the stop code (0)
        Pixel[][] pixels = source.getPixels2D();
        
        if (codes.size() > pixels.length * pixels[0].length) {
        return false; // Not enough pixels to hide the message
        }
        
        int index = 0;
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                if (index >= codes.size()){
                    return true;
                }
                Pixel p = pixels[row][col];
                int[] bits = getBitPairs(codes.get(index));
                
                int red = p.getRed() / 4 * 4 + bits[0];
                int green = p.getGreen() / 4 * 4 + bits[1];
                int blue = p.getBlue() / 4 * 4 + bits[2];
                
                p.setRed(red);
                p.setGreen(green);
                p.setBlue(blue);
                
                index++;
            }
        }
        return true;
    }
    /**
    * Returns a string hidden in the picture
    * @param source picture with hidden string
    * @return revealed string
    */
    public static String revealText(Picture source){
        Pixel[][] pixels = source.getPixels2D();
        ArrayList<Integer> codes = new ArrayList<Integer>();
        for (Pixel[] pixel : pixels) {
            for (int col = 0; col < pixels[0].length; col++) {
                Pixel p = pixel[col];
                int redBits = p.getRed() % 4;
                int greenBits = p.getGreen() % 4;
                int blueBits = p.getBlue() % 4;
                int code = blueBits * 16 + greenBits * 4 + redBits;
                if (code == 0) {
                    return decodeString(codes);
                }
                codes.add(code);
            }
        }
        return decodeString(codes);
    }
    public static void main(String[] args){
        // Picture beach2 = new Picture ("beach.jpg");
        // beach2.explore();
        // Picture copy2 = testSetLow(beach2, Color.PINK);
        // copy2.explore(); 
        // Picture copy3 = revealPicture(copy2);
        // copy3.explore(); 
        
        // Picture beach = new Picture ("beach.jpg");
        // Picture Flower1 = new Picture ("flower2.jpg");
        // Picture HiddenPic = hidePicture(beach, Flower1, 380, 540);
        // HiddenPic.explore();
        // Picture RevealPic = revealPicture(HiddenPic);
        // RevealPic.explore();

        // Picture swan = new Picture("swan.jpg");
        // Picture swan2 = new Picture("swan.jpg");
        // System.out.println("Swan and swan2 are the same: " + isSame(swan, swan2));
        // swan = testClearLow(swan);
        // System.out.println("Swan and swan2 are the same (after clearLow run on swan): " + isSame(swan, swan2));

        // Picture arch = new Picture("arch.jpg");
        // Picture koala = new Picture("koala.jpg") ;
        // Picture robot1 = new Picture("robot.jpg");
        // Picture arch2 = new Picture("arch.jpg");
        // ArrayList<Point> pointList = findDifference(arch, arch2);
        // System.out.println("PointList after comparing two identical pictures " + "has a size of " + pointList.size());
        // pointList = findDifference(arch, koala);
        // System.out.println("PointList after comparing two different sized pictures " + "has a size of " + pointList.size());
        // arch2 = hidePicture(arch, robot1, 65, 102);
        // pointList = findDifference(arch, arch2);
        // System.out.println("Pointlist after hiding a picture has a size of " + pointList.size());
        // arch.show();
        // arch2.show();

        // Picture hall = new Picture("femaleLionAndHall.jpg");
        // Picture robot2 = new Picture("robot.jpg");
        // Picture flower2 = new Picture("flower1.jpg");
        // // hide pictures
        // Picture hall2 = hidePicture(hall, robot2, 50, 300);
        // Picture hall3 = hidePicture(hall2, flower2, 115, 275);
        // hall3.explore();
        // if(isSame(hall, hall3)) {
        //     Picture hall4 = showDifferentArea(hall, findDifference(hall, hall3));
        //     hall4.show();
        //     Picture unhiddenHall3 = revealPicture(hall3);
        //     unhiddenHall3.show();
        // } 
        Picture source = new Picture("beach.jpg");
        String s = "Tim Weng Smells";
        hideText(source, s);
        System.out.println("Hidden string: " + s);
        source.explore();
        String revealed = revealText(source);
        System.out.println("Revealed string: " + revealed);
    }
}