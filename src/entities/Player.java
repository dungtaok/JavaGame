package entities;
import audio.AudioPlayer;
import gameState.Playing;
import main.Game;
import utilz.LoadSave;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.GRAVITY;
import static utilz.Constants.PlayerConstants.*;
import  static  utilz.HelpMethods.*;
public class Player extends  Entity{
    private BufferedImage[][] animations;

    private  boolean moving = false,attacking=false;
    private boolean right,left,jump;

    private int[][] lvlData;
    private float xDrawOffset= 42 *Game.SCALE;
    private  float yDrawOffset= 13*Game.SCALE;
    //jumping / gravity


    private float jumpSpeed=-2.35f*Game.SCALE;
    private  float fallSpeedAfterCollision = 0.5f*Game.SCALE;

    //Status BarUI
    private BufferedImage statusBarImg;

    private int statusBarWidth=(int)(192*Game.SCALE);
    private int statusBarHeight =(int)(58*Game.SCALE);
    private int statusBarX =(int)(10*Game.SCALE);
    private int statusBarY =(int)(10*Game.SCALE);

    private int healthBarWith=(int)(150*Game.SCALE);
    private int healthBarHeigth=(int)(4*Game.SCALE);
    private int healthBarXStart=(int)(34*Game.SCALE);
    private int healthBarYStart=(int)(14*Game.SCALE);


    private int healthWidth= healthBarWith;
    //attackBox


    private int flipX = 0;
    private int flipW = 1;

    private boolean attackChecked;
    private Playing playing;

    public Player(float x, float y, int width, int height, Playing playing) {
        super(x, y,width,height);
        this.playing = playing;
        this.state=IDLE;
        this.maxHealth=100;
        this.currentHealth=maxHealth;
        this.walkSpeed=Game.SCALE*1.0f;
        loadAnimations();
        initHitbox(16, 27);
        initAttackBox();
    }
    public void setSpawn(Point spawn){
        this.x =spawn.x;
        this.y=spawn.y;
        hitbox.x=x;
        hitbox.y=y;
    }

    private void initAttackBox() {
        attackBox=new Rectangle2D.Float(x,y+15,(int)(17*Game.SCALE),(int)(17*Game.SCALE));
    }

    public void update(){
        updateHealthBar();
        if(currentHealth<=0) {
            if(state!=DEAD){
                state=DEAD;
                aniTick=0;
                aniIndex=0;
                playing.setPlayerDying(true);
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
            }else if(aniIndex==GetSpriteAmount(DEAD)-1&&aniTick>=ANI_SPEED-1){
                playing.setGameOver(true);
                playing.getGame().getAudioPlayer().stopSong();
                playing.getGame().getAudioPlayer().playEffect((AudioPlayer.GAMEOVER));
            }else{
                updateAnimationTick();
            }

            return;
        }

        updateAttackBox();
        
        updatePos();
        if(attacking)
            checkAttack();
        updateAnimationTick();
        setAnimation();
        

    }

    private void checkAttack() {
        if(attackChecked||aniIndex!=1)
            return;
        attackChecked = true;
        playing.checkEnemyHit(attackBox);
        playing.getGame().getAudioPlayer().playAttackSound();
    }

    private void updateAttackBox() {
        if(right){
            attackBox.x= hitbox.x+hitbox.width+(int)(Game.SCALE*10);

        }else if(left) {
            attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);
        }

