import bagel.*;
import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Mario class, the main character of the game
 */
public class Mario extends GameObject {
    // Image resources
    private static final Image MARIO_RIGHT = new Image("res/mario_right.png");
    private static final Image MARIO_LEFT = new Image("res/mario_left.png");
    private static final Image MARIO_HAMMER_RIGHT = new Image("res/mario_hammer_right.png");
    private static final Image MARIO_HAMMER_LEFT = new Image("res/mario_hammer_left.png");
    
    // Physics parameters
    private static final double MOVE_SPEED = 3.5;
    private static final double CLIMB_SPEED = 2.0;
    private static final double JUMP_VELOCITY = -5.0;
    private static final double GRAVITY = 0.2;
    private static final double MAX_FALL_SPEED = 10.0;
    private static final double PLATFORM_TOLERANCE = 5.0;
    private static final double MAX_Y_COORD = 10000; // Prevent Y coordinate from getting too large
    
    // Direction and state
    private boolean facingRight = true;
    private boolean hasHammer = false;
    private boolean onPlatform = false;
    private boolean onLadder = false;
    private boolean isJumping = false;
    
    // Physics variables
    private double velocityY = 0;
    
    // Jump tracking
    private List<Barrel> jumpedBarrels = new ArrayList<>();
    
    /**
     * Mario constructor
     * @param x Initial x coordinate
     * @param y Initial y coordinate
     */
    public Mario(double x, double y) {
        super(MARIO_RIGHT, x, y);
        System.out.println("Mario created at position (" + x + "," + y + ")");
    }
    
    /**
     * Update Mario's state
     * @param input User input
     * @param platforms All platforms in the game
     * @param ladders All ladders in the game
     * @param barrels All barrels in the game
     * @param hammer The hammer in the game
     * @param windowWidth Window width
     * @param windowHeight Window height
     * @return Number of barrels jumped over (for score calculation)
     */
    public int update(Input input, List<Platform> platforms, List<Ladder> ladders,
                     List<Barrel> barrels, Hammer hammer, int windowWidth, int windowHeight) {
        int barrelJumpCount = 0;
        updateImage();
        
        // Check if hammer was collected
        if (!hasHammer && hammer != null && !hammer.isCollected() && this.collidesWith(hammer)) {
            hammer.collect();
            hasHammer = true;
            System.out.println("Mario collected the hammer!");
        }
        
        // Handle horizontal movement
        double oldX = x;
        double oldY = y; // Record Y coordinate before movement
        
        if (input.isDown(Keys.LEFT)) {
            facingRight = false;
            x -= MOVE_SPEED;
            System.out.println("Mario moving left");
        }
        
        if (input.isDown(Keys.RIGHT)) {
            facingRight = true;
            x += MOVE_SPEED;
            System.out.println("Mario moving right");
        }
        
        // Prevent Mario from leaving the screen
        if (x < 0) {
            x = 0;
        } else if (x + width > windowWidth) {
            x = windowWidth - width;
        }
        
        // Check if on ladder
        boolean wasOnLadder = onLadder;
        onLadder = false;
        for (Ladder ladder : ladders) {
            if (this.collidesWith(ladder) && ladder.canClimb(x, width)) {
                onLadder = true;
                System.out.println("Mario is on a ladder");
                break;
            }
        }
        
        // Handle ladder climbing
        if (onLadder) {
            if (input.isDown(Keys.UP)) {
                y -= CLIMB_SPEED;
                System.out.println("Mario climbing up");
            }
            if (input.isDown(Keys.DOWN)) {
                y += CLIMB_SPEED;
                System.out.println("Mario climbing down");
            }
            
            // Reset vertical velocity when leaving ladder
            if (!wasOnLadder) {
                velocityY = 0;
            }
            
            // Allow jumping from ladder
            if (input.wasPressed(Keys.SPACE)) {
                velocityY = JUMP_VELOCITY;
                isJumping = true;
                onLadder = false; // Leave ladder after jumping
                System.out.println("Mario jumped from ladder!");
            }
        } else {
            // Jump handling
            if (input.wasPressed(Keys.SPACE) && (onPlatform || onLadder) && !isJumping) {
                velocityY = JUMP_VELOCITY;
                isJumping = true;
                onPlatform = false;
                System.out.println("Mario jumped!");
            }
            
            // Apply gravity
            velocityY = Math.min(velocityY + GRAVITY, MAX_FALL_SPEED);
            y += velocityY;
            
            // Prevent Y coordinate from getting too large
            if (y > MAX_Y_COORD) {
                y = windowHeight - height;
                velocityY = 0;
                System.out.println("Mario Y coordinate too large, position reset (" + x + "," + y + ")");
            }
        }
        
        // Prevent Mario from leaving the screen top and bottom
        if (y < 0) {
            y = 0;
            velocityY = 0;
        } else if (y + height > windowHeight) {
            y = windowHeight - height;
            velocityY = 0;
            onPlatform = true; // Consider standing on ground if at window bottom
        }
        
        // Check if standing on platform
        onPlatform = false;
        for (Platform platform:  platforms) {
            // Check if directly standing on platform
            if (y + height >= platform.getY() && 
                y + height <= platform.getY() + PLATFORM_TOLERANCE &&
                x + width > platform.getX() && 
                x < platform.getX() + platform.getWidth()) {
                
                platform.placeOnTop(this);
                velocityY = 0;
                onPlatform = true;
                isJumping = false;
                System.out.println("Mario standing on platform " + platform.getX() + "," + platform.getY());
                break;
            }
        }
        
        // Check if jumped over barrels
        for (Barrel barrel:  barrels) {
            if (!barrel.isDestroyed()) {
                // Detect if Mario jumped over barrel
                if (isJumping && velocityY > 0 && 
                    y + height < barrel.getY() &&
                    Math.abs((x + width/2) - (barrel.getX() + barrel.getWidth()/2)) < barrel.getWidth() &&
                    !jumpedBarrels.contains(barrel)) {
                    
                    jumpedBarrels.add(barrel);
                    barrelJumpCount++;
                    System.out.println("Mario jumped over a barrel!");
                }
                
                // If has hammer, destroy barrel on collision
                if (hasHammer && this.collidesWith(barrel)) {
                    barrel.destroy();
                    System.out.println("Mario destroyed a barrel with the hammer!");
                }
            }
        }
        
        // Debug output, confirm movement logic working correctly
        if (oldX != x || oldY != y) {
            System.out.println("Mario moving (" + oldX + "," + oldY + ") -> (" + x + "," + y + ")");
        }
        
        return barrelJumpCount;
    }
    
    /**
     * Update Mario's image (based on direction and hammer status)
     */
    private void updateImage() {
        Image oldImage = image;
        
        if (hasHammer) {
            image = facingRight ? MARIO_HAMMER_RIGHT  :MARIO_HAMMER_LEFT;
        } else {
            image = facingRight ? MARIO_RIGHT : MARIO_LEFT;
        }
        
        // Update width and height, as different images may have different dimensions
        if (oldImage != image) {
            width = image.getWidth();
            height = image.getHeight();
        }
    }
    
    /**
     * Check if Mario has the hammer
     * @return Whether Mario has the hammer
     */
    public boolean hasHammer() {
        return hasHammer;
    }
    
    /**
     * Set Mario's position
     * @param x New x coordinate
     * @param y New y coordinate
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        System.out.println("Mario position set to (" + x + "," + y + ")");
    }
    
    @Override
    public void update() {
        // This method is required by the GameObject abstract class, but the parameterized update method is used instead
    }
} 