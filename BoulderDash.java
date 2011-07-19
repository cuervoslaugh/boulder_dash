import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.Timer;

public class BoulderDash extends JPanel
{
    private final int WIDTH = 40, HEIGHT = 22;
    private Random rng = new Random();
    public int rx, ry; // player coordinates    
    public int level; // current level
    public int topX = 0, topY= 0, xIdeal= 0, yIdeal= 0;
    boolean playerAlive;    
    public enum Directions { LEFT, RIGHT, UP, DOWN }
    public ArrayList<Monster> monsters;
    public ArrayList<Sprite> movingSprites;
    public Player player = new Player();
    public GameUtils gu = new GameUtils();
    
    public GameUtils.Contents[][] field = new GameUtils.Contents[WIDTH][HEIGHT];
    
    BoulderDash()
    {
      field = gu.loadGame();
      int[] loc = gu.getPlayerLocation(field);
      rx = loc[0];
      ry = loc[1];
      player.setX(rx);
      player.setY(ry);
        
      monsters = monsterUp();
      new Timer(200, new ScreenTransform()).start();
      new Timer(200, new FallingObjectCheck()).start();
      new Timer(100, new MonsterMover()).start(); 

      this.addKeyListener(new MyKeyListener());
      this.setFocusable(true);
      this.requestFocus();
      this.setPreferredSize(new Dimension(500, 400));
      this.setBorder(BorderFactory.createEtchedBorder());
      this.setBackground(Color.BLACK);
    }
    
    private class ScreenTransform implements ActionListener {
      public void actionPerformed(ActionEvent ae){
       xIdeal = rx;
       yIdeal = ry;
       topX = (xIdeal - 10) * 10;
       topY = (yIdeal - 10) * 10;
       repaint();
      }
    }
    
