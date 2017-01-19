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
        if (!c.isEmpty() && c.getOwner() != goodGuy) {
            Chain ch = c.getChain();
            Set<Cell> libs = ch.getLiberties();
            if (libs.isEmpty()) {
                // this chain was captured.
                ch.unregister();
                for(Cell c1: ch.cells) {
                    assert c1.getOwner() != goodGuy;
                    assert c1.getOwner() != null;
                    c1.setOwner(null);
                    goodGuy.captured++;
                }
            }
        }
    }
}
