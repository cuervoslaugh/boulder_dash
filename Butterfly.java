class Butterfly extends Monster {
  
  public Butterfly(int x, int y, Sprite.Directions d) {
    super(x, y, d);
  }
  
  public void changeDirection() {
    switch(myd){
      case RIGHT:
        myd = Sprite.Directions.UP;
        break;
      case LEFT:
        myd = Sprite.Directions.DOWN;
        break;
      case UP:
        myd = Sprite.Directions.LEFT;
        break;
      case DOWN:
        myd = Sprite.Directions.RIGHT;
        break;
    }
  }
}