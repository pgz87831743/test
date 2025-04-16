import bagel.*;
import bagel.util.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The main class for the Shadow Donkey Kong game.
 * This class extends {@code AbstractGame} and is responsible for managing game initialization,
 * updates, rendering, and handling user input.
 *
 * It sets up the game world, initializes characters, platforms, ladders, and other game objects,
 * and runs the game loop to ensure smooth gameplay.
 */
public class ShadowDonkeyKong extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;
    
    // Game configuration
    private final int WIDTH;
    private final int HEIGHT;
    private final int MAX_FRAMES;
    private final Font TITLE_FONT;
    private final Font PROMPT_FONT;
    private final Font SCORE_FONT;
    private final Font GAME_END_FONT;
    
    // Game state
    private GameState gameState = GameState.HOME_SCREEN;
    private int currentFrame = 0;
    private int score = 0;
    private int destroyedBarrels = 0;
    private int jumpedBarrels = 0;
    
    // Game objects
    private Mario mario;
    private DonkeyKong donkeyKong;
    private Hammer hammer;
    private Image backgroundImage;
    private List<Platform> platforms = new ArrayList<>();
    private List<Ladder> ladders = new ArrayList<>();
    private List<Barrel> barrels = new ArrayList<>();

    /**
     * Constructor, initializes the game
     * @param gameProps Game configuration properties
     * @param messageProps Game message properties
     */
    public ShadowDonkeyKong(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;
        
        // Initialize game configuration
        WIDTH = Integer.parseInt(GAME_PROPS.getProperty("window.width"));
        HEIGHT = Integer.parseInt(GAME_PROPS.getProperty("window.height"));
        MAX_FRAMES = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames"));
        
        // Initialize fonts
        String fontPath = GAME_PROPS.getProperty("font");
        int titleFontSize = Integer.parseInt(GAME_PROPS.getProperty("home.title.fontSize"));
        int promptFontSize = Integer.parseInt(GAME_PROPS.getProperty("home.prompt.fontSize"));
        int scoreFontSize = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.score.fontSize"));
        int gameEndFontSize = Integer.parseInt(GAME_PROPS.getProperty("gameEnd.status.fontSize"));
        
        TITLE_FONT = new Font(fontPath, titleFontSize);
        PROMPT_FONT = new Font(fontPath, promptFontSize);
        SCORE_FONT = new Font(fontPath, scoreFontSize);
        GAME_END_FONT = new Font(fontPath, gameEndFontSize);
        
        // Load background image
        backgroundImage = new Image(GAME_PROPS.getProperty("backgroundImage"));
        
        // Initialize game objects
        initGameObjects();
    }

    /**
     * Initialize game objects
     */
    private void initGameObjects() {
        System.out.println("Starting game object initialization...");
        
        // Create platforms first
        String platformsStr = GAME_PROPS.getProperty("platforms");
        String[] platformCoords = platformsStr.split(";");
        for (String coord:  platformCoords) {
            String[] xy = coord.split(",");
            double x = Double.parseDouble(xy[0]);
            double y = Double.parseDouble(xy[1]);
            
            // Ensure platform Y coordinate is within window bounds
            if (y >= 0 && y < HEIGHT) {
                platforms.add(new Platform(x, y));
            } else {
                // If out of bounds, place it in the visible area of the window
                double adjustedY = Math.min(y, HEIGHT - 50);
                platforms.add(new Platform(x, adjustedY));
                System.out.println("Platform Y coordinate out of bounds, adjusted to " + adjustedY);
            }
        }
        
        // Ensure there's at least one platform at the top for Donkey Kong
        boolean hasTopPlatform = false;
        Platform topPlatform = null;
        for (Platform platform:  platforms) {
            if (platform.getY() < HEIGHT / 4) {
                hasTopPlatform = true;
                topPlatform = platform;
                break;
            }
        }
        
        // If no top platform, add one
        if (!hasTopPlatform) {
            topPlatform = new Platform(WIDTH / 2 - 100, 100);
            platforms.add(0, topPlatform); // Add to beginning of platforms list
            System.out.println("Added top platform for Donkey Kong (" + topPlatform.getX() + "," + topPlatform.getY() + ")");
        }
        
        // Create ladders
        int ladderCount = Integer.parseInt(GAME_PROPS.getProperty("ladder.count"));
        for (int i = 1; i <= ladderCount; i++) {
            String coordStr = GAME_PROPS.getProperty("ladder." + i);
            if (coordStr != null) {
                String[] xy = coordStr.split(",");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                
                // Ensure ladder Y coordinate is within window bounds
                if (y < 0 || y >= HEIGHT) {
                    y = Math.min(y, HEIGHT - 100);
                    System.out.println("Ladder Y coordinate out of bounds, adjusted to " + y);
                }
                
                Ladder ladder = new Ladder(x, y);
                
                // Ensure ladder is on top of a platform
                for (Platform platform:  platforms) {
                    if (platform.isOverlapping(ladder)) {
                        platform.placeOnTop(ladder);
                        break;
                    }
                }
                
                ladders.add(ladder);
            }
        }
        
        // Check if there's a ladder to the top platform
        boolean hasLadderToTop = false;
        for (Ladder ladder:  ladders) {
            // Check if ladder is close to top platform
            if (topPlatform != null && 
                Math.abs(ladder.getX() - topPlatform.getX()) < topPlatform.getWidth() &&
                ladder.getY() < HEIGHT / 2) {
                hasLadderToTop = true;
                break;
            }
        }
        
        // If no ladder to the top, add one
        if (!hasLadderToTop && topPlatform != null) {
            double ladderX = topPlatform.getX() + 50; // A reasonable position on the platform
            Ladder topLadder = new Ladder(ladderX, topPlatform.getY() + 50);
            // Ensure ladder is on top of platform
            topPlatform.placeOnTop(topLadder);
            ladders.add(topLadder);
            System.out.println("Added ladder to top (" + topLadder.getX() + "," + topLadder.getY() + ")");
        }
        
        // Create barrels
        int barrelCount = Integer.parseInt(GAME_PROPS.getProperty("barrel.count"));
        for (int i = 1; i <= barrelCount; i++) {
            String coordStr = GAME_PROPS.getProperty("barrel." + i);
            if (coordStr != null) {
                String[] xy = coordStr.split(",");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                
                // Ensure barrel Y coordinate is within window bounds
                if (y < 0 || y >= HEIGHT) {
                    y = Math.min(y, HEIGHT - 100);
                    System.out.println("Barrel Y coordinate out of bounds, adjusted to " + y);
                }
                
                Barrel barrel = new Barrel(x, y);
                
                // Ensure barrel is on top of a platform
                for (Platform platform:  platforms) {
                    if (platform.isOverlapping(barrel)) {
                        platform.placeOnTop(barrel);
                        break;
                    }
                }
                
                barrels.add(barrel);
            }
        }
        
        // Create hammer - use correct attributes hammer.x/y
        double hammerX = Double.parseDouble(GAME_PROPS.getProperty("hammer.x"));
        double hammerY = Double.parseDouble(GAME_PROPS.getProperty("hammer.y"));
        
        // Ensure hammer Y coordinate is within window bounds
        if (hammerY < 0 || hammerY >= HEIGHT) {
            hammerY = Math.min(hammerY, HEIGHT - 100);
            System.out.println("Hammer Y coordinate out of bounds, adjusted to " + hammerY);
        }
        
        hammer = new Hammer(hammerX, hammerY);
        
        // Create Donkey Kong - use correct attributes donkey.x/y
        double donkeyX = Double.parseDouble(GAME_PROPS.getProperty("donkey.x"));
        double donkeyY = Double.parseDouble(GAME_PROPS.getProperty("donkey.y"));
        
        // Ensure Donkey Kong Y coordinate is within top area of window
        if (donkeyY > HEIGHT / 4 || donkeyY < 0) {
            donkeyY = 100; // Place at top area
            System.out.println("Donkey Kong Y coordinate out of ideal range, adjusted to " + donkeyY);
        }
        
        donkeyKong = new DonkeyKong(donkeyX, donkeyY);
        
        // Ensure Donkey Kong is on top of a platform
        boolean donkeyPlaced = false;
        for (Platform platform:  platforms) {
            if (platform.getY() < HEIGHT / 4) { // Consider only top platforms
                if (Math.abs(platform.getX() - donkeyX) < platform.getWidth()) {
                    // Place on suitable platform
                    ((DonkeyKong)donkeyKong).placeOnPlatform(platform);
                    donkeyPlaced = true;
                    System.out.println("Donkey Kong placed on top platform (" + donkeyKong.getX() + "," + donkeyKong.getY() + ")");
                    break;
                }
            }
        }
        
        // If no suitable platform found, try placing on first platform
        if (!donkeyPlaced && !platforms.isEmpty()) {
            Platform firstPlatform = platforms.get(0);
            donkeyKong.setX(firstPlatform.getX() + 50);
            ((DonkeyKong)donkeyKong).placeOnPlatform(firstPlatform);
            System.out.println("Donkey Kong placed on first platform (" + donkeyKong.getX() + "," + donkeyKong.getY() + ")");
        }
        
        // Create Mario (last to ensure other objects are placed)
        double marioX = Double.parseDouble(GAME_PROPS.getProperty("mario.start.x"));
        double marioY = Double.parseDouble(GAME_PROPS.getProperty("mario.start.y"));
        
        // Check Mario's Y coordinate is within reasonable range
        if (marioY < 0 || marioY >= HEIGHT) {
            marioY = HEIGHT - 100; // If out of range, place near bottom of window
            System.out.println("Mario Y coordinate out of bounds, adjusted to " + marioY);
        }
        
        mario = new Mario(marioX, marioY);
        
        // Ensure Mario is on top of a platform
        boolean onAnyPlatform = false;
        for (Platform platform:  platforms) {
            if (platform.isOverlapping(mario)) {
                platform.placeOnTop(mario);
                onAnyPlatform = true;
                System.out.println("Mario placed on platform (" + mario.getX() + "," + mario.getY() + ")");
                break;
            }
        }
        
        // If Mario is not on any platform, force place on lowest platform
        if (!onAnyPlatform && !platforms.isEmpty()) {
            // Find lowest platform
            Platform lowestPlatform = platforms.get(0);
            for (Platform platform:  platforms) {
                if (platform.getY() > lowestPlatform.getY()) {
                    lowestPlatform = platform;
                }
            }
            
            mario.setX(lowestPlatform.getX() + 50); // A reasonable position on the platform
            lowestPlatform.placeOnTop(mario);
            System.out.println("Mario not on any platform, placed to lowest platform (" + mario.getX() + "," + mario.getY() + ")");
        }

        // Initial update all game objects once to ensure they are in correct positions
        updateAllGameObjects();
        System.out.println("Game initialization completed. Mario position (" + mario.getX() + "," + mario.getY() + ")");
        System.out.println("Donkey Kong position (" + donkeyKong.getX() + "," + donkeyKong.getY() + ")");
    }

    /**
     * Update all game objects
     */
    private void updateAllGameObjects() {
        for (Platform platform:  platforms) {
            platform.update();
        }
        
        for (Ladder ladder:  ladders) {
            ladder.update();
        }
        
        // Update Donkey Kong
        donkeyKong.update(platforms);
        
        // Update all barrels
        for (Barrel barrel:  barrels) {
            barrel.update(platforms);
        }
    }

    /**
     * Reset game
     */
    private void resetGame() {
        gameState = GameState.HOME_SCREEN;
        currentFrame = 0;
        score = 0;
        destroyedBarrels = 0;
        jumpedBarrels = 0;
        
        // Clear game objects
        platforms.clear();
        ladders.clear();
        barrels.clear();
        
        // Reinitialize game objects
        initGameObjects();
    }

    /**
     * Draw home screen
     */
    private void drawHomeScreen() {
        backgroundImage.draw(WIDTH / 2.0, HEIGHT / 2.0);
        
        String title = MESSAGE_PROPS.getProperty("home.title");
        double titleY = Double.parseDouble(GAME_PROPS.getProperty("home.title.y"));
        TITLE_FONT.drawString(title, (WIDTH - TITLE_FONT.getWidth(title)) / 2.0, titleY);
        
        String prompt = MESSAGE_PROPS.getProperty("home.prompt");
        double promptY = Double.parseDouble(GAME_PROPS.getProperty("home.prompt.y"));
        PROMPT_FONT.drawString(prompt, (WIDTH - PROMPT_FONT.getWidth(prompt)) / 2.0, promptY);
    }

    /**
     * Draw game screen
     */
    private void drawGameScreen() {
        backgroundImage.draw(WIDTH / 2.0, HEIGHT / 2.0);
        
        // Draw platforms first
        for (Platform platform:  platforms) {
            platform.render();
        }
        
        // Draw ladders
        for (Ladder ladder:  ladders) {
            ladder.render();
        }
        
        // Draw barrels
        for (Barrel barrel:  barrels) {
            if (!barrel.isDestroyed()) {
                barrel.render();
            }
        }
        
        // Draw hammer, Donkey Kong, and Mario
        hammer.render();
        donkeyKong.render();
        mario.render();
        
        // Draw score and remaining time - use gamePlay.score configuration
        double scoreX = Double.parseDouble(GAME_PROPS.getProperty("gamePlay.score.x"));
        double scoreY = Double.parseDouble(GAME_PROPS.getProperty("gamePlay.score.y"));
        SCORE_FONT.drawString("Score " + score, scoreX, scoreY);
        
        int remainingTime = (MAX_FRAMES - currentFrame) / 60;
        SCORE_FONT.drawString("Time " + remainingTime, scoreX, scoreY + 30);
    }

    /**
     * Draw game over screen
     */
    private void drawGameOverScreen() {
        backgroundImage.draw(WIDTH / 2.0, HEIGHT / 2.0);
        
        // Display winning or losing message
        String statusMessage;
        if (gameState == GameState.GAME_OVER_WIN) {
            statusMessage = MESSAGE_PROPS.getProperty("gameEnd.won");
        } else {
            statusMessage = MESSAGE_PROPS.getProperty("gameEnd.lost");
        }
        
        double statusY = Double.parseDouble(GAME_PROPS.getProperty("gameEnd.status.y"));
        GAME_END_FONT.drawString(statusMessage, 
                (WIDTH - GAME_END_FONT.getWidth(statusMessage)) / 2.0, statusY);
        
        // Calculate and display final score, use gameEnd.scores configuration
        int timeBonus = (MAX_FRAMES - currentFrame) / 60 * 3; // Time bonus
        int finalScore = score + timeBonus;
        
        String scoreLabel = MESSAGE_PROPS.getProperty("gameEnd.score");
        String scoreMessage = scoreLabel + " " + finalScore;
        
        // Use gameEnd.scores.y and gameEnd.scores.fontSize to display score
        double scoresY = Double.parseDouble(GAME_PROPS.getProperty("gameEnd.scores.y"));
        int scoresFontSize = Integer.parseInt(GAME_PROPS.getProperty("gameEnd.scores.fontSize"));
        Font scoresFont = new Font(GAME_PROPS.getProperty("font"), scoresFontSize);
        
        scoresFont.drawString(scoreMessage, 
                (WIDTH - scoresFont.getWidth(scoreMessage)) / 2.0, scoresY);
        
        // Continue prompt
        String continueMessage = MESSAGE_PROPS.getProperty("gameEnd.continue");
        PROMPT_FONT.drawString(continueMessage, 
                (WIDTH - PROMPT_FONT.getWidth(continueMessage)) / 2.0, HEIGHT - 100);
        
        // Detailed score information
        String destroyedBarrelsScore = "Destroyed barrel score " + (destroyedBarrels * 100);
        String jumpedBarrelsScore = "Jumped barrel score " + (jumpedBarrels * 30);
        String timeBonusScore = "Time bonus " + timeBonus;
        
        // Display detailed score below other information
        scoresFont.drawString(destroyedBarrelsScore, 
                (WIDTH - scoresFont.getWidth(destroyedBarrelsScore)) / 2.0, scoresY + 30);
        scoresFont.drawString(jumpedBarrelsScore, 
                (WIDTH - scoresFont.getWidth(jumpedBarrelsScore)) / 2.0, scoresY + 60);
        scoresFont.drawString(timeBonusScore, 
                (WIDTH - scoresFont.getWidth(timeBonusScore)) / 2.0, scoresY + 90);
    }

    /**
     * Update game state
     * @param input User input
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }
        
        switch (gameState) {
            case HOME_SCREEN:
                drawHomeScreen();
                if (input.wasPressed(Keys.ENTER)) {
                    gameState = GameState.GAME_PLAY;
                }
                break;
                
            case GAME_PLAY:
                currentFrame++;
                updateGameplay(input);
                drawGameScreen();
                break;
                
            case GAME_OVER_WIN:
            case GAME_OVER_LOSS:
                drawGameOverScreen();
                if (input.wasPressed(Keys.SPACE)) {
                    resetGame();
                }
                break;
        }
    }

    /**
     * Update game play logic
     * @param input User input
     */
    private void updateGameplay(Input input) {
        // Check game end condition
        if (currentFrame >= MAX_FRAMES) {
            gameState = GameState.GAME_OVER_LOSS;
            return;
        }
        
        // Check if Mario is in valid position
        checkAndResetMario();
        
        // Update game objects
        for (Platform platform:  platforms) {
            platform.update();
        }
        
        for (Ladder ladder:  ladders) {
            ladder.update();
        }
        
        // Update Donkey Kong
        donkeyKong.update(platforms);
        
        // Update all barrels
        for (Barrel barrel:  barrels) {
            barrel.update(platforms);
        }
        
        // Ensure Mario does not leave screen
        if (mario.getY() < 0) {
            mario.setY(0);
        } else if (mario.getY() + mario.getHeight() > HEIGHT) {
            mario.setY(HEIGHT - mario.getHeight());
        }
        
        // Update Mario (must update all other objects before updating Mario to ensure correct movement logic)
        int newJumpedBarrels = mario.update(input, platforms, ladders, barrels, hammer, WIDTH, HEIGHT);
        if (newJumpedBarrels > 0) {
            jumpedBarrels += newJumpedBarrels;
            score += newJumpedBarrels * 30; // Add 30 points for each barrel jumped
        }
        
        // Again check if Mario is in valid position
        checkAndResetMario();
        
        // Check if Mario collided with barrels
        for (Barrel barrel:  barrels) {
            if (!barrel.isDestroyed() && mario.collidesWith(barrel)) {
                if (mario.hasHammer()) {
                    barrel.destroy();
                    destroyedBarrels++;
                    score += 100; // Add 100 points for each destroyed barrel
                } else {
                    gameState = GameState.GAME_OVER_LOSS;
                    return;
                }
            }
        }
        
        // Check if Mario collided with Donkey Kong
        if (mario.collidesWith(donkeyKong)) {
            if (mario.hasHammer()) {
                gameState = GameState.GAME_OVER_WIN;
            } else {
                gameState = GameState.GAME_OVER_LOSS;
            }
            return;
        }
        
        // Debug output current state (limit output frequency, output every 50 frames)
        if (currentFrame % 50 == 0) {
            System.out.println("Current frame " + currentFrame + ", Mario position (" + mario.getX() + "," + mario.getY() + ")");
        }
    }

    /**
     * Check if Mario is in valid position, if not reset his position
     */
    private void checkAndResetMario() {
        // Check if Mario's Y coordinate is abnormal (greater than 1000 is considered abnormal)
        if (mario.getY() > 1000 || mario.getY() < -100) {
            System.out.println("Warning Mario position abnormal (" + mario.getX() + "," + mario.getY() + "), resetting...");
            
            // Try to place Mario on first platform
            if (!platforms.isEmpty()) {
                Platform firstPlatform = platforms.get(0);
                mario.setPosition(firstPlatform.getX() + 50, firstPlatform.getY() - mario.getHeight());
                System.out.println("Mario reset to platform position (" + mario.getX() + "," + mario.getY() + ")");
            } else {
                // If no platform, place at bottom of screen
                mario.setPosition(WIDTH / 2, HEIGHT - 100);
                System.out.println("Mario reset to bottom of screen (" + mario.getX() + "," + mario.getY() + ")");
            }
        }
    }

    /**
     * The main entry point of the Shadow Donkey Kong game.
     *
     * This method loads the game properties and message files, initializes the game,
     * and starts the game loop.
     *
     * @param args Command-line arguments (not used in this game).
     */
    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowDonkeyKong game = new ShadowDonkeyKong(gameProps, messageProps);
        game.run();
    }
}
