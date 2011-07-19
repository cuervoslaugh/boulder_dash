abstract class Monster extends Sprite
{
  public boolean alive = true;  
  public enum Monsters {FIREFLY, BUTTERFLY} 
  public Directions myd;
  
  public Monster(int x, int y, Sprite.Directions d)
  {
    mx = x;
    my = y;
    myd = d;
  }
  
  
  public abstract void changeDirection();
    
  public Directions getDirection()
  {
    return myd;
  }
  
}