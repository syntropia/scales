/*
 * Copyright (C) 2017 Frederic Monjo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package studio.lysid.scales.query.scale;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

public class QueryScaleServiceImpl implements QueryScaleService {

    private static final Logger logger = LoggerFactory.getLogger(QueryScaleServiceImpl.class);
    private static final String ScaleCollection = "scale";

    private final MongoClient db;

    public QueryScaleServiceImpl(MongoClient db) {
        super();
        this.db = db;
    }

    @Override
    public void findScaleById(int id, Handler<AsyncResult<JsonObject>> handler) {
        logger.debug("findScaleById called with parameters: id={0}", id);

        db.findOne(ScaleCollection, new JsonObject().put("_id", Integer.toString(id)), new JsonObject(),
            res -> {
                if (res.succeeded() && res.result() != null) {
                    handler.handle(Future.succeededFuture(res.result()));
                } else {
                    handler.handle(Future.failedFuture("Scale #" + id + " does not exist"));
                }
            });
    }
}
