package net.wuenschenswert.go;

import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A chain of adjacent cells
 */
class Chain {
  Go go;
  public Player owner;
  public Vector cells;

  public Chain(Cell c) {
    owner = c.owner;
    go = c.go;
    cells = new Vector(1);
    cells.addElement(c);
  }

  /**
   * add all cells in <b> to this
   * requires that this and b are disjoint and belong to the same Player
   * and are adjacent
   */
  void merge(Chain b) {
    if (b != this) {
      // assert b.owner == owner;
      Enumeration e = b.cells.elements();
      while (e.hasMoreElements()) {
        Cell c = (Cell) e.nextElement();
        // assert !cells.contains(c);
        c.chain = this;
        cells.addElement(c);
      }
      b.cells = null;  // avoid accidents
      b.owner = null;  // avoid accidents
    }
  }

  void blink() {
    Enumeration e = cells.elements();
    while (e.hasMoreElements()) {
      Cell c = (Cell) e.nextElement();
      c.blink();
    }
  }

  Vector liberties() {
    Vector libs = new Vector();
    LibertyFun f = new LibertyFun(libs);

    Enumeration e = cells.elements();
    while (e.hasMoreElements()) {
      Cell c = (Cell) e.nextElement();
      c.neighboursDo(f);
    }
    return libs;
  }

  void blinkLiberties() {
    Graphics g = go.getGraphics();

    Vector libs = liberties();
    Enumeration l = libs.elements();
    while (l.hasMoreElements()) {
      Cell c = (Cell) l.nextElement();
      c.blink();
    }
  }
}
