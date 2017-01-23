package net.wuenschenswert.go;

import java.awt.*;
import java.applet.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

/**
 A Go board applet <p>

 erstmal nur ein Feld, in das man Kreuze und Kreise packen kann
 (abgeguckt vom TicTacToe-Demo)
 */

public class Go extends Applet {
  /** board size */
  int boardsize = 9;

  /**
   * invariant: board[row][col].row == row,
   *            board[row][col].col == col
   */
  Cell[][] board;

  Player player1, player2;

  int turn;  /* whose turn: 1 or 2 */

  List<BoardState> history = new ArrayList<>();

  public void init() {
    board = new Cell[boardsize][boardsize];
    for (int r=0; r < boardsize; r++) {
      for (int c=0 ; c < boardsize; c++) {
        board[r][c] = new Cell(this,r,c);
      }
    }
    turn = 1;
    player1 = new Player("Black", Color.black);
    player2 = new Player("Red", Color.red);
    recordHistory();
    enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);
  }

  protected Dimension cellSize;

  void computeCellSize() {
    Dimension d = size();  /* ### Cache! */
    cellSize = new Dimension(d.width / boardsize, d.height / boardsize);
  }

  protected int gridX(int col) {
    return cellSize.width/2 + col * cellSize.width;
  }

  protected int gridY(int row) {
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
  Cell whichCell(int x, int y) {
    computeCellSize();
    int c = x / cellSize.width;
    int r = y / cellSize.height;
    if (c < boardsize && r < boardsize) {
      return board[r][c];
    } else {
      return null;
    }
  }

  @Override
  protected void processMouseEvent(MouseEvent e) {
    if(e.getID() == Event.MOUSE_DOWN) {
      mouseDown(e.getModifiers(), e.getX(), e.getY());
    } else if(e.getID() == Event.MOUSE_UP) {
      mouseUp(e.getModifiers(), e.getX(), e.getY());
    }
  }

  public boolean mouseDown(int modifiers, int x, int y) {
    // Figure out the row/colum
    Cell cell = whichCell(x, y);
    if (cell != null && !cell.isEmpty()) {
      if ((modifiers & Event.SHIFT_MASK) != 0) {
        // show chain
        cell.getChain().blink();
      } else if ((modifiers & Event.META_MASK) != 0) {
        // show chain's liberties
        cell.getChain().blinkLiberties();
      }
    }
    return true;
  }

  public boolean mouseUp(int modifiers, int x, int y) {
    // Figure out the row/colum
    Cell cell = whichCell(x, y);
    if ((modifiers & (Event.SHIFT_MASK|Event.META_MASK)) != 0) {
      unblink();
    } else if (cell != null) {
      // drop Piece
      if (cell.putPiece(currentPlayer())) {
        recordHistory();
        changeTurn();
      }
    }
    return true;
  }


  @Override
  protected void processKeyEvent(KeyEvent e) {
    if(canUndo()
        && e.getID() == KeyEvent.KEY_PRESSED
        && e.getKeyChar() == 'z'
        && (e.getModifiersEx() & (KeyEvent.META_DOWN_MASK  | KeyEvent.SHIFT_DOWN_MASK)) == KeyEvent.META_DOWN_MASK) {
      // z pressed with META, but not SHIFT.
      System.out.println("UNDO! "+e);
      undo();
      changeTurn();
    } else {
      System.out.println("key "+e);
    }
    super.processKeyEvent(e);
  }

  private boolean canUndo() {
    return history.size() > 1;
  }

  private void undo() {
    history.remove(history.size()-1);
    restoreState(history.get(history.size()-1));
  }

  private void recordHistory() {
    history.add(BoardState.createFrom(this));
  }

  private void restoreState(BoardState boardState) {
    // TODO: incremental change
    for (int r=0; r < boardsize; r++) {
      for (int c=0 ; c < boardsize; c++) {
        board[r][c].setChain(null);
        board[r][c].setOwner(null);
      }
    }
    for (int r=0; r < boardsize; r++) {
      for (int c=0 ; c < boardsize; c++) {
        Player owner = boardState.getOwner(r, c);
        if (owner != null) {
          boolean success = board[r][c].putPiece(owner);
          assert success;
        }
      }
    }
  }

  void unblink() {
    for (int r=0; r < boardsize; r++) {
      for (int c=0 ; c < boardsize; c++) {
        board[r][c].unblink();
      }
    }
  }

  Player currentPlayer() {
    return turn == 1 ? player1 : player2;
  }
  Player getOpponent(Player player) {
    return player == player1 ? player2 : player1;
  }

  void changeTurn() {
    turn = 3-turn;
    Player current = currentPlayer();
    showStatus(current.name + "'s turn");
  }
}

