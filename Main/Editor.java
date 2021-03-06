import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import processing.core.PVector;
import processing.core.PGraphics;

import java.awt.event.MouseEvent;

/**
 *
 * @author David Kosub
 * @author Jeffery Wooldridge
 * @author Matthew A. Johnston
 * @author Trevor Vardeman
 * @author Carlos Martinez
 */
public class Editor implements Screen
{
  //Fill color of the background.
  private static final int FILL_COLOR = 0xFFFFFFFF;

  //Vector for the dimensions of the screen and the toolbar.
  private PVector _dims, _toolbar;

  //Cordinates where the mouse started dragging, in integer form.
  private int _drag_start_x, _drag_start_y;

  //For drawing each icon in the toolbar.
  private int _icon_bounds, _icon_size, _icon_offset;

  //Related to the drag handling.
  private boolean _dragstart;

  //List to contain all of our drawable elements.
  StatementInstance _instance;
  List<StatementInstance> _instances = new LinkedList<StatementInstance>();

  //Map containing our statements.
  private static final Map<Class, ProgrammingStatement> _statements;
  static
  {
    Map<Class, ProgrammingStatement> map = new HashMap<Class, ProgrammingStatement>();

    //Insert our control structures.
    map.put(IfStatement.class, new IfStatement());

    //Make it final!
    _statements = Collections.unmodifiableMap(map);
  }

  public Editor(final PVector $dims)
  {
    _dims = $dims;
    _toolbar = new PVector($dims.x * 0.15f, $dims.y);

    //The bounds is the overall size of the rectangle.
    _icon_bounds = (int)_toolbar.x;
    //The offset is the padding around the rectangle.
    _icon_offset = (int)(_icon_bounds * 0.1f);
    //The size is the size of the rectangle to draw.
    _icon_size = _icon_bounds - _icon_offset * 2;
  }

  public void update(final float $dt)
  {
  }

  public void draw(final PGraphics $graphics)
  {
    //Draw the core screen.
    $graphics.fill(FILL_COLOR);
    $graphics.rect(0, 0, _dims.x, _dims.y);

    //Draw the editor toolbar.
    $graphics.rect(0, 0, _toolbar.x, _toolbar.y);

    //Draw each statement icon.
    int offset = 0;
    for(Class statement : _statements.keySet())
    {
      ProgrammingStatement ps = _statements.get(statement);
      $graphics.fill(ps.getColor());
      $graphics.rect(_icon_offset, offset + _icon_offset, _icon_size, _icon_size);

      offset += _icon_bounds;
    }

    for(Drawable instance : _instances)
    {
      instance.draw($graphics);
    }
  }

  public void handleMouseEvent(MouseEvent $event)
  {
    int x = $event.getX(),
        y = $event.getY();

    switch($event.getID())
    {
    case MouseEvent.MOUSE_PRESSED:
      _dragstart = true;
      _drag_start_x = x;
      _drag_start_y = y;
      break;
    case MouseEvent.MOUSE_DRAGGED:
      //If we are about to start dragging an item from the toolbar.
      if(_dragstart && _drag_start_x > _icon_offset && _drag_start_x < _icon_offset + _icon_size)
      {
        //Stop dragging.
        _dragstart = false;

        //Helper values.
        int index = _drag_start_y / _icon_bounds,
            pos = _drag_start_y % _icon_bounds;

        //Get an array of statement types on the toolbar.
        Class _statement_types[] = _statements.keySet().toArray(new Class[0]);

        if(index < _statement_types.length && pos > _icon_offset && pos < _icon_offset + _icon_size)
        {
          _instance = _statements.get(_statement_types[index]).spawnInstance();
          _instances.add(_instance);
        }
        else
        {
          _instance = null;
        }
      }
      //Otherwise we're on the board, let's find exactly which element is hovered over.
      else if(_dragstart)
      {
        for(StatementInstance instance : _instances)
        {
          StatementInstance under = instance.instanceUnder(_drag_start_x, _drag_start_y);

          if(under != null)
          {
            _instance = under;
            break;
          }
        }
      }

      //If we are dragging an item.
      if(_instance != null)
      {
        _instance.setPos(x, y);
      }
      break;
    case MouseEvent.MOUSE_RELEASED:
      _dragstart = false;

      //If we are dropping a currently carried instance.
      if(_instance != null)
      {
        //Check and see if the instance is over the toolbar (that means we want to delete it)
        if(x < _toolbar.x)
        {
          _instances.remove(_instance);
        }
        else
        {
          int i, len;
          //Remove the instance from the current list.
          _instances.remove(_instance);

          //Find where it belongs using it's y position.
          for(i = 0, len = _instances.size(); i < len; i++)
          {
            if(_instances.get(i).getPos().y > y)
            {
              _instances.add(i, _instance);
              break;
            }
          }

          //This means we reached the end of the list.
          if(i == len)
          {
            _instances.add(_instance);
          }
        }

        reshuffle();
        _instance = null;
      }
      break;
    }
  }

  private void reshuffle()
  {
    int offset_y = StatementInstance.SPACING,
        offset_x = (int)_toolbar.x + StatementInstance.SPACING;

    for(StatementInstance instance : _instances)
    {
      instance.setPos(offset_x, offset_y);
      offset_y += StatementInstance.SPACING + instance.getHeight();
    }
  }
}

