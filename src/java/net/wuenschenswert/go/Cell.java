package net.wuenschenswert.go;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by axel on 15.01.17.
 */
class Cell {
  public final Go go;
  public final int row,col;
  private Player owner;

  private Chain chain;

  boolean blinking;

  public boolean isEmpty()
  {
    return getOwner() == null;
  }

  public String toString()
  {
    return "Cell{"+row+","+col+","+ (getOwner() ==null ? "/" : getOwner().name)+"}";
  }

  public Cell(Go g, int r, int c)
  {
    go = g;
    row = r;
    col = c;
    setOwner(null);
    setChain(null);
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
    if (getOwner() != null || blinking) {
      g.setColor(blinking ? Color.green : getOwner().color);
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
      go.repaint(bounds());
    }
  }

  boolean putPiece(Player player)
  {
    if (getOwner() == null) {
      Chain newChain = getNewChain(player);

      if (newChain.getLiberties().isEmpty()) {
        // putting a piece here would violate the suicide rule.
        // ...but only if it does not make room by capturing an enemy chain!
        boolean makesRoom = false;
        for(Chain ch: getNeighbourChains(go.getOpponent(player))) {
          if(ch.getLiberties().equals(Collections.singleton(this))) {
            makesRoom = true;
            break;
          }
        }
        if (!makesRoom) {
          return false;
        }
      }

      setOwner(player);
      for (Chain chain: getNeighbourChains(player)) {
        chain.unregister();
      }
      newChain.register();
      // check for killed chains
      neighboursDo(new KillerFun(player));
      // to do: ko rule

      System.out.print("Chain: ");
      for(Cell c: chain.cells) {
        System.out.print(c+" ");
      }
      System.out.println();

      System.out.print("Liberties: ");
      for (Cell l: chain.getLiberties()) {
        System.out.print(l+" ");
      }
      System.out.println();

      return true;
    }
    return false;
  }

  /**
   * returns the chain that would be created if the given player put a piece here.
   * @param player
   * @return chain
   */
  private Chain getNewChain(final Player player) {
    // new chain would be union of this player's adjacent chains plus this cell
    final Chain newChain = new Chain(this, player);
    for(Chain ch: getNeighbourChains(player)) {
      newChain.addCells(ch.cells);
    }
    return newChain;
  }

  private Set<Chain> getNeighbourChains(final Player player) {
    final Set<Chain> neighbourChains = new HashSet<>();
    neighboursDo(new CellFun() {
      @Override
      public void with(Cell c) {
        if(c.getOwner() == player) {
          neighbourChains.add(c.chain);
        }
      }
    });
    return neighbourChains;
  }

  public Chain getChain() {
    return chain;
  }

  public void setChain(Chain chain) {
    assert this.chain == null || chain == null;
    this.chain = chain;
  }

  /**
   * the Player whose stone occupies this field, or null
   */
  public Player getOwner() {
    return owner;
  }
}
