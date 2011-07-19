class Firefly extends Monster {
  
  public Firefly(int x, int y, Sprite.Directions d) {
    super(x, y, d);
  }
  
  public void changeDirection() {
    switch(myd){
      case LEFT:
        myd = Sprite.Directions.UP;
        break;
      case RIGHT:
        myd = Sprite.Directions.DOWN;
        break;
      case UP:
        myd = Sprite.Directions.RIGHT;
        break;
      case DOWN:
        myd = Sprite.Directions.LEFT;
        break;
    }
  }
}