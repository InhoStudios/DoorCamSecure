package net.inhostudios.doorcam;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class MessageHandler {

    public MessageHandler() {
        Twilio.init(Tokens.TWILIO_ACCT_SID, Tokens.TWILIO_AUTH_TOKEN);
    }

    public void sendMessage(String str) {
        Message message = Message.creator (
                new PhoneNumber(Tokens.MY_PHONE_NUMBER),
                new PhoneNumber(Tokens.TWILIO_PHONE_NUMBER),
                str
        ).create();

        System.out.println(message.getSid());
    }

}
