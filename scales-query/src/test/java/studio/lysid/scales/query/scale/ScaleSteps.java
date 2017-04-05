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

import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import studio.lysid.scales.query.indicator.IndicatorId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ScaleSteps {

    private Exception thrownException;

    private ScaleAggregate someScale;
    private ScaleAggregate anotherScale;

    private HashMap<String, IndicatorId> someIndicators;

    @Before()
    public void prepareForNewScenario() {
        this.thrownException = null;
        this.someScale = null;
        this.someIndicators = new HashMap<>(5);
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
        assertEquals(this.thrownException.getClass(), IllegalStateException.class);
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

    @Given("^an Indicator named \"([^\"]*)\"$")
    public void anIndicatorNamed(String indicatorName) {
        this.someIndicators.put(indicatorName, new IndicatorId(indicatorName));
    }

    @Then("^the Scale should contain \"([^\"]*)\"$")
    public void theScaleShouldContain(String indicatorName) {
        IndicatorId expectedIndicator = this.someIndicators.get(indicatorName);
        assertTrue(this.someScale.getAttachedIndicators().contains(expectedIndicator));
    }

    @When("^the Indicator \"([^\"]*)\" is attached to the Scale$")
    public void theIndicatorIsAttachedToTheScale(String indicatorName) {
        try {
            this.someScale.attachIndicator(this.someIndicators.get(indicatorName));
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @When("^I attach an Indicator to the Scale$")
    public void iAttachAnIndicatorToTheScale() {
        final String indicatorName = "someIndicator";
        final IndicatorId someIndicator = new IndicatorId(indicatorName);
        this.someIndicators.put(indicatorName, someIndicator);
        try {
            this.someScale.attachIndicator(someIndicator);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Given("^the following Indicators:$")
    public void theFollowingIndicators(DataTable indicatorNamesTable) {
        List<Map<String, String>> indicatorNameRows = indicatorNamesTable.asMaps(String.class, String.class);
        for (Map<String, String> indicatorNameRow : indicatorNameRows) {
            String indicatorName = indicatorNameRow.get("indicatorName");
            this.someIndicators.put(indicatorName, new IndicatorId(indicatorName));
        }
    }

    @When("^these Indicators are attached to the Scale$")
    public void theseIndicatorsAreAttachedToTheScale() {
        for (IndicatorId indicator : this.someIndicators.values()) {
            this.someScale.attachIndicator(indicator);
        }
    }

    @Then("^the Scale should contain exactly the following Indicators:$")
    public void theScaleShouldContainTheFollowingIndicators(DataTable expectedIndicatorNameTable) {
        List<IndicatorId> scaleIndicators = this.someScale.getAttachedIndicators();
        List<Map<String, String>> expectedIndicatorNameRows = expectedIndicatorNameTable.asMaps(String.class, String.class);
        assertEquals(scaleIndicators.size(), expectedIndicatorNameRows.size());

        for (Map<String, String> expectedIndicatorNameRow : expectedIndicatorNameRows) {
            String expectedIndicatorName = expectedIndicatorNameRow.get("indicatorName");
            IndicatorId expectedIndicator = this.someIndicators.get(expectedIndicatorName);
            assertTrue(scaleIndicators.contains(expectedIndicator));
        }
    }
}