package net.inhostudios.doorcam;

import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.Requester;
import okhttp3.MultipartBody;
import org.apache.http.entity.ContentType;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.function.Consumer;

public class MessageHandler {

    public MessageHandler() {
        Twilio.init(Tokens.TWILIO_ACCT_SID, Tokens.TWILIO_AUTH_TOKEN);

    }

    public void sendMessage(String[] str, BufferedImage image) {
        // sending image
        if (App.globalChannel != null) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Alert!");
            eb.setDescription("Hey, " + App.tagID + "! " + "Your camera caught someone at your door");
            eb.addField("Date", str[0], false);
            eb.addField("Time", str[1], false);
            eb.setColor(Color.red);
            App.globalChannel.sendMessage(eb.build()).complete();
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

}
