package studio.lysid.scales.query.scale;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import studio.lysid.scales.deploy.EventBusService;

@ProxyGen
public interface QueryScaleService extends EventBusService {

    static String eventBusAddress() { return "query-scale-service"; }

    void findScaleById(String id, Handler<AsyncResult<String>> handler);

}
