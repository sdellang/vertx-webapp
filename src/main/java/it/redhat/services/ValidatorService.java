package it.redhat.services;

import it.redhat.services.handlers.TelValidatoriHandler;
import it.redhat.services.handlers.ValidatorHandler;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Container;

/**
 * Created by samuele on 9/9/14.
 */
public class ValidatorService extends BusModBase {

    private boolean isEmailValid = false;

    public ValidatorService() {
        super();
    }

    public void start() {
        super.start();
        vertx.eventBus().registerHandler("email-validator",new ValidatorHandler(vertx, container));
        vertx.eventBus().registerHandler("telnumber-validator",new TelValidatoriHandler(vertx,container));
        vertx.eventBus().registerHandler("ping-address", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> message) {
                message.reply("pong!");
                container.logger().info("Sent back pong");
            }
        });
        container.logger().info("Validator Service started");
    }

}
