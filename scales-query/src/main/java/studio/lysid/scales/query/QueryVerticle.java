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

package studio.lysid.scales.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import studio.lysid.scales.deploy.service.EventBusServiceHelper;
import studio.lysid.scales.deploy.service.Service;
import studio.lysid.scales.query.scale.QueryScaleService;
import studio.lysid.scales.query.scale.QueryScaleServiceImpl;

public class QueryVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(QueryVerticle.class);

    private MongoClient db;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        JsonObject dbConfig = new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name","scales-query");
        this.db = MongoClient.createShared(this.vertx, dbConfig);

        new EventBusServiceHelper(this.vertx, logger)
                .publish(QueryScaleService.class, new QueryScaleServiceImpl(this.db), Service.QueryScale.address);


        startFuture.complete();
    }

}
