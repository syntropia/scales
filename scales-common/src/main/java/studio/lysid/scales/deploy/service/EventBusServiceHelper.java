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

package studio.lysid.scales.deploy.service;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.serviceproxy.ProxyHelper;

public class EventBusServiceHelper {

    private final Vertx vertx;
    private final Logger logger;

    public EventBusServiceHelper(Vertx vertx, Logger logger) {
        this.vertx = vertx;
        this.logger = logger;
    }

    @Fluent
    public <T extends EventBusService> EventBusServiceHelper publish(Class<T> serviceInterface, T serviceImplementation, String address) {
        this.logger.info("Publishing {0} on EventBus, listening at {1}", serviceInterface.getName(), address);
        ProxyHelper.registerService(serviceInterface, this.vertx, serviceImplementation, address);
        return this;
    }

    public <T extends EventBusService> T getProxy(Class<T> serviceInterface, String address) {
        this.logger.info("Creating EventBus proxy for {0}, calling at {1}", serviceInterface.getName(), address);
        return ProxyHelper.createProxy(serviceInterface, this.vertx, address);
    }

}
