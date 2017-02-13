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

package studio.lysid.scales.deploy;

import io.vertx.core.DeploymentOptions;
import studio.lysid.scales.facade.FacadeVerticle;
import studio.lysid.scales.query.QueryVerticle;

public enum Verticle {

    EventStore("eventstore", null/*TODO*/),
    Command("command", null/*TODO*/),
    Query("query", QueryVerticle.class),
    Facade("facade", FacadeVerticle.class);



    public final String shortName;
    public final Class<? extends io.vertx.core.Verticle> implementation;
    public final DeploymentOptions deploymentOptions;

    Verticle(String shortName, Class<? extends io.vertx.core.Verticle> implementation) {
        this.implementation = implementation;
        this.shortName = shortName;
        this.deploymentOptions = new DeploymentOptions();
    }
}
