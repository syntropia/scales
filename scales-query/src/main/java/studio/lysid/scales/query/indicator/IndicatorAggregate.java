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

package studio.lysid.scales.query.indicator;

import studio.lysid.scales.query.scale.ScaleAggregate;
import studio.lysid.scales.query.scale.ScaleStatus;

import java.util.List;

public class IndicatorAggregate {

    private IndicatorId id;
    public IndicatorId getId() {
        return this.id;
    }

    private Integer version;
    public Integer getVersion() {
        return this.version;
    }

    private IndicatorStatus status;
    public IndicatorStatus getStatus() {
        return this.status;
    }

    private IndicatorId evolvedInto;
    public IndicatorId getEvolvedInto() {
        return this.evolvedInto;
    }



    public IndicatorAggregate(IndicatorId id) {
        this.id = id;
        this.version = 0;
        this.status = IndicatorStatus.Draft;
        this.evolvedInto = null;
    }

    public void publish() {
        if (this.status != IndicatorStatus.Draft) {
            throw new IllegalStateException("An indicator can be published only when it has a Draft status.");
        }
        this.status = IndicatorStatus.Published;
        this.version++;
    }

    public void archive(List<ScaleAggregate> scalesUsingThisIndicator) {
        if (scalesUsingThisIndicator != null
                && scalesUsingThisIndicator.stream().anyMatch(
                    scaleAggregate -> scaleAggregate.getStatus() != ScaleStatus.Archived)) {
            throw new IllegalStateException("An indicator can be archived only if no Scale is using it or if all Scales using it are also archived.");
        }
        if (this.status == IndicatorStatus.Archived) {
            throw new IllegalStateException("An indicator can be archived only when it has a Draft, Published or Evolved status.");
        }

        this.status = IndicatorStatus.Archived;
        this.version++;
    }

    public void unarchive() {
        if (this.status != IndicatorStatus.Archived) {
            throw new IllegalStateException("An indicator can be unarchived only when it has an Archived status.");
        }
        this.status = IndicatorStatus.Published;
        this.version++;
    }

    public void evolveInto(IndicatorId newIndicator) {
        if (newIndicator == null) {
            throw new IllegalArgumentException("newIndicator must not be null");
        }
        this.status = IndicatorStatus.Evolved;
        this.version++;
        this.evolvedInto = newIndicator;
    }
}
