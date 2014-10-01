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
                                                sendPositiveResponse();
                                            } else {
                                                sendNegativeResponse("internal error while registering member");
                                            }
                                        }
                                    });

                                } else {
                                    sendNegativeResponse("telephone number invalid");
                                }

                            }
                        });
                    } else {
                        sendNegativeResponse("Email invalid or already present");
                    }
                }});
        }

        container.logger().info(payload.encodePrettily());
    }

    private void sendPositiveResponse() {
        request.response().putHeader("Access-Control-Allow-Origin", "http://localhost:8180");
        request.response().putHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
        request.response().putHeader("Access-Control-Allow-Headers","Content-Type, X-Requested-With");
        request.response().setStatusCode(200);
        request.response().end();
    }

    private void sendNegativeResponse(String message) {
        request.response().putHeader("Access-Control-Allow-Origin", "http://localhost:8180");
        request.response().putHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
        request.response().putHeader("Access-Control-Allow-Headers","Content-Type, X-Requested-With");
        request.response().setStatusCode(400);
        request.response().setStatusMessage(message);
        request.response().end(message);
    }


}
