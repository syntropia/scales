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

import java.util.UUID;

public abstract class EntityId implements UUIdentifiable {

    private final UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    protected EntityId(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("An Indicator UUID cannot be null");
        }
        this.uuid = uuid;
    }

    @Override
    final public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        EntityId entityId = (EntityId) o;
        return this.uuid.equals(entityId.uuid);
    }

    @Override
    final public int hashCode() {
        return this.uuid.hashCode();
    }
}
