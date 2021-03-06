import processing.core.PVector;

/**
 *
 * @author David Kosub
 * @author Jeffery Wooldridge
 * @author Matthew A. Johnston
 * @author Trevor Vardeman
 * @author Carlos Martinez
 */
public abstract class StatementInstance implements Drawable {
  public static int SPACING = 5;

  protected PVector _pos;
  protected boolean _locked = false;

  public StatementInstance() {
    _pos = new PVector();
  }

  public void setPos(int x, int y) {
    _pos.x = x;
    _pos.y = y;
  }

  public PVector getPos() {
    return _pos;
  }

  public abstract boolean isChild();
  public abstract StatementInstance instanceUnder(int $x, int $y);
  public abstract int getHeight();
}

