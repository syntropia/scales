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

public class IndicatorGroup extends ScaleElement {

    private String name;
    void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Indicator Group name must not be empty!");
        }
        this.name = name;
    }


    private boolean isEditable;
    private void setEditable(boolean editable) {
        this.isEditable = editable;
        this.attachedElements.stream()
                .filter(element -> element.getClass().equals(IndicatorGroup.class))
                .forEach(indicatorGroupVO -> ((IndicatorGroup) indicatorGroupVO).isEditable = editable);
    }



    private List<ScaleElement> attachedElements;

    IndicatorGroup(String name, boolean isEditable) {
        this.attachedElements = new ArrayList<>();
        this.setName(name);
        this.setEditable(isEditable);
    }



    // Indicator behaviors

    void makeEditable() {
        this.setEditable(true);
    }

    void makeReadOnly() {
        this.setEditable(false);
    }



    void attachIndicator(IndicatorId indicator) {
        checkIsEditable();
        checkDoesNotContain(indicator);
        this.attachedElements.add(new IndicatorElement(indicator));
    }

    private void checkDoesNotContain(IndicatorId indicator) {
        if (this.contains(indicator)) {
            throw new IllegalStateException("The Indicator '" + indicator.getUuid() + "' is already attached to the Scale. An Indicator can be used only once in a Scale.");
        }
    }

    private boolean contains(IndicatorId indicator) {
        boolean found = false;
        int i = 0;
        int elementCount = this.attachedElements.size();

        while (!found && i < elementCount) {
            ScaleElement attachedElement = this.attachedElements.get(i);
            if (attachedElement instanceof IndicatorElement) {
                found = (((IndicatorElement) attachedElement).getIndicator().equals(indicator));
            } else if (attachedElement instanceof IndicatorGroup) {
                found = ((IndicatorGroup) attachedElement).contains(indicator);
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
            } else if (attachedElement instanceof IndicatorGroup) {
                count += ((IndicatorGroup) attachedElement).getIndicatorCount();
            } else {
                throw new UnsupportedOperationException("Attached ScaleElement is of unsupported type: " + attachedElement.getClass().getCanonicalName());
            }
        }
        return count;
    }

    boolean detachIndicator(IndicatorId indicator) {
        checkIsEditable();
        checkIndicatorIsAttached(indicator);

        boolean indicatorFoundAndRemoved = false;

        for (Iterator<ScaleElement> iterator = attachedElements.iterator(); iterator.hasNext(); ) {
            ScaleElement element =  iterator.next();
            if (element instanceof IndicatorElement) {
                if (((IndicatorElement) element).getIndicator().equals(indicator)) {
                    iterator.remove();
                    indicatorFoundAndRemoved = true;
                    break;
                }
            } else if (element instanceof IndicatorGroup) {
                indicatorFoundAndRemoved = ((IndicatorGroup) element).detachIndicator(indicator);
            }
        }

        return indicatorFoundAndRemoved;
    }

    void reorderIndicators(List<IndicatorId> reorderedIndicators) {
        checkIsEditable();
        ensureIndicatorsAttached();
        // TODO
    }



    // Group behaviors

    void attachGroup(IndicatorGroup group) {
        checkIsEditable();
        checkDoesNotContain(group);
        this.attachedElements.add(group);
    }

    private boolean contains(IndicatorGroup group) {
        boolean found = false;
        int i = 0;
        int elementCount = this.attachedElements.size();

        while (!found && i < elementCount) {
            ScaleElement attachedElement = this.attachedElements.get(i);
            if (attachedElement instanceof IndicatorElement) {
                found = false;
            } else if (attachedElement instanceof IndicatorGroup) {
                found = attachedElement.equals(group) || ((IndicatorGroup) attachedElement).contains(group);
            } else {
                throw new UnsupportedOperationException("Attached ScaleElement is of unsupported type: " + attachedElement.getClass().getCanonicalName());
            }
            i++;
        }

        return found;
    }



    // Guards

    private void checkIsEditable() {
        if (!this.isEditable) {
            throw new IllegalStateException("A Scale can be edited only when it has a Draft status.");
        }
    }

    private void checkDoesNotContain(IndicatorGroup group) {
        if (this.contains(group)) {
            throw new IllegalStateException("The Indicator Group '" + group.name + "' is already attached to the Scale. An Indicator Group can be used only once in a Scale.");
        }
    }

    private void checkIndicatorIsAttached(IndicatorId indicator) {
        if (!contains(indicator)) {
            throw new IllegalArgumentException("The Indicator '" + indicator.getUuid() + "' was not previously attached to this scale");
        }
    }

    private void ensureIndicatorsAttached() {
        if (getIndicatorCount() == 0) {
            throw new IllegalStateException("No Indicator has been added to this scale yet");
        }
    }



    // Helpers

    ScaleElement getElementAtPosition(int i) {
        return this.attachedElements.get(i);
    }

}
