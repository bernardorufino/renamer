package init;

import gui.MainWindow;

import java.awt.EventQueue;
import java.io.File;
import java.util.HashMap;

import model.Renamer;

public class Initializer {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
