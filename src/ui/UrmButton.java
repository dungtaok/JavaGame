package ui;

import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utilz.Constants.UI.URMButtons.URM_DEFAULT_SIZE;
import static utilz.Constants.UI.URMButtons.URM_SIZE;


public class UrmButton extends  PauseButton {
    private  BufferedImage[] imgs;
    private  int rowIndex,index;
    private boolean mouseOver,mousePressed;
    public UrmButton(int x, int y, int width, int height,int rowIndex) {
        super(x, y, width, height);
        this.rowIndex = rowIndex;
        loadImgs();
    }

    private void loadImgs() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.URM_BUTTON);
        imgs = new BufferedImage[3];
        for(int i=0;i<imgs.length;i++)
            imgs[i]=temp.getSubimage(i*URM_DEFAULT_SIZE,rowIndex*URM_DEFAULT_SIZE,URM_DEFAULT_SIZE,URM_DEFAULT_SIZE);
    }

    public  void update(){
        if(mouseOver)
            index = 1;
        if(mousePressed)
            index = 2;

    }
    public  void draw(Graphics g){
        g.drawImage(imgs[index],x,y,URM_SIZE,URM_SIZE,null);

    }
    public void resetBools(){
        mousePressed = false;
        mouseOver= false;

    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }
}
