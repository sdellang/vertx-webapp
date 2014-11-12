package it.redhat.handlers;


import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

/**
 * Created by samuele on 9/5/14.
 */
public class ResultsHandler {

    protected final Vertx vertx;
    protected final Container container;

    public ResultsHandler(Vertx vertx, Container container) {
        this.container = container;
        this.vertx = vertx;
    }

    private JsonArray composeResponse(JsonArray fields, JsonArray data) {
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

                HttpServerResponse response = setHeaders(httpServerRequest);
                response.end(composeResponse(message.body().getArray("fields"), message.body().getArray("results")).encode());
            }
        });
    }

    protected void sendPositiveResponse(HttpServerRequest httpServerRequest) {

        HttpServerResponse response = setHeaders(httpServerRequest);
        response.setStatusCode(200);
        response.end();
    }

    protected void sendNegativeResponse(HttpServerRequest httpServerRequest, String message) {

        HttpServerResponse response = setHeaders(httpServerRequest);
        response.setStatusCode(400);
        response.end(message);
    }

    private HttpServerResponse setHeaders(HttpServerRequest httpServerRequest) {
        HttpServerResponse response = httpServerRequest.response();
        response.putHeader("Access-Control-Allow-Origin", "http://localhost:8180");
        response.putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.putHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With");
        return response;

    }

}
