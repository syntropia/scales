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
import java.util.Collections;
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
        ensureDraftStatus();
        if (this.attachedIndicators == null) {
            this.attachedIndicators = new ArrayList<>();
        } else if (this.attachedIndicators.contains(indicator)) {
            throw new IllegalStateException("The indicator '" + indicator.getUuid() + "' is already attached to the Scale. An Indicator can be used only once in a Scale.");
        }
        this.attachedIndicators.add(indicator);
    }

    public void setIndicatorsOrder(List<IndicatorId> reorderedIndicators) {
        ensureDraftStatus();
        ensureIndicatorsAttached();
        int reorderedIndicatorsSize = reorderedIndicators.size();
        for (int i = 0; i < reorderedIndicatorsSize; i++) {
            IndicatorId reorderedIndicator = reorderedIndicators.get(i);
            int indicatorPreviousPosition = this.attachedIndicators.indexOf(reorderedIndicator);
            if (indicatorPreviousPosition == -1) {
                throw new IllegalArgumentException("The Indicator '" + reorderedIndicator.getUuid() + "' was not previously attached to this scale");
            }
            Collections.swap(this.attachedIndicators, indicatorPreviousPosition, i);
        }
    }

    public void detachIndicator(IndicatorId indicator) {
        ensureDraftStatus();
        ensureIndicatorsAttached();
        if (!this.attachedIndicators.contains(indicator)) {
            throw new IllegalArgumentException("The Indicator '" + indicator.getUuid() + "' was not previously attached to this scale");
        }
        this.attachedIndicators.remove(indicator);
    }

    private void ensureDraftStatus() {
        if (this.status != ScaleStatus.Draft) {
            throw new IllegalStateException("A Scale can be edited only when it has a Draft status.");
        }
    }

    private void ensureIndicatorsAttached() {
        if (this.attachedIndicators == null) {
            throw new IllegalStateException("No Indicator has been added to this scale yet");
        }
    }

    public int getIndicatorCount() {
        return (this.attachedIndicators != null ? this.attachedIndicators.size() : 0);
    }

    public IndicatorId getIndicatorAtPosition(int position) {
        ensureIndicatorsAttached();
        return this.attachedIndicators.get(position);
    }


}

