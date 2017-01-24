package studio.lysid.scales.query.scale;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import studio.lysid.scales.deploy.EventBusService;

@ProxyGen
public interface QueryScaleService extends EventBusService {

    void findScaleById(int id, Handler<AsyncResult<JsonObject>> handler);

}
