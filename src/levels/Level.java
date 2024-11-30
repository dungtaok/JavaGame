package levels;

import entities.Crabby;
import main.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utilz.HelpMethods.*;

public class Level {
    private BufferedImage img;
    private int lvlTilesWide;
    private int maxTilesOffset;
    private int maxLvlOffsetX;
    private int[][] lvlData;
    private ArrayList<Crabby> crabs;
    private Point playerSpawn;
    

 public Level(BufferedImage img){
    this.img=img;
     createLevelData();
     createEnemies();
     calcLvlOffsets();
     calcPlayerSpawn();
 }

    private void calcPlayerSpawn() {	
     playerSpawn=GetPlayerSpawn(img);
    }

    private void createEnemies() {
     crabs = GetCrabs(img);
    }

    private void createLevelData() {
             lvlData=GetLevelData(img);
    }

     private void calcLvlOffsets() {
     lvlTilesWide = img.getWidth();
     maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
     maxLvlOffsetX = Game.TILES_SIZE*maxTilesOffset;
     }

     public int getSpriteIndex(int x,int y){
    	return lvlData[y][x];

	 }
	 public int[][] getLvlData(){
	     return lvlData;
	 }
	
	public int getLvlOffset(){
	     return maxLvlOffsetX;
	}

    public ArrayList<Crabby> getCrabs() {
        return crabs;
    }
  public Point getPlayerSpawn(){
     return playerSpawn;
  }
  
  public int getMapHeight() {
	    return lvlData.length; // Số hàng trong mảng 2D là chiều cao của bản đồ (theo số tile)
	}
}
