package it.redhat.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by samuele on 9/4/14.
 */
public class GetAllMemberHandler extends AbstractResultsHandler implements Handler<HttpServerRequest> {

    public GetAllMemberHandler(Vertx vertx) {
        super(vertx);
    }

    @Override
    public void handle(final HttpServerRequest httpServerRequest) {
        JsonObject select = new JsonObject()
                .putString("action","select")
                .putString("table","Member");

        executeQuerySendResponse(select, httpServerRequest);
    }

}
