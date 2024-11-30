package entities;
import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;


import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.EnemyConstants.*;


public class Crabby extends Enemy{


    private int attackBoxOffsetX;

    public Crabby(float x, float y) {
        super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
        initHitbox(22,6);
        initAttackBox();
    }
    private void initAttackBox(){
        attackBox=new Rectangle2D.Float(x,y,(int)(82*Game.SCALE),(int)(19*Game.SCALE));
        attackBoxOffsetX = (int)(Game.SCALE*30);
    }



    public void update(int[][] lvlData,Player player){
        updateBehaviour(lvlData,player);
        updateAnimationTick();
        updateAttackBox();

    }

    private void updateAttackBox() {
        attackBox.x= hitbox.x-attackBoxOffsetX;
        attackBox.y=hitbox.y;
    }

    private void updateBehaviour(int[][] lvlData,Player player) {
        if (firstUpdate){
         firstUpdateCheck(lvlData);

        }
        if(inAir) {
           updateInAir(lvlData);
        }else{
            switch (state){
                case IDLE :
                    newState(RUNNING);
                    break;

                case RUNNING:
                    if(canSeePlayer(lvlData,player)) {
                        turnTowardsPlayer(player);
                        if (isPlayerCloseAttack(player))
                            newState(ATTACK);
                    }
                    move(lvlData);
                    break;
                case ATTACK:
                    if(aniIndex==0)
                        attackChecked = false;

                    if(aniIndex==3&&!attackChecked)
                        checkEnmyHit(attackBox,player);
                    break;
                case HIT:
                    break;
            }

        }

    }



    public void drawAttackBox(Graphics g,int xLvlOffset){
        g.setColor(Color.red);
//        g.drawRect((int)(attackBox.x-xLvlOffset),(int)(attackBox.y),(int)attackBox.width,(int)attackBox.height);d

    }
    public int flipX(){
        if(walkDir==RIGHT)
            return width;
        else
            return  0;
    }
    public int flipY(){
          if(walkDir==RIGHT)
              return -1;
          else{
              return 1;
          }
    }

}
