package it.redhat.handlers;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by samuele on 9/5/14.
 */
public class GetMemberByIdHandler extends AbstractResultsHandler implements Handler<HttpServerRequest> {

    public GetMemberByIdHandler(Vertx vertx) {
       super(vertx);
    }

    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        String id = httpServerRequest.params().get("id");

        if(id != null) {
            JsonObject select = new JsonObject()
                    .putString("action","prepared")
                    .putString("statement","SELECT * FROM Member WHERE id = ?")
                    .putArray("values" , new JsonArray().add(Integer.parseInt(id)));

            executeQuerySendResponse(select,httpServerRequest);
        }

    }
}
