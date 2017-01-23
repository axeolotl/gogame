package net.wuenschenswert.go;

/**
 * Created by axel on 23.01.17.
 */
public class BoardState {
  Player[][] state;
  private int boardsize;

  private BoardState(int boardsize) {
    this.boardsize = boardsize;
    state = new Player[boardsize][];
    for(int i=0; i<boardsize; ++i) {
      state[i] = new Player[boardsize];
    }
  }

  static BoardState createFrom(Go go) {
    int boardsize = go.boardsize;
    BoardState result = new BoardState(boardsize);
    for (int r = 0; r < boardsize; r++) {
      for (int c = 0; c < boardsize; c++) {
        result.state[r][c] = go.board[r][c].getOwner();
      }
    }
    return result;
  }

  public int getBoardsize() {
    return boardsize;
  }

  public Player getOwner(int r, int c) {
    return state[r][c];
  }
}
