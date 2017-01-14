import java.awt.*;
import java.awt.image.*;
import java.net.*;
import java.applet.*;
import java.util.*;

/**
   A Go board applet <p>

   erstmal nur ein Feld, in das man Kreuze und Kreise packen kann
   (abgeguckt vom TicTacToe-Demo)
 */

public
class Go extends Applet {
  /** board size */
  static final int boardsize = 18;

  /**
   * invariant: board[row][col].row == row,
   *            board[row][col].col == col
   */
  Cell[][] board;

  Player player1, player2;

  int turn;  /* whose turn: 1 or 2 */

  public void init()
  {
    board = new Cell[boardsize][boardsize];
    for (int r=0; r < boardsize; r++) {
      for (int c=0 ; c < boardsize; c++) {
        board[r][c] = new Cell(this,r,c);
      }
    }
    turn = 1;
    player1 = new Player("Black", Color.black);
    player2 = new Player("Red", Color.red);
  }

  protected Dimension cellSize;

  void computeCellSize()
  {
    Dimension d = size();  /* ### Cache! */
    cellSize = new Dimension(d.width / boardsize, d.height / boardsize);
  }

  protected int gridX(int col)
  {
    return cellSize.width/2 + col * cellSize.width;
  }    

  protected int gridY(int row)
  {
    return cellSize.height/2 + row * cellSize.height;
  }    

  /**
   * Paint it.
   */
  public void paint(Graphics g) {
/*  ### exploit clipping information?
    Rectangle clip = g.getClipRect();
    System.out.print("clip x="+clip.x+" y="+clip.y
		   +" w="+clip.width+" h="+clip.height+"\n");
 */
    computeCellSize();
    g.setColor(Color.black);
    int left = gridX(0);
    int right = gridX(boardsize-1);
    int top = gridY(0);
    int bottom = gridY(boardsize-1);
    for(int i = 0; i < boardsize; ++i) {
      g.drawLine(gridX(i), top, gridX(i), bottom);
      g.drawLine(left, gridY(i), right, gridY(i));
    }

    for (int r=0; r < boardsize; r++) {
      for (int c=0 ; c < boardsize; c++) {
        board[r][c].paint(g);
      }
    }
  }

  public void repaint(Rectangle r) {
    repaint(r.x,r.y,r.width,r.height);
  }

  /**
   * translate pixel position to Cell
   */
  Cell whichCell(int x, int y)
  {
    computeCellSize();
    int c = x / cellSize.width;
    int r = y / cellSize.height;
    return board[r][c];
  }

  public boolean mouseDown(Event evt, int x, int y) {
    // Figure out the row/colum
    Cell cell = whichCell(x, y);
    if (cell != null && !cell.isEmpty()) {
      if (evt.shiftDown()) {
	// show chain
	cell.chain.blink();
      } else if (evt.metaDown()) {
	// show chain's liberties
	cell.chain.blinkLiberties();
      }
    }    
    return true;
  }

  public boolean mouseUp(Event evt, int x, int y) {
    // Figure out the row/colum
    Cell cell = whichCell(x, y);
    if (evt.shiftDown() || evt.metaDown()) {
      unblink();
    } else {
      // drop Piece
      if (cell.putPiece(currentPlayer()))
	changeTurn();
    }
    return true;
  }

  void unblink()
  {
    for (int r=0; r < boardsize; r++) {
      for (int c=0 ; c < boardsize; c++) {
        board[r][c].unblink();
      }
    }
  }   

  Player currentPlayer()
  {
     return turn == 1 ? player1 : player2;
  }

  void changeTurn()
  {
    turn = 3-turn;
    Player current = currentPlayer();
    showStatus(current.name + "'s turn");
  }
}

class Player {
  String name;
  Color color;
  int captured;  // number of enemy counters captured
  Player(String n, Color c)
  {
    name = n;
    color = c;
    captured = 0;
  }
}

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

/**
 * A chain of adjacent cells
 */
class Chain
{
  Go go;
  public Player owner;
  public Vector cells;

  public Chain(Cell c)
  {
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
  void merge(Chain b)
  {
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

  void blink()
  {
    Enumeration e = cells.elements();
    while (e.hasMoreElements()) {
      Cell c = (Cell) e.nextElement();
      c.blink();
    }
  }

  Vector liberties()
  {
    Vector libs = new Vector();
    LibertyFun f = new LibertyFun(libs);

    Enumeration e = cells.elements();
    while (e.hasMoreElements()) {
      Cell c = (Cell) e.nextElement();
      c.neighboursDo(f);
    }
    return libs;
  }

  void blinkLiberties()
  {
    Graphics g = go.getGraphics();

    Vector libs = liberties();
    Enumeration l = libs.elements();
    while (l.hasMoreElements()) {
      Cell c = (Cell) l.nextElement();
      c.blink();
    }
  }
}

interface CellFun {
  public void with(Cell c);
}

class LibertyFun implements CellFun {
  Vector liberties;
  protected LibertyFun(Vector libs)
  {
    liberties = libs;
  }
  public void with(Cell c)
  {
    if (c.owner == null && !liberties.contains(c)) {
      liberties.addElement(c);
    }
  }
}

class MergeFun implements CellFun {
  Chain chain;
  protected MergeFun(Chain ch)
  {
    chain = ch;
  }
  public void with(Cell c)
  {
    // swallow adjacent chains with same owner
    if (c.owner == chain.owner) {
      chain.merge(c.chain);
    }
  }    
}

class KillerFun implements CellFun {
  Player goodGuy;
  protected KillerFun(Player p)
  {
    goodGuy = p;
  }
  public void with(Cell c)
  {
    if (!c.isEmpty() && c.owner != goodGuy) {
      Chain ch = c.chain;
      Vector libs = ch.liberties();
      if (libs.isEmpty()) {
        // KILL! BLOOD! GORE!
        Enumeration e = ch.cells.elements();
	while (e.hasMoreElements()) {
	  Cell c1 = (Cell) e.nextElement();
	  // assert c1.owner == badGuy;
	  c1.setOwner(null);
	  goodGuy.captured++;
	}
      }
    }
  }    
}

