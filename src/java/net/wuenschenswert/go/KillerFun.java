package net.wuenschenswert.go;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by axel on 15.01.17.
 */
class KillerFun implements CellFun {
    Player goodGuy;
    protected KillerFun(Player p) {
        goodGuy = p;
    }
    public void with(Cell c) {
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
