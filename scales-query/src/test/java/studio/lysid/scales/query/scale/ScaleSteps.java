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

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class ScaleSteps {

    private Exception thrownException;

    private ScaleAggregate someScale;
    private ScaleAggregate anotherScale;

    @Before()
    public void prepareForNewScenario() {
        this.thrownException = null;
        this.someScale = null;
    }


    @Given("^(?:a|an) (Draft|Published|Archived|Evolved) Scale$")
    public void aStatusScale(String statusName) {
        this.someScale = createScaleWithStatus(new ScaleId("someScale"), ScaleStatus.valueOf(statusName));
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
                    newScale.evolveInto(new ScaleId("someEvolvingScale"));
                    break;
            }
        }
        return newScale;
    }

    @When("^I create a new Scale$")
    public void iCreateANewScale() {
        this.someScale = createScaleWithStatus(new ScaleId("someScale"), null);
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
    public void itShouldThrowAnIllegalStateExceptionWithMessage(String message) {
        assertNotNull(this.thrownException);
        assertEquals(this.thrownException.getMessage(), message);
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
        this.someScale = createScaleWithStatus(new ScaleId("someScale"), ScaleStatus.valueOf(statusName));
        this.someScale.archive();
    }

    @And("^another (Draft|Published|Archived|Evolved) Scale$")
    public void anotherScale(String statusName) {
        this.anotherScale = createScaleWithStatus(new ScaleId("anotherScale"), ScaleStatus.valueOf(statusName));
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



    // Indicator-related steps

    @Given("^a (Draft|Published|Archived|Evolved) Scale with the following Indicators attached: (.*)$")
    public void aScaleWithTheFollowingIndicatorsAttached(String scaleStatus, List<String> indicatorNames) {
        this.someScale = createScaleWithStatus(new ScaleId("someScale"), ScaleStatus.valueOf(scaleStatus));
        for (String indicatorName : indicatorNames) {
            this.someScale.attachIndicator(new IndicatorId(indicatorName));
        }
    }


    @When("^the Indicator \"([^\"]*)\" is attached to the Scale$")
    public void theIndicatorIsAttachedToTheScale(String indicatorName) {
        try {
            this.someScale.attachIndicator(new IndicatorId(indicatorName));
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("^the Scale should contain exactly the following Indicators: (.*)$")
    public void theScaleShouldContainTheFollowingIndicators(List<String> expectedIndicatorNames) {
        int scaleIndicatorCount = this.someScale.getIndicatorCount();
        assertEquals(scaleIndicatorCount, expectedIndicatorNames.size());
        for (int i = 0; i < scaleIndicatorCount; i++) {
            IndicatorId indicator = this.someScale.getIndicatorAtPosition(i);
            assertTrue(expectedIndicatorNames.contains(indicator.getUuid()));
        }
    }

    @When("^the following Indicators are attached to the Scale: (.*)$")
    public void theFollowingIndicatorsAreAttachedToTheScale(List<String> indicatorNames) {
        for (String indicatorName : indicatorNames) {
            try {
                this.someScale.attachIndicator(new IndicatorId(indicatorName));
            } catch (Exception e) {
                this.thrownException = e;
            }
        }
    }

    @When("^the Indicators are reordered like this: (.*)$")
    public void theIndicatorsAreReorderedLikeThis(List<String> reorderedIndicatorNames) {
        List<IndicatorId> reorderedIndicators =
                reorderedIndicatorNames.stream()
                        .map(s -> new IndicatorId(s))
                        .collect(toList());
        try {
            this.someScale.setIndicatorsOrder(reorderedIndicators);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }
}