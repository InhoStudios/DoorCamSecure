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

public class App extends ListenerAdapter {

    public static MessageChannel globalChannel;
    public static String channelID = "567949078424453122";
    public static String tagID = "<@259191629049626634>";
    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        Camera cm = new Camera();
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

}
