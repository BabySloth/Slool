package SloolDirect;

import javafx.scene.paint.Color;

public enum DesignEnum {
    RED(new Color(1, 0.2431, 0.2549, 1)),
    GREEN(new Color(0.3255, 0.8667, 0.4235, 1)),
    GREY(new Color(0.8196, 0.8078, 0.8118, 1)),
    BLUE(new Color(0.0784, 0.5843, 0.8784, 1)),
    YELLOW(new Color( 0.9765, 0.8824, 0.3255, 1)),
    DARK_RED(new Color(0.8196, 0, 0.0784, 1));


    private final Color color;

    DesignEnum(Color designColor) {
        color = designColor;
    }

    public Color getColor() {
        return color;
    }
}
