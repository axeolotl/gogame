package net.wuenschenswert.go;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by axel on 23.01.17.
 */
public class History {
  private Go go;
  private List<BoardState> history = new ArrayList<>();
  private int redoPosition = 0;

  public History(Go go) {
    this.go = go;
  }

  boolean canUndo() {
    return redoPosition > 1;
  }

  void undo() {
    redoPosition -= 1;
    go.restoreState(history.get(redoPosition - 1));
  }

  boolean canRedo() {
    return redoPosition < history.size();
  }

  void redo() {
    go.restoreState(history.get(redoPosition));
    redoPosition += 1;
  }

  void recordState() {
    while(history.size() > redoPosition) {
      history.remove(history.size()-1);
    }
    history.add(BoardState.createFrom(go));
    ++redoPosition;
  }
}
