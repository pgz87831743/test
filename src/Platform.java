import bagel.Image;

/**
 * Platform class, the platform entity in the game
 */
public class Platform extends GameObject {
    private static final Image PLATFORM_IMAGE = new Image("res/platform.png");
    private static final double COLLISION_TOLERANCE = 5.0;

    /**
     * Platform constructor
     * @param x Platform x coordinate
     * @param y Platform y coordinate
     */
    public Platform(double x, double y) {
        super(PLATFORM_IMAGE, x, y);
    }

    /**
     * Check if an object is standing on the platform
     * @param obj Object to check
     * @return Whether the object is on top of the platform
     */
    public boolean isOnTop(GameObject obj) {
        // Check if the object's bottom is in contact with the platform's top
        double objBottom = obj.getY() + obj.getHeight();
        double platformTop = this.getY();
        
        // Horizontal check
        boolean horizontalOverlap = (obj.getX() < this.getX() + this.getWidth()) && 
                                   (obj.getX() + obj.getWidth() > this.getX());
        
        // Vertical contact check (object bottom in contact with platform top)
        boolean verticalTouch = Math.abs(objBottom - platformTop) <= COLLISION_TOLERANCE;
        
        return horizontalOverlap && verticalTouch;
    }

    /**
     * Check if an object overlaps with the platform
     * @param obj Object to check
     * @return Whether there is an overlap
     */
    public boolean isOverlapping(GameObject obj) {
        // Get object bottom edge
        double objBottom = obj.getY() + obj.getHeight();
        // Get platform top edge
        double platformTop = this.getY();
        
        // Horizontal overlap check
        boolean horizontalOverlap = (obj.getX() < this.getX() + this.getWidth()) && 
                                   (obj.getX() + obj.getWidth() > this.getX());
        
        // Vertical overlap check, object must contact platform from above
        boolean verticalOverlap = (objBottom >= platformTop - COLLISION_TOLERANCE) && 
                                 (obj.getY() < platformTop);
        
        return horizontalOverlap && verticalOverlap;
    }

    /**
     * Place an object on top of the platform
     * @param obj Object to place
     */
    public void placeOnTop(GameObject obj) {
        if (obj != null) {
            obj.setY(this.getY() - obj.getHeight());
        }
    }

    @Override
    public void update() {
        // Platform does not need update logic
    }
} 