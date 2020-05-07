package net.iuyy.vertx;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.LanguageHeader;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.common.template.TemplateEngine;
import io.vertx.ext.web.handler.TemplateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.exceptions.TemplateInputException;

import java.util.Iterator;
import java.util.Locale;

/**
 * @author iuyy
 * @version v1.0
 * @corporation Copyright by iuyy.net
 * @date 2020/5/5 16:36
 * @description 将TemplateHandlerImpl复制了一遍，
 * 然后修改了 this.engine.render
 * 将 templateDirectory + file 换成了 context.normalisedPath()
 * 不然：
 *      如果用 TemplateHandler handler = TemplateHandler.create(engine);
 *      无论什么请求每次都是去找templates下我为templates.html的模板
 *      如果用 TemplateHandler handler = TemplateHandler.create(engine, path, "text/html");
 *      每次都要定义一个，太麻烦
 */
public class IuyyHandler implements TemplateHandler {

    private static final Logger log = LoggerFactory.getLogger(IuyyHandler.class);

    private final TemplateEngine engine;
    private final String templateDirectory;
    private final String contentType;
    private String indexTemplate;

    public IuyyHandler(TemplateEngine engine) {
        this.engine = engine;
        this.templateDirectory = "src/main/resources/templates";
        this.contentType = "text/html";
        this.indexTemplate = "index";
    }

    public IuyyHandler(TemplateEngine engine, String templateDirectory, String contentType) {
        this.engine = engine;
        this.templateDirectory = templateDirectory != null && !templateDirectory.isEmpty() ? templateDirectory : ".";
        this.contentType = contentType;
        this.indexTemplate = "index";
    }

    public void handle(RoutingContext context) {
        if (!context.data().containsKey("lang")) {
            Iterator var3 = context.acceptableLanguages().iterator();

            while(var3.hasNext()) {
                LanguageHeader acceptableLocale = (LanguageHeader)var3.next();

                try {
                    Locale.forLanguageTag(acceptableLocale.value());
                } catch (RuntimeException var6) {
                    continue;
                }

                context.data().put("lang", acceptableLocale.value());
                break;
            }
        }

        String templatePath = context.normalisedPath().equals("/") ? indexTemplate : context.normalisedPath();

        this.engine.render(new JsonObject(context.data()), templatePath, (res) -> {
            if (res.succeeded()) {
                context.response().putHeader(HttpHeaders.CONTENT_TYPE, this.contentType).end(res.result());
            } else {
                log.warn(res.cause().getClass().getSimpleName());
                log.warn(res.cause().toString());
                switch (res.cause().getClass().getSimpleName()){
                    case "TemplateInputException":
                        resourceNotFound(context);
                        break;
                    case "TemplateProcessingException":
                        systemError(context);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void resourceNotFound(RoutingContext context) {
        this.engine.render(new JsonObject(context.data()), "error/404", (result) -> {
            context.response().putHeader(HttpHeaders.CONTENT_TYPE, this.contentType).end(result.result());
        });
    }

    private void systemError(RoutingContext context) {
        this.engine.render(new JsonObject(context.data()), "error/500", (result) -> {
            context.response().putHeader(HttpHeaders.CONTENT_TYPE, this.contentType).end(result.result());
        });
    }

    public TemplateHandler setIndexTemplate(String indexTemplate) {
        this.indexTemplate = indexTemplate;
        return this;
    }
}
