package it.redhat.handlers;

import it.redhat.services.ValidatorService;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;


/**
 * Created by samuele on 9/8/14.
 */
public class MemberHandler extends AbstractResultsHandler implements Handler<Buffer> {

    Container container;

    public MemberHandler(Vertx vertx, Container container) {
        super(vertx);
        this.container = container;

    }

    @Override
    public void handle(Buffer buffer) {
        final String body = buffer.getString(0, buffer.length());
        JsonObject payload = new JsonObject(body);
        if(validate(payload)) {
            container.logger().info("VALIDATION SUCCESS");
            JsonObject query = new JsonObject()
                    .putString("action","insert")
                    .putString("table","Member")
                    .putString("fields","email,name,phone_number");

        }
        container.logger().info(payload.encodePrettily());
    }

    private boolean validate(JsonObject payload) {

        String name = payload.getString("name");
        String email = payload.getString("email");
        String number = payload.getString("phoneNumber");

        if(name == null || email == null || number == null) {
            return false;
        }

        //ValidatorService validator = new ValidatorService(vertx, container);
        /*if(!name.isEmpty() && validator.validateEmail(email) && validator.validateTelNumber(number)) {
            return true;
        } */

        return false;
    }


}
