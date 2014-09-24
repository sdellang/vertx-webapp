package it.redhat.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Container;


/**
 * Created by samuele on 9/5/14.
 */
public class RegisterMemberHandlerPost extends AbstractResultsHandler implements Handler<HttpServerRequest> {

    private Container container;

    public RegisterMemberHandlerPost(Vertx vertx, Container container) {
        super(vertx);
        this.container = container;
    }

    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        httpServerRequest.bodyHandler(new MemberHandler(vertx,container));
        httpServerRequest.response().putHeader("Access-Control-Allow-Origin", "http://localhost:8180");
        httpServerRequest.response().putHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
        httpServerRequest.response().putHeader("Access-Control-Allow-Headers","Content-Type, X-Requested-With");
        httpServerRequest.response().setStatusCode(200);
        httpServerRequest.response().end();
    }
}
