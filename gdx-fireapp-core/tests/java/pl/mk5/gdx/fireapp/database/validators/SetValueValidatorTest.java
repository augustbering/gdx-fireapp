/*
 * Copyright 2018 mk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.mk5.gdx.fireapp.database.validators;

import com.badlogic.gdx.utils.Array;

import org.junit.Assert;
import org.junit.Test;

public class SetValueValidatorTest {

    @Test
    public void validate() {
        // Given
        SetValueValidator validator = new SetValueValidator();
        Array arguments = new Array();
        arguments.add("test");

        // When
        validator.validate(arguments);

        // Then
        // no exception
    }

    @Test
    public void validate3() {
        // Given
        SetValueValidator validator = new SetValueValidator();
        Array arguments = new Array();
        arguments.add(null);

        // When
        validator.validate(arguments);

        // Then
        // no exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void validate_error3() {
        // Given
        SetValueValidator validator = new SetValueValidator();
        Array arguments = new Array();

        // When
        validator.validate(arguments);

        // Then
        Assert.fail();
    }
}