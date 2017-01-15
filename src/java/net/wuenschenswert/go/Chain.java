package net.wuenschenswert.go;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A chain of adjacent cells
 */
class Chain {
  Go go;
  public Player owner;
  public List<Cell> cells;

  public Chain(Cell c) {
    owner = c.owner;
    go = c.go;
    cells = new ArrayList<>(1);
    cells.add(c);
  }

  /**
   * add all cells in <b> to this
   * requires that this and b are disjoint and belong to the same Player
   * and are adjacent
   */
  void merge(Chain b) {
    if (b != this) {
      assert b.owner == owner;
      for (Cell c: b.cells) {
        assert !cells.contains(c);
        c.chain = this;
        cells.add(c);
      }
      b.cells = null;  // avoid accidents
      b.owner = null;  // avoid accidents
    }
  }

  void blink() {
    for (Cell c: cells) {
      c.blink();
    }
  }

  Set<Cell> getLiberties() {
    LibertyFun f = new LibertyFun();

    for (Cell c: cells) {
      c.neighboursDo(f);
    }
    return f.getLiberties();
  }

  void blinkLiberties() {
    for (Cell c: getLiberties()) {
      c.blink();
    }
  }
}
