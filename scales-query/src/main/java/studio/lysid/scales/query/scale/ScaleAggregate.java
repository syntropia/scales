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

import studio.lysid.scales.query.indicator.IndicatorId;

public class ScaleAggregate {

    private final ScaleId id;
    public ScaleId getId() { return this.id; }


    private Integer version;
    public Integer getVersion() {
        return this.version;
    }

    private ScaleStatus status;
    public ScaleStatus getStatus() { return this.status; }


    private ScaleId evolvedInto;
    public ScaleId getEvolvedInto() {
        return this.evolvedInto;
    }



    public ScaleAggregate(ScaleId id) {
        this.id = id;
        this.version = 0;
        this.status = ScaleStatus.Draft;
    }



    public void publish() {
        this.status = ScaleStatus.Published;
        this.version++;
    }


    public void archive() {
        this.status = ScaleStatus.Archived;
        this.version++;
    }

    public void unarchive() {
        this.status = ScaleStatus.Published;
        this.version++;
    }

    public void evolveInto(ScaleId newScale) {
        this.status = ScaleStatus.Evolved;
        this.version++;
        this.evolvedInto = newScale;
    }

    public void attachIndicator(IndicatorId id) {
        // TODO
    }
}
