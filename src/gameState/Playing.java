package gameState;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;


import entities.EnemyManager;
import entities.Player;

import levels.LevelManager;
import main.Game;
import static main.Game.*;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utilz.LoadSave;
// import static utilz.Constants.Environment.*;

public class Playing extends State implements Statemethods {
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private LevelCompletedOverlay levelCompletedOverlay;
    private boolean paused = false;

    private int xLvlOffset;
    
    private int yLvlOffset;
    
    private int topBorder = (int) (0.25 * Game.GAME_HEIGHT);
    private int bottomBorder = (int) (0.75 * Game.GAME_HEIGHT);
    
    private int leftBorder = (int) (0.3 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.7 * Game.GAME_WIDTH);

    private int maxLvlOffsetX;
    private int maxLvlOffsetY;

    private BufferedImage backgroundImg,groundImg, bigCloud, smallCloud;
    private int[] smallCloudsPos;
    private Random rnd = new Random();

    private boolean gameOver;
    private boolean lvlCompleted=false;
    private boolean playerDying;

    public Playing(Game game) {
        super(game);
        initClasses();

        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG);
        
        /*bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos = new int[8];
        for (int i = 0; i < smallCloudsPos.length; i++)
            smallCloudsPos[i] = (int) (90 * Game.SCALE) + rnd.nextInt((int) (100 * Game.SCALE));*/

        caclcLvlOffset();
        loadStartLevel();
    }
    public void loadNextLevel(){
        resetAll();
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
    }

    private void loadStartLevel() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());

    }

    private void caclcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
        
     // Tính chiều cao map theo số lượng tile
        int mapHeightInPixels = levelManager.getCurrentLevel().getMapHeight() * Game.TILES_SIZE;
        maxLvlOffsetY = mapHeightInPixels - Game.GAME_HEIGHT;

        // Đảm bảo không có giá trị âm
        if (maxLvlOffsetY < 0) maxLvlOffsetY = 0;
        if (maxLvlOffsetX < 0) maxLvlOffsetX = 0;
    }
    
    // initClass
    
    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);

        player = new Player((int)(0.3*GAME_WIDTH), (int)(0.1*Game.GAME_HEIGHT), (int) (64 * 1.65 * Game.SCALE), (int) (40 * 1.65 * Game.SCALE), this);	
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());

        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompletedOverlay = new LevelCompletedOverlay(this);

        // Căn chỉnh camera theo vị trí nhân vật
        caclcLvlOffset(); // Tính maxLvlOffsetX và maxLvlOffsetY trước

        int playerX = (int) player.getHitBox().x;
        int playerY = (int) player.getHitBox().y;

        xLvlOffset = Math.max(0, Math.min(maxLvlOffsetX, playerX - Game.GAME_WIDTH / 2));
        yLvlOffset = Math.max(0, Math.min(maxLvlOffsetY, playerY - Game.GAME_HEIGHT / 2));
    }




//    private void initClasses() {
//        levelManager = new LevelManager(game);
//        enemyManager = new EnemyManager(this);
//
//        player = new Player(200, 200, (int) (64 *1.65* Game.SCALE), (int) (40 *1.65* Game.SCALE), this);
//        player.loadLvlData(levelManager.getCurrentLevel().getLvlData());
//        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
//
//        pauseOverlay = new PauseOverlay(this);
//        gameOverOverlay = new GameOverOverlay(this);
//        levelCompletedOverlay = new LevelCompletedOverlay(this);
//    }

    @Override
    public void update() {
        if (paused) {
            pauseOverlay.update();
        } else if (lvlCompleted) {
            levelCompletedOverlay.update();
        }else if(gameOver){
            gameOverOverlay.update();
        }else if(playerDying){
            player.update();

        }else  {
            levelManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLvlData(), player);
            checkCloseToBorder();
            checkCloseToBorderVertical();
        }

    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitBox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;
    }

    
    private void checkCloseToBorderVertical() {
        int playerY = (int) player.getHitBox().y;
        int diff = playerY - yLvlOffset;

        if (diff > bottomBorder)
            yLvlOffset += diff - bottomBorder;
        else if (diff < topBorder)
            yLvlOffset += diff - topBorder;

        if (yLvlOffset > maxLvlOffsetY)
            yLvlOffset = maxLvlOffsetY;
        else if (yLvlOffset < 0)
            yLvlOffset = 0;
    }

    
    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        g.drawImage(groundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        levelManager.draw(g, xLvlOffset, yLvlOffset);
        player.render(g, xLvlOffset, yLvlOffset);
        enemyManager.draw(g, xLvlOffset, yLvlOffset);

        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        } else if (gameOver)
            gameOverOverlay.draw(g);
        else if (lvlCompleted)
            levelCompletedOverlay.draw(g);
    }

    public void resetAll() {
        gameOver = false;
        paused = false;
        lvlCompleted=false;
        playerDying=false;
        player.resetAll();
        enemyManager.resetAllEnemies();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameOver)
            if (e.getButton() == MouseEvent.BUTTON1)
                player.setAttacking(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver)
            gameOverOverlay.keyPressed(e);
        else
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(true);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(true);
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = !paused;
                    break;
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(false);
                    break;
            }

    }

    public void mouseDragged(MouseEvent e) {
        if (!gameOver)
            if (paused)
                pauseOverlay.mouseDragged(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pauseOverlay.mousePressed(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mousePressed(e);
        }
        else{
            gameOverOverlay.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pauseOverlay.mouseReleased(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mouseReleased(e);
        }else{
            gameOverOverlay.mouseReleased(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pauseOverlay.mouseMoved(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mouseMoved(e);
        }
        else
            gameOverOverlay.mouseMoved(e);
    }
    public void setLevelCompleted(boolean levelCompleted){
        this.lvlCompleted=levelCompleted;
        if(levelCompleted)
            game.getAudioPlayer().lvlCompleted();
    }

    public void setMaxLvlOffset(int lvlOffset){
        this.maxLvlOffsetX=lvlOffset;
    }

    public void unpauseGame() {
        paused = false;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public Player getPlayer() {
        return player;
    }
    public EnemyManager getEnemyManager(){
        return enemyManager;
    }
    public LevelManager getLevelManager(){
        return levelManager;
    }

    public void setPlayerDying(boolean playerDying) {
        this.playerDying=playerDying;
    }
}