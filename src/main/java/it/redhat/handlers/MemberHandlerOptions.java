package it.redhat.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Container;


/**
 * Created by samuele on 9/5/14.
 */
public class MemberHandlerOptions extends ResultsHandler implements Handler<HttpServerRequest> {

    public MemberHandlerOptions(Vertx vertx, Container container) {
        super(vertx, container);
    }

    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        sendPositiveResponse(httpServerRequest);
    }
}
