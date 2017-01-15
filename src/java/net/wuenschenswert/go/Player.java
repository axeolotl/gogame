package net.wuenschenswert.go;

import java.awt.*;

/**
 * Created by axel on 15.01.17.
 */
class Player {
  String name;
  Color color;
  int captured;  // number of enemy counters captured
  Player(String n, Color c) {
    name = n;
    color = c;
    captured = 0;
  }
}