    private class MonsterMover implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
        monsterMove();
      }
    }
    private class FallingObjectCheck implements ActionListener {
      public void actionPerformed(ActionEvent ae){
        for(int j = 21; j >= 0; j--){ // y
          for(int i = 0; i < 38; i++) {  // x
            setFallingObject(i, j);
            moveFallingObject(i, j);
            repaint();
          }
        }
      }
      
      public void setFallingObject(int x, int y) 
      {
        switch(field[x][y])
        {
          case ROCK:
            if(field[x][y + 1] == GameUtils.Contents.EMPTY) { 
              field[x][y] = GameUtils.Contents.FALLINGROCK;
              break;
            }
            if(field[x - 1][y] == GameUtils.Contents.EMPTY &&
               field[x - 1][y + 1] == GameUtils.Contents.EMPTY) {
              field[x][y] = GameUtils.Contents.FALLINGROCK;
              break;
            }
            if(field[x + 1][y] == GameUtils.Contents.EMPTY &&
               field[x + 1][y + 1] == GameUtils.Contents.EMPTY) {
              field[x][y] = GameUtils.Contents.FALLINGROCK;
              break;
            }
            if(field[x][y] == GameUtils.Contents.FALLINGROCK && 
               (field[x][y + 1] == GameUtils.Contents.DIRT) ||
               (field[x][y + 1] == GameUtils.Contents.WALL)) field[x][y] = GameUtils.Contents.ROCK;
            if(field[x][y + 1] == GameUtils.Contents.BUTTERFLY) { goBoom(x, y + 1, GameUtils.Contents.BUTTERFLY);}
            if(field[x][y + 1] == GameUtils.Contents.FIREFLY) {goBoom(x, y + 1, GameUtils.Contents.FIREFLY);}
            break;
          case DIAMOND:
            if(field[x][y + 1] == GameUtils.Contents.EMPTY) {
              field[x][y] = GameUtils.Contents.FALLINGDIAMOND;
            }
            if(field[x - 1][y] == GameUtils.Contents.EMPTY &&
               field[x - 1][y + 1] == GameUtils.Contents.EMPTY) {
              field[x][y] = GameUtils.Contents.FALLINGDIAMOND;
              break;
            }
            if(field[x + 1][y] == GameUtils.Contents.EMPTY &&
               field[x + 1][y + 1] == GameUtils.Contents.EMPTY) {
              field[x][y] = GameUtils.Contents.FALLINGDIAMOND;
              break;
            }
            break;
        }
      }
      
      private void goBoom(int x, int y, GameUtils.Contents contact) 
      {
        switch(contact) {
          case PLAYER:
            playerAlive = false;
            break;
          case BUTTERFLY:
            splode(x, y);
            repaint();
            break;
          case FIREFLY:
            splode(x, y);
            repaint();
            break;
        }
      }
      
      public void splode(int x, int y) {
        int rEdge = x + 1;
        int lEdge = x - 1;
        int top = y - 1;
        int bottom = y + 1;
        for(int j = top; j <= bottom; j++){
          for(int i = lEdge; i <= rEdge; i++) {
            if(canTransMutate(i, j)) field[i][j] = GameUtils.Contents.DIAMOND;
          }
        }
      }
      
      private boolean canTransMutate(int x, int y) {
        switch(field[x][y]) {
          case FIREFLY:
            return true;
          case BUTTERFLY:
            return true;
          case EMPTY:
            return true;
          default:
            return false;
        }
      }
      
      
      public void moveFallingObject(int x, int y)
      {
        switch(field[x][y])
        {
          case FALLINGROCK:
            if(field[x][y + 1] == GameUtils.Contents.PLAYER)
            {
              player.alive = false;
              field[x][y + 1] = GameUtils.Contents.EMPTY;
              break;
            }
            if(field[x][y + 1] == GameUtils.Contents.EMPTY)
          {
            field[x][y + 1] = GameUtils.Contents.FALLINGROCK;
            field[x][y] = GameUtils.Contents.EMPTY;
            break;
          }
            if(field[x][y + 1] == GameUtils.Contents.BUTTERFLY)
            {
              goBoom(x, y, GameUtils.Contents.BUTTERFLY);
              break;
            }
            if(field[x][y + 1] == GameUtils.Contents.AMOEBA)
            {
              //kill the amoeba
              break;
            }
            if(field[x - 1][y + 1] == GameUtils.Contents.EMPTY) {
              field[x - 1][y + 1] = GameUtils.Contents.FALLINGROCK;
              field[x][y] = GameUtils.Contents.EMPTY;
              break;
            }
            if(field[x + 1][y + 1] == GameUtils.Contents.EMPTY) {
              field[x + 1][y + 1] = GameUtils.Contents.FALLINGROCK;
              field[x][y] = GameUtils.Contents.EMPTY;
              break;
            }
            field[x][y] = GameUtils.Contents.ROCK;
            break;
          case FALLINGDIAMOND:
            if(field[x][y + 1] == GameUtils.Contents.EMPTY){
              field[x][y + 1] = GameUtils.Contents.DIAMOND;
              field[x][y] = GameUtils.Contents.EMPTY;
              break;
          }
        }
      }
    }
        
    private class MyKeyListener implements KeyListener {
      private int kc;      
      public void keyPressed(KeyEvent ke) {
        kc = ke.getKeyCode();
        if(player.alive) {
        switch(kc){
          case 37:
            movePlayer(Directions.LEFT);
            break;
          case 38:
            movePlayer(Directions.UP);
            break;
          case 39:
            movePlayer(Directions.RIGHT);
            break;
          case 40:
            movePlayer(Directions.DOWN);
            break;
          case 78:
            if(level == 10) level = 0;
            nextLevel();
            break;
        }
        }
      }
      public void keyReleased(KeyEvent ke) {};
      public void keyTyped(KeyEvent ke) {};
  }
    
    public boolean isEmpty(int x, int y) {
      if(field[x][y] == GameUtils.Contents.EMPTY) return true;
      return false;
    }
    
    public void monsterMove() {
      if( monsters.size() > 0 ){
        for(Monster m : monsters) {
          Monster.Directions direction = m.getDirection();
          int mX = m.getX();
          int mY = m.getY();
          switch(direction) {
            case RANDOM:
              Float chance = rng.nextFloat();
              if(chance < 0.05) {
                switch(rng.nextInt(4)) {
                  case 0:
                    if(field[mX - 1][mY] == GameUtils.Contents.EMPTY) {
                    field[mX - 1][mY] = GameUtils.Contents.AMOEBA;
                    break;
                  }
                  case 1:
                    if(field[mX + 1][mY] == GameUtils.Contents.EMPTY){
                    field[mX + 1][mY] = GameUtils.Contents.AMOEBA;
                    break;
                  }
                  case 2:
                    if(field[mX][mY - 1] == GameUtils.Contents.EMPTY){
                    field[mX][mY - 1] = GameUtils.Contents.AMOEBA;
                    break;
                  }
                  case 3:
                    if(field[mX][mY + 1] == GameUtils.Contents.EMPTY){
                    field[mX][mY + 1] = GameUtils.Contents.AMOEBA;
                    break;
                  }
                }
                  
              }
              break;
            case LEFT:
              //code to go left
              if(hasPlayer(mX - 1, mY)) {
                player.alive = false;
                break;}
              if(isEmpty(mX - 1, mY)) {
                field[mX - 1][mY] = field[mX][mY];
                field[mX][mY] = GameUtils.Contents.EMPTY;
                m.setX(mX - 1);
            } else { m.changeDirection(); }
              break;
            case RIGHT:
              //code to go right
              if(hasPlayer(mX + 1, mY)) {
              player.alive = false;
              break;
            }
              if(isEmpty(mX + 1, mY)) {              
                field[mX + 1][mY] = field[mX][mY];
                field[mX][mY] = GameUtils.Contents.EMPTY;
                m.setX(mX + 1);
            } else { m.changeDirection(); }
              break;
            case UP:
              //code to go up
              if(hasPlayer(mX, mY - 1)) {
              player.alive = false;
              break;
            }
              if(isEmpty(mX, mY - 1)) {             
                field[mX][mY - 1] = field[mX][mY];
                field[mX][mY] = GameUtils.Contents.EMPTY;
                m.setY(mY - 1);
            } else { m.changeDirection(); }                            
              break;
            case DOWN:
              //code to go down
              if(hasPlayer(mX, mY + 1)) {
              player.alive = false;
              break;
            }
              if(isEmpty(mX, mY +1)) {              
                field[mX][mY + 1] = field[mX][mY];
                field[mX][mY] = GameUtils.Contents.EMPTY;
                m.setY(mY + 1);
            }  else { m. changeDirection(); }                   
              break;
          }
        }
      }
    }
    
    private boolean hasPlayer(int x, int y) {
      if(field[x][y] == GameUtils.Contents.PLAYER) return true;
      return false;
    }

    private void checkExit(Directions direction){
      switch(direction){
        case LEFT:
          if( hasLevelExit(rx - 1, ry) && player.diamonds >= gu.diamondsNeeded){ nextLevel();}
          break;
        case RIGHT:
          if(hasLevelExit(rx + 1, ry) && player.diamonds >= gu.diamondsNeeded) { nextLevel(); }
          break;
        case UP:
          if(hasLevelExit(rx, ry - 1) && player.diamonds >= gu.diamondsNeeded) { nextLevel(); }
          break;
        case DOWN:
          if(hasLevelExit(rx, ry + 1) && player.diamonds >= gu.diamondsNeeded) { nextLevel(); }
          break;
      }
    }
    
    private void movePlayer(Directions direction)
    {
      switch(direction){
        case DOWN:
          checkExit(Directions.DOWN);
          if(!hasContact(Directions.DOWN)){
            if(field[rx][ry +1] == GameUtils.Contents.DIAMOND) { 
              player.increasePoints(10);
              gu.diamondsNeeded -= 1;
            }
          field[rx][ry + 1] = GameUtils.Contents.PLAYER;
          field[rx][ry] = GameUtils.Contents.EMPTY;
          ry += 1;
          repaint();
          }
          break;
        case UP:
          checkExit(Directions.UP);
          if(!hasContact(Directions.UP)){
            if(field[rx][ry - 1] == GameUtils.Contents.DIAMOND) { 
              player.increasePoints(10);
              gu.diamondsNeeded -= 1;
            }            
          field[rx][ry - 1] = GameUtils.Contents.PLAYER;
          field[rx][ry] = GameUtils.Contents.EMPTY;
          ry -= 1;
          repaint();
          }
          break;
        case LEFT:
          checkExit(Directions.LEFT);
          moveRock(Directions.LEFT);
          if(!hasContact(Directions.LEFT)){
            if(field[rx - 1][ry] == GameUtils.Contents.DIAMOND) { 
              player.increasePoints(10);
              gu.diamondsNeeded -= 1;
            }            
            field[rx - 1][ry] = GameUtils.Contents.PLAYER;
            field[rx][ry] = GameUtils.Contents.EMPTY;
            rx -= 1;
            repaint();
          }
          break;
        case RIGHT:
          checkExit(Directions.RIGHT);
          moveRock(Directions.RIGHT);
          if(!hasContact(Directions.RIGHT)){
            if(field[rx + 1][ry] == GameUtils.Contents.DIAMOND) { 
              player.increasePoints(10);
              gu.diamondsNeeded -= 1;
            }            
          field[rx + 1][ry] = GameUtils.Contents.PLAYER;
          field[rx][ry] = GameUtils.Contents.EMPTY;
          rx += 1;
          repaint();
          }
          break;
      }
    }
    
    private boolean hasLevelExit(int x, int y){
     if(field[x][y] == GameUtils.Contents.EXIT) {
        return true;
      }
      return false;
    }
    
    public void nextLevel(){
      for(int i = 0; i < WIDTH; i++){
        for(int j = 0; j < HEIGHT; j++){
          field[i][j] = GameUtils.Contents.EMPTY;
        }
      }
      field = gu.increaseLevel(gu.level);
      int loc[] = gu.getPlayerLocation(field);
      rx = loc[0];
      ry = loc[1];
      player.diamonds = 0;
      this.monsters = monsterUp();
      repaint();
    }
    
    
    private void moveRock(Directions direction) 
    {
      switch(direction){
        case DOWN:
          if(field[rx][ry + 1] == GameUtils.Contents.ROCK && 
             field[rx][ry + 2] == GameUtils.Contents.EMPTY) {
          field[rx][ry + 2] = GameUtils.Contents.ROCK;
          field[rx][ry + 1] = GameUtils.Contents.EMPTY;
        }
        break;
        case UP:
          break;
        case LEFT:
          if(field[rx - 1][ry] == GameUtils.Contents.ROCK &&
             field[rx - 2][ry] == GameUtils.Contents.EMPTY) {
          field[rx - 2][ry] = GameUtils.Contents.ROCK;
          field[rx - 1][ry] = GameUtils.Contents.EMPTY;
        }
          break;
        case RIGHT:
          if(field[rx + 1][ry] == GameUtils.Contents.ROCK &&
             field[rx + 2][ry] == GameUtils.Contents.EMPTY) {
          field[rx + 2][ry] = GameUtils.Contents.ROCK;
          field[rx + 1][ry] = GameUtils.Contents.EMPTY;
        }
          break;
      }
    }
    
    private boolean hasContact(Directions direction)
    {       
      switch(direction){
        case DOWN:
          if(field[rx][ry + 1] == GameUtils.Contents.EMPTY || 
             field[rx][ry + 1] == GameUtils.Contents.DIRT ||
             field[rx][ry + 1] == GameUtils.Contents.DIAMOND ||
             field[rx][ry + 1] == GameUtils.Contents.FALLINGDIAMOND) { return false; }
          else {return true;}
        case UP:
          if(field[rx][ry - 1] == GameUtils.Contents.EMPTY || 
             field[rx][ry - 1] == GameUtils.Contents.DIRT ||
             field[rx][ry - 1] == GameUtils.Contents.DIAMOND||
             field[rx][ry - 1] == GameUtils.Contents.FALLINGDIAMOND) { return false; }
          else {return true;}
        case LEFT:
          if(field[rx - 1][ry] == GameUtils.Contents.EMPTY || 
             field[rx - 1][ry] == GameUtils.Contents.DIRT ||
             field[rx - 1][ry] == GameUtils.Contents.DIAMOND|| 
             field[rx - 1][ry] == GameUtils.Contents.FALLINGDIAMOND) { return false; }
          else {return true;}
        case RIGHT:
          if(field[rx + 1][ry] == GameUtils.Contents.EMPTY || 
             field[rx + 1][ry] == GameUtils.Contents.DIRT ||
             field[rx + 1][ry] == GameUtils.Contents.DIAMOND||
             field[rx + 1][ry] == GameUtils.Contents.FALLINGDIAMOND) { return false; }
          else {return true;}
      }
      return true;
    }
    
    public void paintComponent(Graphics g)
    {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.translate(-topX, -topY);
      for(int j = 0; j < HEIGHT; j++){
        for(int i = 0; i < WIDTH; i++){
          boolean isBoulder = false;
          switch(field[i][j]){
            case EMPTY:
              g2.setColor(Color.black);
              break;
            case DIRT:
              g2.setColor(new Color(165, 42, 42));
              break;
            case WALL:
              g2.setColor(Color.gray);
              break;
            case ROCK:
              isBoulder = true;
              g2.setColor(Color.pink);
              break;
            case FALLINGROCK:
              isBoulder = true;
              g2.setColor(Color.pink);
              break;
            case DIAMOND:
              g2.setColor(Color.white);
              break;
            case FALLINGDIAMOND:
              g2.setColor(Color.white);
              break;
            case AMOEBA:
              g2.setColor(new Color(0, 100, 0));
              break;
            case FIREFLY:
              g2.setColor(Color.orange);
              break;
            case BUTTERFLY:
              g2.setColor(Color.yellow);
              break;
            case EXIT:
              g2.setColor(Color.blue);
              break;
            case PLAYER:
              int seconds;
              Date date = new Date();
              seconds = date.getSeconds();
              if((seconds % 3 == 0) && (player.diamonds >= gu.diamondsNeeded)) {
                g2.setColor(Color.red);
              } else {
                g2.setColor(Color.green); }
              break;
          }
          if(isBoulder) { 
            g2.fill(new Ellipse2D.Double(i*20, j*20, 20, 20));
            isBoulder = false;
          }
          g2.fill(new Ellipse2D.Double(i*20, j*20, 20, 20));
        }
      }
      g2.translate(topX, topY);
    }
    
    public ArrayList<Monster> monsterUp(){
      ArrayList<Monster> ret = new ArrayList<Monster>();
      
      for(int x = 0; x < 38; x++) {
        for(int y = 0; y < 21; y++) {
          if(field[x][y] == GameUtils.Contents.FIREFLY) {
            Firefly f = new Firefly(x, y, Sprite.Directions.RIGHT);
            ret.add(f);
          }
          if(field[x][y] == GameUtils.Contents.BUTTERFLY){
            Butterfly b = new Butterfly(x, y, Sprite.Directions.LEFT);
            ret.add(b);
          }
          if(field[x][y] == GameUtils.Contents.AMOEBA) {
            Amoeba a = new Amoeba(x, y, Sprite.Directions.RANDOM);
            ret.add(a);
          }
        }
      }
      return ret;
    }
    
     public static void main(String[] args)
     {
       JFrame jf = new JFrame("CCPS 209 Final Project");
       jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       jf.setLayout(new FlowLayout());
       final BoulderDash bd = new BoulderDash();
       jf.add(bd);
       jf.pack();
       jf.setVisible(true);
     }
     
}