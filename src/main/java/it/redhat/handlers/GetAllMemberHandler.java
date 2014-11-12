package it.redhat.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

/**
 * Created by samuele on 9/4/14.
 */
public class GetAllMemberHandler extends ResultsHandler implements Handler<HttpServerRequest> {

    public GetAllMemberHandler(Vertx vertx, Container container) {
        super(vertx, container);
    }

    @Override
    public void handle(final HttpServerRequest httpServerRequest) {
        JsonObject select = new JsonObject()
                .putString("action","select")
                .putString("table","Member");

        executeQuerySendResponse(select, httpServerRequest);
    }

}
