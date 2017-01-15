package net.wuenschenswert.go;

/**
 * Created by axel on 15.01.17.
 */
class MergeFun implements CellFun {
  Chain chain;
  protected MergeFun(Chain ch) {
    chain = ch;
  }
  public void with(Cell c) {
    // swallow adjacent chains with same owner
    if (c.owner == chain.owner) {
      chain.merge(c.chain);
    }
  }
}
