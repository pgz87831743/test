import bagel.Image;
import java.util.List;

/**
 * Barrel class, obstacle in the game
 */
public class Barrel extends GameObject {
    private static final Image BARREL_IMAGE = new Image("res/barrel.png");
    private static final double GRAVITY = 0.2;
    private static final double MAX_FALL_SPEED = 5;
    
    private double velocityY = 0;
    private boolean destroyed = false;

    /**
     * Barrel constructor
     * @param x Barrel's x coordinate
     * @param y Barrel's y coordinate
     */
    public Barrel(double x, double y) {
        super(BARREL_IMAGE, x, y);
    }

    /**
     * Check if barrel is destroyed
     * @return Whether destroyed
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Destroy the barrel
     */
    public void destroy() {
        this.destroyed = true;
    }

    /**
     * Update barrel state
     * @param platforms All platforms in the game
     */
    public void update(List<Platform> platforms) {
        if (destroyed) {
            return;
        }

        // Apply gravity
        velocityY = Math.min(velocityY + GRAVITY, MAX_FALL_SPEED);
        y += velocityY;

        // Check if landed on platform
        boolean onPlatform = false;
        for (Platform platform:  platforms) {
            if (platform.isOverlapping(this)) {
                platform.placeOnTop(this);
                velocityY = 0;
                onPlatform = true;
                break;
            }
        }
    }

    @Override
    public void update() {
        // This method is required by the GameObject abstract class, but the parameterized update method is used instead
    }
} 