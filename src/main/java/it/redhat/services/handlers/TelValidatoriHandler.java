package it.redhat.services.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Container;

import java.util.regex.Pattern;

/**
 * Created by samuele on 9/19/14.
 */
public class TelValidatoriHandler implements Handler<Message<String>> {

    private static final String TELEPHONE_PATTERN = "^[0-9]{10,12}$";

    private final Vertx vertx;
    private final Container container;

    public TelValidatoriHandler(Vertx vertx, Container container) {
        this.vertx = vertx;
        this.container = container;
    }

    @Override
    public void handle(Message<String> stringMessage) {
        String number = stringMessage.body();
        Pattern numberPattern = Pattern.compile(TELEPHONE_PATTERN);
        stringMessage.reply(String.valueOf(numberPattern.matcher(number).matches()));
    }
}
