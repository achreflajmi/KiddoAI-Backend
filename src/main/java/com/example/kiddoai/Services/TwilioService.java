package com.example.kiddoai.Services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TwilioService {

    @Value("${TWILIO_ACCOUNT_SID}")
    private String accountSid;

    @Value("${TWILIO_AUTH_TOKEN}")
    private String authToken;

    @Value("${TWILIO_NUMBER}")
    private String twilioNumber;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public void sendSms(String to, String body) {
        System.out.println("[TwilioService] Sending SMS to: " + to);
        System.out.println("[TwilioService] Message body: " + body);

        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(twilioNumber),
                body
        ).create();

        System.out.println("[TwilioService] Message SID = " + message.getSid());
        System.out.println("[TwilioService] Message status = " + message.getStatus());
    }
}
