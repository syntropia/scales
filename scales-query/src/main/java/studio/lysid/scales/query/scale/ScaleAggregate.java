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

import java.util.List;

public class ScaleAggregate {

    private final ScaleId id;

    public ScaleId getId() { return this.id; }

    private ScaleStatus status;
    private ScaleStatus statusBeforeArchiving;
    public ScaleStatus getStatus() { return this.status; }

    private ScaleId evolvedInto;
    public ScaleId getEvolvedInto() {
        return this.evolvedInto;
    }

    private IndicatorGroup rootGroup;

    public ScaleAggregate(ScaleId id) {
        this.id = id;
        this.status = ScaleStatus.Draft;
        this.rootGroup = new IndicatorGroup("__SCALE_ROOT_GROUP__", true);
    }



    // Scale behaviors

    public void publish() {
        if (this.status != ScaleStatus.Draft) {
            throw new IllegalStateException("A Scale can be published only when it has a Draft status.");
        }
        this.status = ScaleStatus.Published;
    }


    public void archive() {
        if (this.status == ScaleStatus.Archived) {
            throw new IllegalStateException("A Scale can be archived only when it has a Draft, Published or Evolved status.");
        }
        this.statusBeforeArchiving = this.status;
        this.status = ScaleStatus.Archived;
    }

    public void unarchive() {
        if (this.status != ScaleStatus.Archived) {
            throw new IllegalStateException("A Scale can be unarchived only when it has an Archived status.");
        }
        this.status = this.statusBeforeArchiving;
    }

    public void evolveInto(ScaleId newScale) {
        this.status = ScaleStatus.Evolved;
        this.evolvedInto = newScale;
    }



    // Indicator management behaviors

    public void attachIndicator(IndicatorId indicator) {
        ensureDraftStatus();
        this.rootGroup.attachIndicator(indicator);
    }

    public int getIndicatorCount() {
        return this.rootGroup.getIndicatorCount();
    }

    public IndicatorId getIndicatorAtPosition(int i) throws IllegalAccessException {
        ScaleElement elementAtPosition = this.rootGroup.getElementAtPosition(i);
        if (elementAtPosition instanceof IndicatorElement) {
            return ((IndicatorElement) elementAtPosition).getIndicator();
        } else {
            throw new IllegalAccessException("Element at position " + i + " is not an Indicator");
        }
    }

    public void reorderIndicators(List<IndicatorId> reorderedIndicators) {
        ensureDraftStatus();
        this.rootGroup.reorderIndicators(reorderedIndicators);
    }

    public void detachIndicator(IndicatorId indicator) {
        ensureDraftStatus();
        this.rootGroup.detachIndicator(indicator);
    }



    // Indicator Group management behaviors

    public void attachGroup(IndicatorGroup group) {
        ensureDraftStatus();
        this.rootGroup.attachGroup(group);
    }



    // Guards

    private void ensureDraftStatus() {
        if (this.status != ScaleStatus.Draft) {
            throw new IllegalStateException("A Scale can be edited only when it has a Draft status.");
        }
    }
}

