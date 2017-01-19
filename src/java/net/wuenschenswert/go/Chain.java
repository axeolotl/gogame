package net.wuenschenswert.go;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A chain of adjacent cells
 */
class Chain {
  public Player owner;
  public List<Cell> cells;

  public Chain(Cell c, Player owner) {
    this.owner = owner;
    cells = new ArrayList<>(1);
    cells.add(c);
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

  /**
   * register this chain as each member cell's chain
   */
  public void register() {
    for(Cell c: cells) {
      assert(c.getOwner() == owner);
      c.setChain(this);
    }
  }

  /**
   * unregister this chain as each member cell's chain (when being captured)
   */
  public void unregister() {
    for(Cell c: cells) {
      assert(c.getChain() == this);
      c.setChain(null);
    }

  }

  public void addCells(List<Cell> cells) {
    this.cells.addAll(cells);
  }
}
