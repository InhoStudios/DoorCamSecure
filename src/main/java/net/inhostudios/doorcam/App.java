package net.inhostudios.doorcam;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.inhostudios.doorcam.image.Camera;
import net.inhostudios.doorcam.image.ImageHandler;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.List;

public class App extends ListenerAdapter {

    public static MessageChannel globalChannel;
    public static String channelID = "668140992184057871";
    public static String tagID = "<@259191629049626634>";
    public static JDA jda;
    public static Camera cm;

    public static void main(String[] args) throws LoginException {
        cm = new Camera();
        cm.start();

        jda = new JDABuilder(AccountType.BOT)
                .setToken(Tokens.JDA_TOKEN)
                .addEventListeners(new App())
                .setActivity(Activity.playing("Keeping my eyes out for creeps"))
                .build();
    }

    @Override
    public void onReady(ReadyEvent e) {
        globalChannel = jda.getTextChannelById(channelID);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
        Message msg = evt.getMessage();
        User auth = evt.getAuthor();
        String content = msg.getContentRaw();
        System.out.println(msg.getContentRaw());

        content = content.replace(tagID, "Andy");
        content = content.replaceAll("`", "");

        if(auth.isBot() && auth.getId().equalsIgnoreCase("667990514058133553")) {
            System.out.println("is from bot");
            Message.Attachment attch = msg.getAttachments().get(0);
            System.out.println(attch.getUrl());

            cm.getMh().sendTextMsg(content, attch.getUrl());
        }
    }

}
