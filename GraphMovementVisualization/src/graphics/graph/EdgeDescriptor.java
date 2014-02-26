/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.graph;

/**
 *
 * @author Shl
 */
public class EdgeDescriptor {

    private final EdgeStroke outerStroke;
    private final EdgeStroke innerStroke;

    public static void initialize() throws Exception {
//        try {
//            BufferedImage road = ImageIO.read(Images.class.getResource("road.jpg"));
//            texture = new TexturePaint(road, new Rectangle(15, 15));
//        } catch (IOException ex) {
//            throw ex;
//        }
    }

    public EdgeDescriptor(EdgeStroke outerStroke, EdgeStroke innerStroke) {
        this.outerStroke = outerStroke;
        this.innerStroke = innerStroke;
    }

    public EdgeStroke getOuterStroke() {
        return outerStroke;
    }

    public EdgeStroke getInnerStroke() {
        return innerStroke;
    }


}
