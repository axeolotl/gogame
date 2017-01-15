package net.wuenschenswert.go;

import java.awt.*;
import java.util.Enumeration;

/**
 * Created by axel on 15.01.17.
 */
class Cell {
  public Go go;
  public int row,col;
  /**
   * the Player whose stone occupies this field, or null
   */
  public Player owner;

  /**
   * if owner non-null:
   * the chain this Cell belongs to.
   */
  public Chain chain;

  boolean blinking;

  public boolean isEmpty()
  {
    return owner == null;
  }

  public String toString()
  {
    return "Cell{"+row+","+col+","+ (owner==null ? "/" : owner.name)+"}";
  }

  public Cell(Go g, int r, int c)
  {
    go = g;
    row = r;
    col = c;
    owner = null;
    chain = null;
    blinking = false;
  }

  void neighboursDo(CellFun f)
  {
    if (col > 0) {
      f.with(go.board[row][col-1]);
    }
    if (col < go.boardsize-1) {
      f.with(go.board[row][col+1]);
    }
    if (row > 0) {
      f.with(go.board[row-1][col]);
    }
    if (row < go.boardsize-1) {
      f.with(go.board[row+1][col]);
    }
  }

  Rectangle bounds()
  {
    int x = go.gridX(col);
    int y = go.gridY(row);
    int w = go.cellSize.width;
    int h = go.cellSize.height;
    x -= w/2;
    y -= h/2;
    return new Rectangle(x,y,w,h);
  }

  void paint(Graphics g)
  {
    if (owner != null || blinking) {
      g.setColor(blinking ? Color.green : owner.color);
      int x = go.gridX(col);
      int y = go.gridY(row);
      int w = go.cellSize.width * 8 / 10;
      int h = go.cellSize.height * 8 / 10;
      x -= w/2;
      y -= h/2;
      g.fillOval(x,y,w,h);
      g.setColor(Color.black);
      g.drawOval(x,y,w,h);
    }
  }

  void blink()
  {
    if (!blinking) {
      blinking = true;
      paint(go.getGraphics());
    }
  }

  void unblink()
  {
    if (blinking) {
      blinking = false;
      go.repaint(bounds());
    }
  }

  void setOwner(Player p)
  {
    if (owner != p) {
      owner = p;
      chain = null;
      go.repaint(bounds());
    }
  }

  boolean putPiece(Player player)
  {
    if (owner == null) {
      setOwner(player);
      chain = new Chain(this);
      // merge with adjacent chains of same player
      neighboursDo(new MergeFun(chain));
      // check for killed chains
      neighboursDo(new KillerFun(player));
      // to do: suicide rule, ko rule

      System.out.print("Chain: ");
      Enumeration e = chain.cells.elements();
      while (e.hasMoreElements()) {
        System.out.print(e.nextElement()+" ");
      }
      System.out.println();

      System.out.print("Liberties: ");
      Enumeration l = chain.liberties().elements();
      while (l.hasMoreElements()) {
        System.out.print(l.nextElement()+" ");
      }
      System.out.println();

      return true;
    }
    return false;
  }
}
