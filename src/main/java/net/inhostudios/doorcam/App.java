package net.inhostudios.doorcam;

import net.inhostudios.doorcam.image.Camera;
import net.inhostudios.doorcam.image.ImageHandler;

public class App {

    public static void main(String[] args) {
        try {
            ImageHandler.detectLabels(Globals.resources + "\\Me.jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Camera cm = new Camera();
        cm.start();
    }

}