        attackBox.y= hitbox.y+(int)(Game.SCALE*10);
    }


    private void updateHealthBar() {
        healthWidth=(int)((currentHealth/(float)maxHealth)*healthBarWith);
    }

    public void render(Graphics g, int xlvlOffset, int yLvlOffset){
        g.drawImage(animations[state][aniIndex],(int)(hitbox.x-xDrawOffset)-xlvlOffset+flipX,(int)(hitbox.y-yDrawOffset)-yLvlOffset,width*flipW,height,null);
        drawAttackHitbox(g,xlvlOffset);
        drawUI(g);
        drawHitbox(g,xlvlOffset);

    }
    




    private void drawUI(Graphics g) {
        g.drawImage(statusBarImg,statusBarX,statusBarY,statusBarWidth,statusBarHeight,null);
        g.setColor(Color.red);
        g.fillRect(healthBarXStart+statusBarX,healthBarYStart+statusBarY,healthWidth,healthBarHeigth);

    }


    private void updatePos() {
        moving = false;
        if(jump)
            jump();
        if(!inAir)
            if((!left&&!right)||(right&&left))
                return;

        float xSpeed=0;

		if(left) {
		    xSpeed -= walkSpeed;
		    flipX = width;
		    flipW = -1;
		}
		
		 if(right) {
		     xSpeed += walkSpeed;
		     flipX = 0;
		     flipW = 1;
		
		 }
		 if(!inAir)
		     if(!IsEntityOnFloor(hitbox,lvlData))
		         inAir=true;
		
		
		
		if(inAir) {
		    if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
		        hitbox.y += airSpeed;
		        airSpeed += GRAVITY;
		        updateXPos(xSpeed);
		    } else {
		        hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
		
		        if(airSpeed>0)
		            resetInAir();
		        else airSpeed = fallSpeedAfterCollision;
		        updateXPos(xSpeed);
		    }
		}
		else{
		    updateXPos(xSpeed);
		
		}
		moving = true;

    }

    private void jump() {
        if(inAir)
            return;
        playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float xSpeed) {
         if(CanMoveHere(hitbox.x+xSpeed,hitbox.y, hitbox.width,hitbox.height,lvlData)) {
            hitbox.x += xSpeed;
        }else{
             hitbox.x = GetEntityXPosNextToWall(hitbox,xSpeed);
         }
    }
    public void changeHealth(int value){
        currentHealth +=value;
        if(currentHealth<=0){
            currentHealth = 0;
            //gameOver();
        }else if(currentHealth>=maxHealth){
            currentHealth=maxHealth;
        }

    }

    public void updateGame(){
        updatePos();
        updateAnimationTick();
        setAnimation();

    }

    private void setAnimation() {
        int startAni = state;
        if(moving)
            state=RUNNING;

        else
            state=IDLE;
        if(inAir){
            if(airSpeed<0)
                state=JUMP;
            else
                state = FALLING;
        }
        if(attacking) {
            state = ATTACK;
            if(startAni!=ATTACK){
                aniIndex = 1;
                aniTick = 0;
                return;

            }
        }
        if(startAni!=state)
            resetAniTick();

    }

    private void resetAniTick() {
        aniIndex=0;
        aniTick=0;
    }

    private void updateAnimationTick() {
        aniTick++;
        if(aniTick>=ANI_SPEED){
            aniTick=0;
            aniIndex++;
            if(aniIndex>=GetSpriteAmount(state)){
                aniIndex=0;
                attacking=false;
                attackChecked = false;
            }

        }
    }



    public boolean isRight() {
        return right;
    }

    public boolean isLeft() {
        return left;
    }



    public void setRight(boolean right) {
        this.right = right;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }
public void resetDirBooleans(){

        right=false;
        left=false;

}

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    private void loadAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        animations = new BufferedImage[7][8];
        for (int j = 0; j < animations.length; j++)
            for (int i = 0; i < animations[j].length; i++)
                animations[j][i] = img.getSubimage(i * 64 , j * 40 , 64, 40);

            statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

        }
        public void loadLvlData(int[][]lvlData){
          this.lvlData=lvlData;
          if(!IsEntityOnFloor(hitbox,lvlData))
              inAir = true;
        }


    public void setJump(boolean jump) {
        this.jump=jump;
    }
    public void resetAll(){
        resetDirBooleans();
        inAir=false;
        attacking=false;
        moving=false;
        state=IDLE;
        currentHealth=maxHealth;

        hitbox.x=x;
        hitbox.y=y;
        if(!IsEntityOnFloor(hitbox,lvlData))
            inAir = true;
    }
}




