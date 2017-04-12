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
import java.util.Iterator;
import java.util.List;

public class IndicatorGroupVO extends ScaleElement {

    private String name;
    public String getName() { return this.name; }

    private boolean isEditable;
    void setEditable(boolean editable) {
        this.isEditable = editable;
        this.attachedElements.stream()
                .filter(element -> element.getClass().equals(IndicatorGroupVO.class))
                .forEach(indicatorGroupVO -> ((IndicatorGroupVO) indicatorGroupVO).isEditable = editable);
    }


    private List<ScaleElement> attachedElements;

    public IndicatorGroupVO(String name, boolean isEditable) {
        this.name = name;
        this.isEditable = isEditable;
        this.attachedElements = new ArrayList<>();
    }



    // Indicator methods

    public void attachIndicator(IndicatorId indicator) {
        checkIsEditable();
        checkDoesNotContain(indicator);
        this.attachedElements.add(new IndicatorElement(indicator));
    }

    private void checkDoesNotContain(IndicatorId indicator) {
        if (this.contains(indicator)) {
            throw new IllegalStateException("The Indicator '" + indicator.getUuid() + "' is already attached to the Scale. An Indicator can be used only once in a Scale.");
        }
    }

    boolean contains(IndicatorId indicator) {
        boolean found = false;
        int i = 0;
        int elementCount = this.attachedElements.size();

        while (!found && i < elementCount) {
            ScaleElement attachedElement = this.attachedElements.get(i);
            if (attachedElement instanceof IndicatorElement) {
                found = (((IndicatorElement) attachedElement).getIndicator().equals(indicator));
            } else if (attachedElement instanceof IndicatorGroupVO) {
                found = ((IndicatorGroupVO) attachedElement).contains(indicator);
            } else {
                throw new UnsupportedOperationException("Attached ScaleElement is of unsupported type: " + attachedElement.getClass().getCanonicalName());
            }
            i++;
        }

        return found;
    }

    int getIndicatorCount() {
        int count = 0;
        for (ScaleElement attachedElement : this.attachedElements) {
            if (attachedElement instanceof IndicatorElement) {
                count++;
            } else if (attachedElement instanceof IndicatorGroupVO) {
                count += ((IndicatorGroupVO) attachedElement).getIndicatorCount();
            } else {
                throw new UnsupportedOperationException("Attached ScaleElement is of unsupported type: " + attachedElement.getClass().getCanonicalName());
            }
        }
        return count;
    }

    public boolean detachIndicator(IndicatorId indicator) {
        checkIsEditable();

        boolean indicatorFoundAndRemoved = false;

        for (Iterator<ScaleElement> iterator = attachedElements.iterator(); iterator.hasNext(); ) {
            ScaleElement element =  iterator.next();
            if (element instanceof IndicatorElement) {
                if (((IndicatorElement) element).getIndicator().equals(indicator)) {
                    iterator.remove();
                    indicatorFoundAndRemoved = true;
                    break;
                }
            } else if (element instanceof IndicatorGroupVO) {
                indicatorFoundAndRemoved = ((IndicatorGroupVO) element).detachIndicator(indicator);
            }
        }

        return indicatorFoundAndRemoved;
    }



    // Group methods

    public void attachGroup(IndicatorGroupVO group) {
        checkIsEditable();
        checkDoesNotContain(group);
        this.attachedElements.add(group);
    }


    private void checkDoesNotContain(IndicatorGroupVO group) {
        if (this.contains(group)) {
            throw new IllegalStateException("The Indicator Group '" + group.getName() + "' is already attached to the Scale. An Indicator Group can be used only once in a Scale.");
        }
    }

    boolean contains(IndicatorGroupVO group) {
        boolean found = false;
        int i = 0;
        int elementCount = this.attachedElements.size();

        while (!found && i < elementCount) {
            ScaleElement attachedElement = this.attachedElements.get(i);
            if (attachedElement instanceof IndicatorElement) {
                found = false;
            } else if (attachedElement instanceof IndicatorGroupVO) {
                found = attachedElement.equals(group) || ((IndicatorGroupVO) attachedElement).contains(group);
            } else {
                throw new UnsupportedOperationException("Attached ScaleElement is of unsupported type: " + attachedElement.getClass().getCanonicalName());
            }
            i++;
        }

        return found;
    }



    // Common helpers

    private void checkIsEditable() {
        if (!this.isEditable) {
            throw new IllegalStateException("A Scale can be edited only when it has a Draft status.");
        }
    }

    ScaleElement getElementAtPosition(int i) {
        return this.attachedElements.get(i);
    }
}
