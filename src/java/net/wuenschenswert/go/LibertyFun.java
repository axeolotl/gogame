package net.wuenschenswert.go;

import java.util.Vector;

/**
 * Created by axel on 15.01.17.
 */
class LibertyFun implements CellFun {
  Vector liberties;
  protected LibertyFun(Vector libs)
  {
    liberties = libs;
  }
  public void with(Cell c) {
    if (c.owner == null && !liberties.contains(c)) {
      liberties.addElement(c);
    }
  }
}
