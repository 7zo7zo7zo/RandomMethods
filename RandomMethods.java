public class RandomMethods {

    public static void print2DArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static int[][] multiply2DArray(int[][] array, int size) {
        int[][] newArray = new int[size * array.length][size * array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                for (int k = 0; k < size; k++) {
                    for (int f = 0; f < size; f++) {
                        newArray[(size * i) + k][(size * j) + f] = array[i][j];
                    }
                }
            }
        }
        return newArray;
    }
    
    public BufferedImage getMinimap(int[][] map) {
        BufferedImage image = new BufferedImage(map[0].length, map.length, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        int count = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] != 0) {
                    pixels[count] = Color.RED.getRGB();
                } else {
                    pixels[count] = Color.WHITE.getRGB();
                }
                count++;
            }
        }
        return image;
    }
    
    public int darken(int value, int amount) {
        int red = (value >> 16) & 0xFF;
        int green = (value >> 8) & 0xFF;
        int blue = (value) & 0xFF;

        red -= amount;
        green -= amount;
        blue -= amount;

        if(red < 0) red = 0;
        if(red > 255) red = 255;
        if(green < 0) green = 0;
        if(green > 255) green = 255;
        if(blue < 0) blue = 0;
        if(blue > 255) blue = 255;

        return (red << 16) + (green << 8) + blue;
    }
    
    double x;
    double y;
    double[] xValues;
    double[] yValues;
    
    public void render(Graphics2D g2) {
        g2.setColor(Color.RED);
        for(int i = 1; i < xValues.length; i++) {
            g2.drawLine((int) (xValues[i - 1] + x), (int) (yValues[i - 1] + y), (int) (xValues[i] + x), (int) (yValues[i] + y));
        }
        g2.drawLine((int) (xValues[xValues.length - 1] + x), (int) (yValues[yValues.length - 1] + y), (int) (xValues[0] + x), (int) (yValues[0] + y));
    }
    
    public void rotate(double a) {
        double totalX = 0;
        double totalY = 0;
        for(int i = 0; i < xValues.length; i++) {
            totalX += xValues[i];
            totalY += yValues[i];
        }

        double centroidX = totalX/xValues.length;
        double centroidY = totalY/yValues.length;

        for(int i = 0; i < xValues.length; i++) {
            double xDist = xValues[i] - centroidX;
            double yDist = yValues[i] - centroidY;
            double angle = Math.atan(yDist/xDist);
            if(xDist < 0 && yDist >= 0) angle += Math.PI;
            if(xDist < 0 && yDist < 0) angle -= Math.PI;
            double hypot = Math.sqrt(Math.pow(yDist, 2) + Math.pow(xDist, 2));
            angle += a;
            xValues[i] = Math.cos(angle) * hypot + centroidX;
            yValues[i] = Math.sin(angle) * hypot + centroidY;
        }
    }


    public boolean collides(Entity e, boolean swapped) {
        int length = xValues.length;
        int lengthE = e.xValues.length;
        double a;
        double n;
        double xDist;
        double yDist;
        double x1;
        double y1;
        double xf;
        double yf;
        double hypot;
        double min = 10000;
        double max = 0;
        double minE = 10000;
        double maxE = 0;


        for(int i = 1; i < length; i++) {
            xDist = xValues[i] - xValues[i - 1];
            yDist = yValues[i] - yValues[i - 1];
            a = Math.atan(yDist / xDist);
            if (xDist < 0 && yDist >= 0) a += Math.PI;
            if (xDist < 0 && yDist < 0) a -= Math.PI;

            n = a + Math.PI / 2;
            for(int j = 0; j < length; j++) {
                x1 = xValues[j] + x;
                y1 = yValues[j] + y;
                xf = (-Math.tan(a) * x1 + y1)/(Math.tan(n) - Math.tan(a));
                yf = Math.tan(n) * xf;
                hypot = Math.sqrt(Math.pow(xf, 2) + Math.pow(yf, 2));
                if(hypot < min) {
                    min = hypot;
                }
                if(hypot > max) {
                    max = hypot;
                }
            }
            for(int j = 0; j < lengthE; j++) {
                x1 = e.xValues[j] + e.x;
                y1 = e.yValues[j] + e.y;
                xf = (-Math.tan(a) * x1 + y1)/(Math.tan(n) - Math.tan(a));
                yf = Math.tan(n) * xf;
                hypot = Math.sqrt(Math.pow(xf, 2) + Math.pow(yf, 2));
                if(hypot < minE) {
                    minE = hypot;
                }
                if(hypot > maxE) {
                    maxE = hypot;
                }
            }
            if(!testOverlap(min, max, minE, maxE)) {
                return false;
            }

        }
        xDist = xValues[0] - xValues[length - 1];
        yDist = yValues[0] - yValues[length - 1];
        a = Math.atan(yDist / xDist);
        if (xDist < 0 && yDist >= 0) a += Math.PI;
        if (xDist < 0 && yDist < 0) a -= Math.PI;
        n = a + Math.PI / 2;

        for(int j = 0; j < length; j++) {
            x1 = xValues[j] + x;
            y1 = yValues[j] + y;
            xf = (-Math.tan(a) * x1 + y1)/(Math.tan(n) - Math.tan(a));
            yf = Math.tan(n) * xf;
            hypot = Math.sqrt(Math.pow(xf, 2) + Math.pow(yf, 2));
            if(hypot < min) {
                min = hypot;
            }
            if(hypot > max) {
                max = hypot;
            }
        }
        for(int j = 0; j < lengthE; j++) {
            x1 = e.xValues[j] + e.x;
            y1 = e.yValues[j] + e.y;
            xf = (-Math.tan(a) * x1 + y1)/(Math.tan(n) - Math.tan(a));
            yf = Math.tan(n) * xf;
            hypot = Math.sqrt(Math.pow(xf, 2) + Math.pow(yf, 2));
            if(hypot < minE) {
                minE = hypot;
            }
            if(hypot > maxE) {
                maxE = hypot;
            }
        }
        if(!testOverlap(min, max, minE, maxE)) {
            return false;
        }
        if(!swapped) {
            return e.collides(this, true);
        }
        return true;
    }

    boolean testOverlap(double x1, double x2, double y1, double y2) {
        return (x1 >= y1 && x1 <= y2) ||
                (x2 >= y1 && x2 <= y2) ||
                (y1 >= x1 && y1 <= x2) ||
                (y2 >= x1 && y2 <= x2);
    }
}
