abstract class Sprite
{
  public int mx;
  public int my;
  public enum Directions {LEFT, RIGHT, UP, DOWN, RANDOM}  
  
  public int getX() 
  {
    return mx;
  }
  
  public int getY()
  {
    return my;
  }
  
  public void setX(int x)
  {
    mx = x;
  }
  
  public void setY(int y)
  {
    my = y;
  }
  
  public int[] getLocation()
  {
    int[] loc = new int[2];
    loc[0] = mx;
    loc[1] = my;
    return loc;
  }

}