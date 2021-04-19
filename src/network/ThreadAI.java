package network;

import build.Main;
import components.gui.Table;


public class ThreadAI extends Thread {
    private Main main;
    private int turn;
    public static boolean runing = false;

    public ThreadAI(Main _main, int _turn) {
        this.main = _main;
        this.turn = _turn;
        this.start();
    }

    public void run() {
        Table.get().show();
        main.setVisible(false);
    }

}
