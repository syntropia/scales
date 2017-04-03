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
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ScaleSteps {

    private Exception thrownException;

    private ScaleAggregate someScale;

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

    @Then("^it should fail with message \"([^\"]*)\"$")
    public void itShouldThrowAnIllegalStateExceptionWithMessage(String message) {
        assertNotNull(this.thrownException);
        assertEquals(this.thrownException.getClass(), IllegalStateException.class);
        assertEquals(this.thrownException.getMessage(), message);
    }
}