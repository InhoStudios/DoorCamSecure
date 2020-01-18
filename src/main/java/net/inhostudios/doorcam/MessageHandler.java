package net.inhostudios.doorcam;

import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import com.twilio.rest.api.v2010.account.Message;
import net.dv8tion.jda.api.MessageBuilder;

import java.io.File;

public class MessageHandler {

    private String[] dateAndTime = {"", ""};

    public MessageHandler() {
        Twilio.init(Tokens.TWILIO_ACCT_SID, Tokens.TWILIO_AUTH_TOKEN);

    }

    public void sendMessage(String[] str, File image) {
        // sending image
        if (App.globalChannel != null) {
            net.dv8tion.jda.api.entities.Message msg = new MessageBuilder().append(
                    "Hey, " + App.tagID + "! " + "Your camera caught someone at your door"
                    + "\n `Date: " + str[0] + "`"
                    + "\n `Time: " + str[1] + "`"
            ).build();

            App.globalChannel.sendMessage("Hey, " + App.tagID + "! " + "Your camera caught someone at your door"
                    + "\n `Date: " + str[0] + "`"
                    + "\n `Time: " + str[1] + "`").addFile(image).complete();
            System.out.println("Message sent");

            dateAndTime[0] = str[0];
            dateAndTime[1] = str[1];

        } else
            System.out.println("null channel, waiting for bind");

//        Message message = Message.creator (
//                new PhoneNumber(Tokens.MY_PHONE_NUMBER),
//                new PhoneNumber(Tokens.TWILIO_PHONE_NUMBER),
//                str
//        ).create();
//
//        System.out.println(message.getSid());
    }

    public void sendTextMsg(String msg, String url) {

        Message message = Message.creator (
                new PhoneNumber(Tokens.MY_PHONE_NUMBER),
                new PhoneNumber(Tokens.TWILIO_PHONE_NUMBER),
                msg + "\n" + url
        ).create();

        System.out.println(message.getSid());
    }

}
