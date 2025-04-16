import bagel.Image;
import java.util.List;

/**
 * Donkey Kong class, the boss in the game
 */
public class DonkeyKong extends GameObject {
    private static final Image DONKEY_KONG_IMAGE = new Image("res/donkey_kong.png");
    private static final double GRAVITY = 0.2;
    private static final double MAX_FALL_SPEED = 5;
    
    private double velocityY = 0;
    private boolean isPlacedOnPlatform = false; // Flag if already placed on a platform

    /**
     * Donkey Kong constructor
     * @param x Donkey Kong's x coordinate
     * @param y Donkey Kong's y coordinate
     */
    public DonkeyKong(double x, double y) {
        super(DONKEY_KONG_IMAGE, x, y);
        System.out.println("Donkey Kong created at position (" + x + "," + y + ")");
    }

    /**
     * Update Donkey Kong's state
     * @param platforms All platforms in the game
     */
    public void update(List<Platform> platforms) {
        // If already placed on a platform, no need to update position
        if (isPlacedOnPlatform) {
            return;
        }
        
        // Apply gravity
        velocityY = Math.min(velocityY + GRAVITY, MAX_FALL_SPEED);
        double oldY = y;
        y += velocityY;

        // Check if landed on a platform
        for (Platform platform:  platforms) {
            if (platform.isOverlapping(this)) {
                platform.placeOnTop(this);
                velocityY = 0;
                isPlacedOnPlatform = true; // Set as placed on platform
                System.out.println("Donkey Kong placed on platform (" + x + "," + y + ")");
                break;
            }
        }
        
        // Debug output
        if (oldY != y) {
            System.out.println("Donkey Kong position updated (" + x + "," + oldY + ") -> (" + x + "," + y + ")");
        }
    }
    
    /**
     * Force Donkey Kong onto a specific platform
     * @param platform Target platform
     */
    public void placeOnPlatform(Platform platform) {
        if (platform != null) {
            platform.placeOnTop(this);
            velocityY = 0;
            isPlacedOnPlatform = true;
            System.out.println("Donkey Kong forcibly placed on platform (" + x + "," + y + ")");
        }
    }

    @Override
    public void update() {
        // This method is required by the GameObject abstract class, but the parameterized update method is used instead
    }
} 