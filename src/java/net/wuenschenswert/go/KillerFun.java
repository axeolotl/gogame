package net.wuenschenswert.go;

import java.util.Set;

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
            Set<Cell> libs = ch.getLiberties();
            if (libs.isEmpty()) {
                // KILL! BLOOD! GORE!
                for(Cell c1: ch.cells) {
                    assert c1.owner != goodGuy;
                    assert c1.owner != null;
                    c1.setOwner(null);
                    goodGuy.captured++;
                }
            }
        }
    }
}
