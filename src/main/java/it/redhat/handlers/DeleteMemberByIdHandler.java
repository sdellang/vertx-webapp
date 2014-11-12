package it.redhat.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

/**
 * Created by samuele on 10/2/14.
 */
public class DeleteMemberByIdHandler extends ResultsHandler implements Handler<HttpServerRequest> {

    public DeleteMemberByIdHandler(Vertx vertx, Container container) {
        super(vertx,container);
    }

    @Override
    public void handle(final HttpServerRequest httpServerRequest) {
        final String id = httpServerRequest.params().get("id");

        if(id != null) {

            JsonObject delete = new JsonObject()
                    .putString("action", "prepared")
                    .putString("statement", "DELETE FROM Member WHERE id = ?")
                    .putArray("values" , new JsonArray().add(Integer.parseInt(id)));

            vertx.eventBus().send("mysql-persistor", delete, new Handler<Message<JsonObject>>() {

                @Override
                public void handle(Message<JsonObject> message) {
                    container.logger().info(message.body().encodePrettily());
                    if (message.body().getField("status").equals("ok")) {
                        vertx.eventBus().publish("delmember-address","deleted member id: "+id);
                        sendPositiveResponse(httpServerRequest);
                    } else {
                        sendNegativeResponse(httpServerRequest, "internal error while deleting member");
                    }
                }
            });
        }
    }
}
