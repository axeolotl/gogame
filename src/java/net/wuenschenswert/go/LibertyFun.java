package net.wuenschenswert.go;

import java.util.HashSet;
import java.util.Set;

class LibertyFun implements CellFun {
  private Set<Cell> liberties = new HashSet<>();
  public void with(Cell c) {
    if (c.getOwner() == null) {
      liberties.add(c);
    }
  }
  Set<Cell> getLiberties() {
    return liberties;
  }
}
