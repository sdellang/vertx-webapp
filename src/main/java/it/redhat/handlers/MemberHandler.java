package it.redhat.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;



/**
 * Created by samuele on 9/8/14.
 */
public class MemberHandler extends ResultsHandler implements Handler<Buffer> {

    private final HttpServerRequest request;

    public MemberHandler(Vertx vertx, Container container, HttpServerRequest httpServerRequest) {
        super(vertx, container);
        this.request = httpServerRequest;

    }

    @Override
    public void handle(Buffer buffer) {
        final String body = buffer.getString(0, buffer.length());
        JsonObject payload = new JsonObject(body);

        final String name = payload.getString("name");
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
                                            .putArray("fields", new JsonArray().add("email").add("name").add("phone_number"))
                                            .putArray("values", new JsonArray().addArray(new JsonArray().add(email).add(name).add(number)));
                                    vertx.eventBus().send("mysql-persistor", query, new Handler<Message<JsonObject>>() {

                                        @Override
                                        public void handle(Message<JsonObject> message) {
                                            container.logger().info(message.body().encodePrettily());
                                            if(message.body().getField("status").equals("ok")) {
                                                vertx.eventBus().publish("newmember-address","New member registered: "+name);
                                                sendPositiveResponse(request);
                                            } else {
                                                sendNegativeResponse(request, "internal error while registering member");
                                            }
                                        }
                                    });

                                } else {
                                    sendNegativeResponse(request, "telephone number invalid");
                                }

                            }
                        });
                    } else {
                        sendNegativeResponse(request, "Email invalid or already present");
                    }
                }});
        }

        container.logger().info(payload.encodePrettily());
    }


}
