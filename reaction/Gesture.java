package reaction;

import graphicslib.G;
import graphicslib.I;

import java.util.ArrayList;

public class Gesture {

  public Shape shape;
  public G.VS vs;
  private static List UNDO = new List();
  public static I.Area AREA = new I.Area() {
    @Override
    public boolean hit(int x, int y) {
      return true;
    }

    @Override
    public void dn(int x, int y) {
      Ink.BUFFER.dn(x, y);
    }

    @Override
    public void drag(int x, int y) {
      Ink.BUFFER.drag(x, y);
    }

    @Override
    public void up(int x, int y) {
      Ink.BUFFER.add(x, y);
      Ink ink = new Ink();
      Gesture gesture = Gesture.getNew(ink);
      Ink.BUFFER.clear();
      if (gesture != null) {
        if (gesture.shape.name.equals("N-N")) {
          undo();
        } else {
          gesture.doGestureAndAdd();
        }
//        Reaction r = Reaction.best(gesture);
//        if (r != null) {
//          r.act(gesture);
//        }
      }
    }
  };

  // does not add to undo list
  public void doGesture() {
    Reaction r = Reaction.best(this);
    if (r != null) {
      r.act(this);
    }
  }

  // does add to undo list
  public void doGestureAndAdd() {
    Reaction r = Reaction.best(this);
    if (r != null) {
      UNDO.add(this);
      r.act(this);
    }
  }

  public static void undo() {
    if (UNDO.size() > 0) {
      UNDO.remove(UNDO.size() - 1);
      Layer.nuke();
      Reaction.nuke();
      //
      Reaction.initialAction.act(null);
      //
      UNDO.redo();
    }
  }

  private Gesture(Shape shape, G.VS vs) {
    this.shape = shape;
    this.vs = vs;
  }

  public static Gesture getNew(Ink ink) {
    Shape s = Shape.recognize(ink);
    return s == null ? null : new Gesture(s, ink.vs);
  }

  public static class List extends ArrayList<Gesture> {

    public void redo() {
      for (Gesture gesture : this) {
        gesture.doGesture();
      }
    }
  }

}
