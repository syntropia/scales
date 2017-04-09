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

public abstract class AggregateId {

    private final String uuid;

    public String getUuid() {
        return uuid;
    }

    protected AggregateId(String uuid) {
        if (uuid == null || uuid == "") {
            throw new IllegalArgumentException("An Indicator id cannot be null or empty String");
        }
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj || obj instanceof AggregateId && this.uuid.equals(((AggregateId) obj).uuid));
    }
}
