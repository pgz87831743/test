import bagel.Image;

/**
 * Hammer class, item in the game
 */
public class Hammer extends GameObject {
    private static final Image HAMMER_IMAGE = new Image("res/hammer.png");
    private boolean collected = false;

    /**
     * Hammer constructor
     * @param x Hammer's x coordinate
     * @param y Hammer's y coordinate
     */
    public Hammer(double x, double y) {
        super(HAMMER_IMAGE, x, y);
    }

    /**
     * Check if hammer is collected
     * @return Whether collected
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Collect the hammer
     */
    public void collect() {
        this.collected = true;
    }

    @Override
    public void render() {
        if (!collected) {
            super.render();
        }
    }

    @Override
    public void update() {
        // Hammer does not need update logic
    }
} 