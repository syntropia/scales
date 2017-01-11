package studio.lysid.scales.query.scale;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
public interface QueryScaleService {

    void findScaleById(String id, Handler<AsyncResult<String>> handler);

}
