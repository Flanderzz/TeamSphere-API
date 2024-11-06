package co.teamsphere.teamsphere.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;

import static com.twilio.rest.api.v2010.account.Message.creator;


@Slf4j
public class SmsUtils {
    public static final String FROM_NUMBER = "+<Twillo number>";
    public static final String SID_KEY = "<Your Twilio API Key>";
    public static final String TOKEN_KEY = "<Your Twilio Token Key>";

    public static void sendSMS(String to, String messageBody) {
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message message = creator(new PhoneNumber("+" + to), new PhoneNumber(FROM_NUMBER), messageBody).create();
        log.info(message.toString());
    }
}