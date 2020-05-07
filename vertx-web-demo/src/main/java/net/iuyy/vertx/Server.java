package net.iuyy.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


/**
 * @author iuyy
 * @version v1.0
 * @corporation Copyright by iuyy.net
 * @date 2020/5/2 15:02
 * @description
 */
public class Server extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(Server.class.getName());
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);

        // 处理静态资源，默认路径为 webroot, 这里改成了 static
        StaticHandler staticHandler = StaticHandler.create("static");
        router.get("/static/*").handler(staticHandler);

        ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create(vertx);
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        engine.getThymeleafTemplateEngine().setTemplateResolver(resolver);
        TemplateHandler handler = new IuyyHandler(engine);
        router.get("/*").handler(handler);

        vertx.createHttpServer().requestHandler(router).listen(8080);
    }
}
