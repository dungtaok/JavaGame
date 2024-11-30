package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gameState.Gamestate;
import main.Game;
import utilz.LoadSave;
import levels.Level;

public class LevelManager {

    private Game game;
    private BufferedImage[] levelSprite;
    private ArrayList<Level> levels;
    private int lvlIndex = 0;
    private int lvlData[][];

    public LevelManager(Game game) {
        this.game = game;
        importOutsideSprites();
        levels = new ArrayList<>();
        builAllLevels();
    }

    public void loadNextLevel(){
        lvlIndex++;
        if(lvlIndex>=levels.size()){
            lvlIndex=0;
            System.out.println("No more levels! Game Completed!");
            Gamestate.state=Gamestate.MENU;
        }
        Level newLevel = levels.get(lvlIndex);
        lvlData = newLevel.getLvlData();
        
        game.getPlaying().getEnemyManager().loadEnemies(newLevel);
        game.getPlaying().getPlayer().loadLvlData(newLevel.getLvlData());
        game.getPlaying().setMaxLvlOffset(newLevel.getLvlOffset());

    }

    private void builAllLevels() {
        BufferedImage[] allLevels = LoadSave.GetAllLevels();
        for(BufferedImage img : allLevels)
            levels.add(new Level(img));
    }

    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[90];
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 10; i++) {
                int index = j*10 + i;
                levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
    }
    
    public void draw(Graphics g, int xlvlOffset, int yLvlOffset) {
        for (int j = 0 ; j < 16 ; j++)
            for (int i = 0; i < levels.get(lvlIndex).getLvlData()[0].length; i++) {
                int index = levels.get(lvlIndex).getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], Game.TILES_SIZE * i - xlvlOffset, Game.TILES_SIZE * j - yLvlOffset, Game.TILES_SIZE, Game.TILES_SIZE, null);
            }
    }

    public void update() {

    }
    

    public Level getCurrentLevel() {
        return levels.get(lvlIndex);
    }
    public int getAmountOfLevels(){
        return levels.size();
    }

    public int getLevelIndex(){
        return lvlIndex;
    }
    

}
