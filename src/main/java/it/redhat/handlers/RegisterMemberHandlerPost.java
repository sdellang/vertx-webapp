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
        httpServerRequest.bodyHandler(new MemberHandler(vertx,container,httpServerRequest));

    }
}
