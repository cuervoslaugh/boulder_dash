class Player extends Sprite 
{
  public int diamonds;
  public boolean alive = true;
  public int points = 0;
  
  Player() {
    mx = 0;
    my = 0;
  }
  
  Player(int x, int y) {
    mx = x;
    my = y;
  }
  
  public void increasePoints(int x)
  {
    diamonds += 1;
    points += x;
  }
}