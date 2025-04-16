import bagel.Image;

/**
 * Ladder class, the ladder entity in the game
 */
public class Ladder extends GameObject {
    private static final Image LADDER_IMAGE = new Image("res/ladder.png");

    /**
     * Ladder constructor
     * @param x Ladder x coordinate
     * @param y Ladder y coordinate
     */
    public Ladder(double x, double y) {
        super(LADDER_IMAGE, x, y);
    }

    /**
     * Check if Mario can climb this ladder
     * @param marioX Mario's x coordinate
     * @param marioWidth Mario's width
     * @return Whether Mario can climb
     */
    public boolean canClimb(double marioX, double marioWidth) {
        // Check if Mario's center is within the horizontal range of the ladder
        double marioCenter = marioX + marioWidth / 2;
        return marioCenter >= this.getX() && marioCenter <= this.getX() + this.getWidth();
    }

    @Override
    public void update() {
        // Ladder does not need update logic
    }
} 