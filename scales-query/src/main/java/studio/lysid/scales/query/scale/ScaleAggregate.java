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

import java.util.ArrayList;
import java.util.List;

public class ScaleAggregate {

    private final ScaleId id;

    public ScaleId getId() { return this.id; }


    private Integer version;
    public Integer getVersion() {
        return this.version;
    }

    private ScaleStatus status;
    private ScaleStatus statusBeforeArchiving;
    public ScaleStatus getStatus() { return this.status; }

    private ScaleId evolvedInto;
    public ScaleId getEvolvedInto() {
        return this.evolvedInto;
    }

    private List<IndicatorId> attachedIndicators;


    public ScaleAggregate(ScaleId id) {
        this.id = id;
        this.version = 0;
        this.status = ScaleStatus.Draft;
    }



    public void publish() {
        if (this.status != ScaleStatus.Draft) {
            throw new IllegalStateException("A Scale can be published only when it has a Draft status.");
        }
        this.status = ScaleStatus.Published;
        this.version++;
    }


    public void archive() {
        if (this.status == ScaleStatus.Archived) {
            throw new IllegalStateException("A Scale can be archived only when it has a Draft, Published or Evolved status.");
        }
        this.statusBeforeArchiving = this.status;
        this.status = ScaleStatus.Archived;
        this.version++;
    }

    public void unarchive() {
        if (this.status != ScaleStatus.Archived) {
            throw new IllegalStateException("A Scale can be unarchived only when it has an Archived status.");
        }
        this.status = this.statusBeforeArchiving;
        this.version++;
    }

    public void evolveInto(ScaleId newScale) {
        this.status = ScaleStatus.Evolved;
        this.version++;
        this.evolvedInto = newScale;
    }

    public void attachIndicator(IndicatorId indicator) {
        if (this.status != ScaleStatus.Draft) {
            throw new IllegalStateException("A Scale can be edited only when it has a Draft status.");
        }
        if (this.attachedIndicators == null) {
            this.attachedIndicators = new ArrayList<>();
        }
        this.attachedIndicators.add(indicator);
    }

    public List<IndicatorId> getAttachedIndicators() {
        return attachedIndicators;
    }
}
