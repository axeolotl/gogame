package net.wuenschenswert.go;

import net.wuenschenswert.go.*;

import java.awt.*;
import java.applet.*;

/**
 A Go board applet <p>

 erstmal nur ein Feld, in das man Kreuze und Kreise packen kann
 (abgeguckt vom TicTacToe-Demo)
 */

public class Go extends Applet {
  /** board size */
  int boardsize = 18;

  /**
   * invariant: board[row][col].row == row,
   *            board[row][col].col == col
   */
  Cell[][] board;

  Player player1, player2;

  int turn;  /* whose turn: 1 or 2 */

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
    return board[r][c];
  }

  public boolean mouseDown(Event evt, int x, int y) {
    // Figure out the row/colum
    Cell cell = whichCell(x, y);
    if (cell != null && !cell.isEmpty()) {
      if (evt.shiftDown()) {
        // show chain
        cell.getChain().blink();
      } else if (evt.metaDown()) {
        // show chain's liberties
        cell.getChain().blinkLiberties();
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

  void changeTurn() {
    turn = 3-turn;
    Player current = currentPlayer();
    showStatus(current.name + "'s turn");
  }
}

