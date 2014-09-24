package it.redhat.services.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

import java.util.regex.Pattern;

/**
 * Created by samuele on 9/16/14.
 */
public class ValidatorHandler implements Handler<Message<String>> {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    private Vertx vertx;
    private Container container;
   boolean isEmailValid = false;

    public ValidatorHandler(Vertx vertx, Container container) {
        this.vertx = vertx;
        this.container = container;
    }

    @Override
    public void handle(final Message<String> messagePrincipal) {
        String email = messagePrincipal.body();

        Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
        isEmailValid = emailPattern.matcher(email).matches();

        JsonObject select = new JsonObject()
                .putString("action","prepared")
                .putString("statement","SELECT * FROM Member WHERE email = ?")
                .putArray("values" , new JsonArray().add(email));

        vertx.eventBus().send("mysql-persistor", select, new Handler<Message<JsonObject>>() {

            @Override
            public void handle(Message<JsonObject> message) {
                if(message.body().getArray("results").size() != 0) {
                    container.logger().info("CAZZ!!!");
                    isEmailValid = false;
                    messagePrincipal.reply(String.valueOf(isEmailValid));
                }
            }
        });
    }

}
