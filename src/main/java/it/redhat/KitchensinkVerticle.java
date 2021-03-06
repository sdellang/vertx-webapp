package it.redhat;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import it.redhat.handlers.*;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.platform.Verticle;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class KitchensinkVerticle extends Verticle {

  public void start() {

    // load the general config object, loaded by using -config on command line
    JsonObject appConfig = container.config();
    container.logger().info(appConfig);

    JsonObject webConfig = new JsonObject()
              .putString("host",appConfig.getString("web-host"))
              .putNumber("port", appConfig.getInteger("web-port"));

    // deploy the mysql-persistor module, which we'll use for persistence
    container.deployModule("io.vertx~mod-mysql-postgresql_2.10~0.3.1", appConfig);
    container.deployModule("io.vertx~mod-web-server~2.0.0-final", webConfig);
    container.deployVerticle("it.redhat.services.ValidatorService");
    RouteMatcher routes = new RouteMatcher();

    routes.get("/rest/members" , new GetAllMemberHandler(vertx, container));
    routes.options("/rest/members", new MemberHandlerOptions(vertx, container));
    routes.options("/rest/members/:id", new MemberHandlerOptions(vertx, container));
    routes.post("/rest/members", new RegisterMemberHandlerPost(vertx, container));
    routes.get("/rest/members/:id" , new GetMemberByIdHandler(vertx, container));
    routes.delete("/rest/members/:id", new DeleteMemberByIdHandler(vertx, container));


    HttpServer httpServer =  vertx.createHttpServer();

    JsonObject sockJsConfig = new JsonObject()
            .putString("prefix", "/eventbus");

    httpServer.requestHandler(routes);

    SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);

    JsonArray inboundPermitted = new JsonArray();
    JsonObject ping = new JsonObject().putString("address", "ping-address");
    inboundPermitted.add(ping);

    JsonArray outboundPermitted = new JsonArray();
    JsonObject newmember = new JsonObject().putString("address","newmember-address");
    JsonObject delmember = new JsonObject().putString("address","delmember-address");
    outboundPermitted.add(newmember).add(delmember);

    sockJSServer.bridge(sockJsConfig, inboundPermitted, outboundPermitted);

    httpServer.listen(8888);

    container.logger().info("Webserver started, listening on port: 8888");



    container.logger().info("KitchensinkVerticle started");

  }
}
