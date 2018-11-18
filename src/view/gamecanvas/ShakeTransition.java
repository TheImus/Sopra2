package view.gamecanvas;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.util.Duration;

class ShakeTransition extends Transition {

	//private final Interpolator WEB_EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
    private final Interpolator WEB_EASE_X = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
    private final Interpolator WEB_EASE_Y = Interpolator.SPLINE(0.25, 0.8, 0.1, 1);
    private final Timeline timeline;
    private final Node node;
    private boolean oldCache = false;
    private CacheHint oldCacheHint = CacheHint.DEFAULT;
    private final boolean useCache = false; // was true, cache can not be used :( this breaks the shake of 90 or 270 degree tokens
    private final double xInitial;
    private final double yInitial;
    
    private final double DURATION = 600;
    private final int SHAKE_DELAY = 0;
    private final int FRAMES = 10;
    private final double FRAME_TIME = DURATION / (double) FRAMES;
    

    private final DoubleProperty xPosition = new SimpleDoubleProperty();
    private final DoubleProperty yPosition = new SimpleDoubleProperty();

    /**
    * Create new ShakeTransition
    * 
    * @param node The node to affect
    */
    public ShakeTransition(final Node node, EventHandler<ActionEvent> event) {
        this.node=node;
        
        statusProperty().addListener((ov, t, newStatus) -> {
           switch(newStatus) {
               case RUNNING:
                   starting();
                   break;
               default:
                   stopping();
                   break;
           }
        });

        this.timeline= new Timeline(
        	/* PERFECT LINE
    	    // x value
            new KeyFrame(Duration.millis(FRAME_TIME * 0), new KeyValue(xPosition, 0, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 1), new KeyValue(xPosition, -10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 2), new KeyValue(xPosition, 10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 3), new KeyValue(xPosition, -10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 4), new KeyValue(xPosition, 10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 5), new KeyValue(xPosition, -10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 6), new KeyValue(xPosition, 10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 7), new KeyValue(xPosition, -10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 8), new KeyValue(xPosition, 10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 9), new KeyValue(xPosition, -10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 10), new KeyValue(xPosition, 0, WEB_EASE)),
            // y value
            new KeyFrame(Duration.millis(FRAME_TIME * 0), new KeyValue(yPosition, 0, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 1), new KeyValue(yPosition, -10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 2), new KeyValue(yPosition, 10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 3), new KeyValue(yPosition, -10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 4), new KeyValue(yPosition, 10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 5), new KeyValue(yPosition, -10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 6), new KeyValue(yPosition, 10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 7), new KeyValue(yPosition, -10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 8), new KeyValue(yPosition, 10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 9), new KeyValue(yPosition, -10, WEB_EASE)),
            new KeyFrame(Duration.millis(FRAME_TIME * 10), new KeyValue(yPosition, 0, WEB_EASE))
            */
        	// x value
            new KeyFrame(Duration.millis(FRAME_TIME * 0), new KeyValue(xPosition, 0, WEB_EASE_X)),
            new KeyFrame(Duration.millis(FRAME_TIME * 1), new KeyValue(xPosition, -5, WEB_EASE_X)),
            new KeyFrame(Duration.millis(FRAME_TIME * 2), new KeyValue(xPosition, 10, WEB_EASE_X)),
            new KeyFrame(Duration.millis(FRAME_TIME * 3), new KeyValue(xPosition, -10, WEB_EASE_X)),
            new KeyFrame(Duration.millis(FRAME_TIME * 4), new KeyValue(xPosition, 2, WEB_EASE_X)),
            new KeyFrame(Duration.millis(FRAME_TIME * 5), new KeyValue(xPosition, -8, WEB_EASE_X)),
            new KeyFrame(Duration.millis(FRAME_TIME * 6), new KeyValue(xPosition, 10, WEB_EASE_X)),
            new KeyFrame(Duration.millis(FRAME_TIME * 7), new KeyValue(xPosition, -2, WEB_EASE_X)),
            new KeyFrame(Duration.millis(FRAME_TIME * 8), new KeyValue(xPosition, 7, WEB_EASE_X)),
            new KeyFrame(Duration.millis(FRAME_TIME * 9), new KeyValue(xPosition, -8, WEB_EASE_X)),
            new KeyFrame(Duration.millis(FRAME_TIME * 10), new KeyValue(xPosition, 0, WEB_EASE_X)),
            // y value
            new KeyFrame(Duration.millis(FRAME_TIME * 0), new KeyValue(yPosition, 0, WEB_EASE_Y)),
            new KeyFrame(Duration.millis(FRAME_TIME * 1), new KeyValue(yPosition, 10, WEB_EASE_Y)),
            new KeyFrame(Duration.millis(FRAME_TIME * 2), new KeyValue(yPosition, 2, WEB_EASE_Y)),
            new KeyFrame(Duration.millis(FRAME_TIME * 3), new KeyValue(yPosition, -9, WEB_EASE_Y)),
            new KeyFrame(Duration.millis(FRAME_TIME * 4), new KeyValue(yPosition, 10, WEB_EASE_Y)),
            new KeyFrame(Duration.millis(FRAME_TIME * 5), new KeyValue(yPosition, 2, WEB_EASE_Y)),
            new KeyFrame(Duration.millis(FRAME_TIME * 6), new KeyValue(yPosition, -5, WEB_EASE_Y)),
            new KeyFrame(Duration.millis(FRAME_TIME * 7), new KeyValue(yPosition, 6, WEB_EASE_Y)),
            new KeyFrame(Duration.millis(FRAME_TIME * 8), new KeyValue(yPosition, -8, WEB_EASE_Y)),
            new KeyFrame(Duration.millis(FRAME_TIME * 9), new KeyValue(yPosition, 9, WEB_EASE_Y)),
            new KeyFrame(Duration.millis(FRAME_TIME * 10), new KeyValue(yPosition, 0, WEB_EASE_Y)),
            
            // opacity
            new KeyFrame(Duration.millis(FRAME_TIME * 0), new KeyValue(node.opacityProperty(), node.getOpacity(), Interpolator.LINEAR)),
            new KeyFrame(Duration.millis(FRAME_TIME * 10), new KeyValue(node.opacityProperty(), 0, Interpolator.LINEAR))
        );
        //xIni=node.getScene().getWindow().getX();
        xInitial = node.getTranslateX();
        yInitial = node.getTranslateY();

        //xPosition.addListener((ob,n,n1)->(node.getScene().getWindow()).setX(xIni+n1.doubleValue()));
        xPosition.addListener((ob, n, n1) -> node.setTranslateX(xInitial+n1.doubleValue()));
        yPosition.addListener((ob, n, n1) -> node.setTranslateY(yInitial+n1.doubleValue()));

        setCycleDuration(Duration.millis(DURATION));
        setDelay(Duration.millis(SHAKE_DELAY));
        setOnFinished(event);
    } 

    /**
    * Called when the animation is starting
    */
    protected final void starting() {
        if (useCache) {
            oldCache = node.isCache();
            oldCacheHint = node.getCacheHint();
            node.setCache(true);
            node.setCacheHint(CacheHint.SPEED);
        }
    }

    /**
    * Called when the animation is stopping
    */
    protected final void stopping() {
        if (useCache) {
            node.setCache(oldCache);
            node.setCacheHint(oldCacheHint);
        }
    }

    @Override 
    protected void interpolate(double d) {
        timeline.playFrom(Duration.seconds(d));
        timeline.stop();
    }
}