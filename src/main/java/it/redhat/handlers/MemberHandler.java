package it.redhat.handlers;

import it.redhat.services.ValidatorService;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;


/**
 * Created by samuele on 9/8/14.
 */
public class MemberHandler extends AbstractResultsHandler implements Handler<Buffer> {

    private final HttpServerRequest request;
    Container container;

    public MemberHandler(Vertx vertx, Container container, HttpServerRequest httpServerRequest) {
        super(vertx);
        this.container = container;
        this.request = httpServerRequest;

    }

    @Override
    public void handle(Buffer buffer) {
        final String body = buffer.getString(0, buffer.length());
        JsonObject payload = new JsonObject(body);

        String name = payload.getString("name");
        final String email = payload.getString("email");
        final String number = payload.getString("phoneNumber");

        if(name != null || email != null || number != null) {
            vertx.eventBus().send("email-validator", email, new Handler<Message<String>>() {
                @Override
                public void handle(Message<String> message) {
                    if(Boolean.parseBoolean(message.body()) != false) {
                        vertx.eventBus().send("telnumber-validator", number, new Handler<Message<String>>() {
                            @Override
                            public void handle(Message<String> message) {
                                if(Boolean.parseBoolean(message.body()) != false) {
                                    container.logger().info("VALIDATION SUCCESS");
                                    JsonObject query = new JsonObject()
                                            .putString("action", "insert")
                                            .putString("table", "Member")
                                            .putString("fields", "email,name,phone_number");
                                    sendPositiveReaponse();
                                } else {
                                    sendNegativeReaponse("telephone number invalid");
                                }

                            }
                        });
                    } else {
                        sendNegativeReaponse("Email invalid or already present");
                    }
                }});
        }

        container.logger().info(payload.encodePrettily());
    }

    private void sendPositiveReaponse() {
        request.response().putHeader("Access-Control-Allow-Origin", "http://localhost:8180");
        request.response().putHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
        request.response().putHeader("Access-Control-Allow-Headers","Content-Type, X-Requested-With");
        request.response().setStatusCode(200);
        request.response().end();
    }

    private void sendNegativeReaponse(String message) {
        request.response().putHeader("Access-Control-Allow-Origin", "http://localhost:8180");
        request.response().putHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
        request.response().putHeader("Access-Control-Allow-Headers","Content-Type, X-Requested-With");
        request.response().setStatusCode(400);
        request.response().setStatusMessage(message);
        request.response().end(message);
    }


}
