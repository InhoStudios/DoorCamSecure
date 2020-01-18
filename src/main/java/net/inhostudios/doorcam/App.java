package net.inhostudios.doorcam;

import net.inhostudios.doorcam.image.Camera;
import net.inhostudios.doorcam.image.ImageHandler;

public class App {

    public static void main(String[] args) {
        Camera cm = new Camera();
        cm.start();
    }

}
