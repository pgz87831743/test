import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Base game object class, the base class for all game entities
 */
public abstract class GameObject {
    protected Image image;
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    /**
     * Constructor
     * @param image Object image
     * @param x Initial x coordinate
     * @param y Initial y coordinate
     */
    public GameObject(Image image, double x, double y) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /**
     * Render the object
     */
    public void render() {
        image.drawFromTopLeft(x, y);
    }

    /**
     * Get the object's collision box
     * @return Collision box rectangle
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Check if colliding with another object
     * @param other Another game object
     * @return Whether collision occurred
     */
    public boolean collidesWith(GameObject other) {
        return getBoundingBox().intersects(other.getBoundingBox());
    }

    /**
     * Get the center point of the object
     * @return Center point position
     */
    public Point getCenter() {
        return new Point(x + width / 2, y + height / 2);
    }

    /**
     * Get the object's x coordinate
     * @return x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Get the object's y coordinate
     * @return y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Get the object's width
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Get the object's height
     * @return height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Set the object's x coordinate
     * @param x New x coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the object's y coordinate
     * @param y New y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Update object state
     */
    public abstract void update();
} 