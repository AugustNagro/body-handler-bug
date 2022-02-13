package com.augustnagro.bodyhandlerbug;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.loom.core.VertxLoom;

import java.nio.charset.StandardCharsets;

public class Main {

  static final String FUT_VALUE = "hello world";

  public static Future<String> fut() {
    return Future.succeededFuture(FUT_VALUE);
  }

  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx()
        .exceptionHandler(Throwable::printStackTrace);
    VertxLoom vertxLoom = new VertxLoom(vertx);

    vertxLoom.virtual(() -> {

      Router router = Router.router(vertx);
      router.route().handler(BodyHandler.create());
      router.get("/test").handler(ctx -> {
        Buffer body = ctx.getBody();
        System.out.println("body: " + body);
        ctx.end(FUT_VALUE);
      });
      HttpServer server = vertx.createHttpServer();
      server.requestHandler(router);
      vertxLoom.await(server.listen(8088, "localhost"));

      HttpClient client = vertx.createHttpClient();
      System.out.println("If 100 lines are printed the test passed:");
      for (int i = 0; i < 100; ++i) {
        System.out.println("Attempt #" + i);
        HttpClientRequest req = vertxLoom.await(client.request(
            HttpMethod.GET,
            8088,
            "localhost",
            "/test"
        ));
        HttpClientResponse resp = vertxLoom.await(req.send("hello world"));
        Buffer body = vertxLoom.await(resp.body());
        String bodyString = body.toString(StandardCharsets.UTF_8);
        if (!FUT_VALUE.equals(bodyString)) throw new RuntimeException("Failed");
      }
      System.out.println("It worked!");
    });


  }
}
