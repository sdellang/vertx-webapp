package it.redhat.handlers;


import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by samuele on 9/5/14.
 */
public abstract class AbstractResultsHandler {

    protected Vertx vertx;

    public AbstractResultsHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    protected JsonArray composeResponse(JsonArray fields, JsonArray data) {
        JsonArray responseArray = new JsonArray();
        for(Object el : data) {
            JsonArray singleRes = (JsonArray)el;
            JsonObject responseObj = new JsonObject();
            for(int i = 0 ; i < fields.size() ; i++) {
                responseObj.putValue(fields.get(i).toString(),singleRes.get(i));
            }
            responseArray.add(responseObj);

        }

        return responseArray;
    }

    protected void executeQuerySendResponse(JsonObject query, final HttpServerRequest httpServerRequest) {
        vertx.eventBus().send("mysql-persistor", query, new Handler<Message<JsonObject>>() {

            @Override
            public void handle(Message<JsonObject> message) {

                httpServerRequest.response().putHeader("Content-Type", "application/json");
                httpServerRequest.response().putHeader("Access-Control-Allow-Origin","http://localhost:8180");
                httpServerRequest.response().putHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
                httpServerRequest.response().putHeader("Access-Control-Allow-Headers","Content-Type, X-Requested-With");
                httpServerRequest.response().end(composeResponse(message.body().getArray("fields"),message.body().getArray("results")).encode());
            }
        });
    }

}
