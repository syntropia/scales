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

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import studio.lysid.scales.query.indicator.IndicatorId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class ScaleSteps {

    private Exception thrownException;

    private ScaleAggregate someScale;
    private ScaleAggregate anotherScale;

    private IndicatorGroupVO someGroup;
    private Map<String, IndicatorId> indicatorForName;
    private Map<String, ScaleId> scaleForName;

    @Before()
    public void prepareForNewScenario() {
        this.thrownException = null;
        this.someScale = null;
        this.someGroup = null;
        this.indicatorForName = new HashMap<>();
        this.scaleForName = new HashMap<>();
    }

    private IndicatorId getIndicatorFromName(String name) {
        IndicatorId indicator = this.indicatorForName.get(name);
        if (indicator == null) {
            indicator = new IndicatorId(UUID.randomUUID());
            this.indicatorForName.put(name, indicator);
        }
        return indicator;
    }

    private ScaleId getScaleFromName(String name) {
        ScaleId scale = this.scaleForName.get(name);
        if (scale == null) {
            scale = new ScaleId(UUID.randomUUID());
            this.scaleForName.put(name, scale);
        }
        return scale;
    }



    // Status-related steps

    @Given("^(?:a|an) (Draft|Published|Archived|Evolved) Scale$")
    public void aStatusScale(String statusName) {
        this.someScale = createScaleWithStatus(getScaleFromName("someScale"), ScaleStatus.valueOf(statusName));
    }

    public static ScaleAggregate createScaleWithStatus(ScaleId id, ScaleStatus status) {
        ScaleAggregate newScale = new ScaleAggregate(id);
        if (status != null) {
            switch (status) {
                case Draft:
                    // Nothing to do
                    break;

                case Published:
                    newScale.publish();
                    break;

                case Archived:
                    newScale.archive();
                    break;

                case Evolved:
                    newScale.publish();
                    newScale.evolveInto(new ScaleId(UUID.randomUUID()));
                    break;
            }
        }
        return newScale;
    }

    @When("^I create a new Scale$")
    public void iCreateANewScale() {
        this.someScale = createScaleWithStatus(getScaleFromName("someScale"), null);
    }

    @Then("^(?:its|the Scale) status should be (Draft|Published|Archived|Evolved)$")
    public void itsStatusShouldBe(String statusName) {
        assertNull(this.thrownException);
        assertEquals(this.someScale.getStatus(), ScaleStatus.valueOf(statusName));
    }

    @When("^I publish this Scale$")
    public void iPublishThisScale() {
        try {
            this.someScale.publish();
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @When("^I archive this Scale$")
    public void iArchiveThisScale() {
        try {
            this.someScale.archive();
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("^it should fail with message \"([^\"]*)\"$")
    public void itShouldFailWithMessage(String message) {
        assertNotNull(this.thrownException);

        // Replace Indicator names by their UUID
        StringBuffer messageWithUUIDs = new StringBuffer();
        Matcher m = Pattern.compile("Indicator '(.+?)'").matcher(message);
        while (m.find()) {
            String indicatorUUID = this.getIndicatorFromName(m.group(1)).getUuid().toString();
            m.appendReplacement(messageWithUUIDs, "Indicator '" + indicatorUUID + "'");
        }
        m.appendTail(messageWithUUIDs);

        assertEquals(this.thrownException.getMessage(), messageWithUUIDs.toString());
    }

    @When("^I unarchive this Scale$")
    public void iUnarchiveThisScale() {
        try {
            this.someScale.unarchive();
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Given("^an Archived Scale previously in (Draft|Published|Archived|Evolved) state$")
    public void anArchivedScalePreviouslyInState(String statusName) {
        this.someScale = createScaleWithStatus(getScaleFromName("someScale"), ScaleStatus.valueOf(statusName));
        this.someScale.archive();
    }

    @And("^another (Draft|Published|Archived|Evolved) Scale$")
    public void anotherScale(String statusName) {
        this.anotherScale = createScaleWithStatus(getScaleFromName("anotherScale"), ScaleStatus.valueOf(statusName));
    }

    @When("^I evolve one into the other$")
    public void iEvolveOneIntoTheOther() {
        this.someScale.evolveInto(this.anotherScale.getId());
    }

    @Then("^the former Scale status should be (Draft|Published|Archived|Evolved)")
    public void theFormerScaleStatusShouldBe(String statusName) {
        assertEquals(this.someScale.getStatus(), ScaleStatus.valueOf(statusName));
    }

    @Then("^it should designate the latter as its evolved version$")
    public void itShouldDesignateTheLatterAsItsEvolvedVersion() {
        assertEquals(this.someScale.getEvolvedInto(), this.anotherScale.getId());
    }



    // Indicator attaching steps

    @Given("^a (Draft|Published|Archived|Evolved) Scale with the following Indicators attached: (.*)$")
    public void aScaleWithTheFollowingIndicatorsAttached(String scaleStatus, List<String> indicatorNames) {
        this.someScale = createScaleWithStatus(getScaleFromName("someScale"), ScaleStatus.valueOf(scaleStatus));
        for (String indicatorName : indicatorNames) {
            this.someScale.attachIndicator(getIndicatorFromName(indicatorName));
        }
    }


    @When("^the Indicator \"([^\"]*)\" is attached to the Scale$")
    public void theIndicatorIsAttachedToTheScale(String indicatorName) {
        try {
            this.someScale.attachIndicator(getIndicatorFromName(indicatorName));
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @When("^the following Indicators are attached to the Scale: (.*)$")
    public void theFollowingIndicatorsAreAttachedToTheScale(List<String> indicatorNames) {
        for (String indicatorName : indicatorNames) {
            try {
                this.someScale.attachIndicator(getIndicatorFromName(indicatorName));
            } catch (Exception e) {
                this.thrownException = e;
            }
        }
    }

    @Then("^the Scale should contain exactly the following Indicators: (.*)$")
    public void theScaleShouldExactlyContainTheFollowingIndicators(List<String> expectedIndicatorNames) {
        int scaleIndicatorCount = this.someScale.getIndicatorCount();
        assertEquals(scaleIndicatorCount, expectedIndicatorNames.size());

        for (int i = 0; i < scaleIndicatorCount; i++) {
            String indicatorName = expectedIndicatorNames.get(i);
            IndicatorId indicator;
            try {
                indicator = this.someScale.getIndicatorAtPosition(i);
                assertTrue(indicator.equals(getIndicatorFromName(indicatorName)));
            } catch (IllegalAccessException e) {
                fail(e.getMessage());
            }
        }
    }



    // Indicator reordering steps

    @When("^the Indicators are reordered like this: (.*)$")
    public void theIndicatorsAreReorderedLikeThis(List<String> reorderedIndicatorNames) {
        List<IndicatorId> reorderedIndicators =
                reorderedIndicatorNames.stream()
                        .map(this::getIndicatorFromName)
                        .collect(toList());
        try {
            this.someScale.setIndicatorsOrder(reorderedIndicators);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }



    // Indicator detach steps

    @When("^the Indicator \"([^\"]*)\" is detached from the Scale$")
    public void theIndicatorIsDetachedFromTheScale(String indicatorName) {
        try {
            this.someScale.detachIndicator(getIndicatorFromName(indicatorName));
        } catch (Exception e) {
            this.thrownException = e;
        }
    }



    // Indicator grouping steps

    @Given("^the Indicator group \"([^\"]*)\" containing the indicators: (.*)$")
    public void theIndicatorGroupContainingTheIndicators(String groupName, List<String> indicators) {
        this.someGroup = new IndicatorGroupVO(groupName, true);
        for (String indicatorName : indicators) {
            this.someGroup.attachIndicator(getIndicatorFromName(indicatorName));
        }
    }

    @When("^it is attached to the Scale$")
    public void itIsAttachedToTheScale() {
        try {
            this.someScale.attachGroup(this.someGroup);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }


}