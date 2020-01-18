package net.inhostudios.doorcam.image;

import net.inhostudios.doorcam.Globals;
import net.inhostudios.doorcam.MessageHandler;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;
import sun.plugin2.message.Message;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Camera extends JComponent implements Runnable{

    private static final long serialVersionUID = 1L;

    // initializing starter variables
    private static CanvasFrame frame = new CanvasFrame("Webcam");
    private static boolean running = false;
    private static int frameWidth = 1280;
    private static int frameHeight = 720;
    private BufferedImage screenshot;
    private boolean detected = false, sent = false;

    private String path = Globals.resources + "\\image.jpg";

    private MessageHandler mh;

    private Date date;
    private DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy");
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss z");

    private long curTime = 0;
    private int diff = 5 * 60000;

    // image grabbing object from open CV api
    private static OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    private static BufferedImage bufImg;

    public Camera(){
        // setting camera size
        frame.setSize(frameWidth, frameHeight);

        // map for keyboard inputs
        ActionMap actionMap = frame.getRootPane().getActionMap();
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        // adding key inputs to key maps
        for (Keys direction : Keys.values())
        {
            actionMap.put(direction.getText(), new KeyBinding(direction.getText()));
            inputMap.put(direction.getKeyStroke(), direction.getText());
        }

        // adding key listeners to the frame
        frame.getRootPane().setActionMap(actionMap);
        frame.getRootPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);

        // setup window listener for close action
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                try {
                    grabber.stop();
                } catch (FrameGrabber.Exception e1) {
                    e1.printStackTrace();
                }
                stop();
            }
        });

        mh = new MessageHandler();
    }

    // run method
    public void run(){
        // starting the thread for the frame grabbing object
        try {
            grabber.start();
            System.out.println("grabber started");
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        // while running loop
        while(running){
            // grabbing the image from the frame
            try{
                grabber.setImageWidth(frameWidth);
                grabber.setImageHeight(frameHeight);
                // grabbing image from the screen into a frame object
                Frame cvimg = grabber.grab();
                //converting it to the ipl image fileformat
                OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
                opencv_core.IplImage img = converter.convert(cvimg);
                //showing it on the screen if the image exists
                // error might be happening here? if the image doesn't exist it might not show image, break, pass through and end the thread
                if(cvimg != null){
                    screenshot = IplImageToByteArray(img); // converting iplimage to bufferedimage
                    frame.showImage(converter.convert(img));
                }
            } catch(Exception e){
                e.printStackTrace();
            }

            // save the image and process it (in the save image method)
            saveImage(screenshot);

            // send notification if detected
            sendText();

            // handler on a timer
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Ending camera thread");
        // ending the grabber thread
        try {
            grabber.stop();
            grabber.release();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        // ending the frame thread
        frame.dispose();
        System.out.println("Closing camera");
    }

    private void sendText() {
        if(System.currentTimeMillis() > curTime + diff) {
            date = new Date();
            if (detected && !sent) {
                String message = "Person detected at door on " + dateFormat.format(date) + " at " + timeFormat.format(date);
                sent = true;
                // send notification
                mh.sendMessage(message);
                System.out.println("Message sent");
            } else if (!detected) {
                sent = false;
            }
            curTime = System.currentTimeMillis();
        }
    }

    // starting the thread
    public void start()
    {
        new Thread(this).start();
        running = true;
    }

    // stopping the thread running boolean
    public void stop()
    {
        running = false;
    }


    // conversion helper for ipl images to byte arrays
    public static BufferedImage IplImageToByteArray(opencv_core.IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }

    // save image class
    public void saveImage(BufferedImage bufferedImage){
        File outputFile = new File(path);
        try {
            ImageIO.write(bufferedImage, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(detected = processImage()) System.out.println("Person detected");
        else System.out.println("no person detected");
    }

    private boolean processImage() {
        try {
            for(String str : ImageHandler.detectLabels(path)) {
                if(str.toLowerCase().equals("person") || str.toLowerCase().equals("people") || str.toLowerCase().equals("man") || str.toLowerCase().equals("woman")) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // key binding object for key mapping
    private class KeyBinding extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public KeyBinding(String text)
        {
            super(text);
            putValue(ACTION_COMMAND_KEY, text);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            String action = e.getActionCommand();
            if (action.equals(Keys.ESCAPE.toString())) stop();
            else System.out.println("Key Binding: " + action);

            // taking a screenshot
            if(action.equals(Keys.CTRLC.toString())){
                //flash
//                try {
////                    BufferedImage flash = ImageIO.read(new File(System.getProperty("user.dir") +
////                            "\\src\\main\\resources\\flash.jpg"));
////                    frame.showImage(flash);
//                } catch (FileNotFoundException e1) {
//                    e1.printStackTrace();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }

                saveImage(screenshot);
            }
        }
    }
}

// honestly i don't know exactly how this works but it shouldn't be the reason it stops working
enum Keys
{
    ESCAPE("Escape", KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)),
    CTRLC("Control-S", KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)),
    UP("Up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0)),
    DOWN("Down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)),
    LEFT("Left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)),
    RIGHT("Right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));

    private String text;
    private KeyStroke keyStroke;

    Keys(String text, KeyStroke keyStroke)
    {
        this.text = text;
        this.keyStroke = keyStroke;
    }

    public String getText()
    {
        return text;
    }

    public KeyStroke getKeyStroke()
    {
        return keyStroke;
    }

    @Override
    public String toString()
    {
        return text;
    }
}