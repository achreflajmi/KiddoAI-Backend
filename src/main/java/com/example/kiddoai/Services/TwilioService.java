package com.example.kiddoai.Services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TwilioService {

    Dotenv dotenv = Dotenv.load();
    String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");

    String TWILIO_NUMBER = "+12184605358";




    @PostConstruct
    public void initTwilio() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendSms(String to, String body) {
        System.out.println("[TwilioService] Sending SMS to: " + to);
        System.out.println("[TwilioService] Message body: " + body);

        // Create the message
        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(TWILIO_NUMBER),
                body
        ).create();

        // Log the message SID and status
        System.out.println("[TwilioService] Message SID = " + message.getSid());
        System.out.println("[TwilioService] Message status = " + message.getStatus());
    }
}