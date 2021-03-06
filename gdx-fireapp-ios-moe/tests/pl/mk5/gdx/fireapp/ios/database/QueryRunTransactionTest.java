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

package pl.mk5.gdx.fireapp.ios.database;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.moe.natj.general.NatJ;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import bindings.google.firebasedatabase.FIRDatabase;
import bindings.google.firebasedatabase.FIRDatabaseQuery;
import bindings.google.firebasedatabase.FIRDatabaseReference;
import pl.mk5.gdx.fireapp.database.validators.ArgumentsValidator;
import pl.mk5.gdx.fireapp.database.validators.RunTransactionValidator;
import pl.mk5.gdx.fireapp.functional.Function;
import pl.mk5.gdx.fireapp.ios.GdxIOSAppTest;

@PrepareForTest({NatJ.class, FIRDatabase.class, FIRDatabaseReference.class, FIRDatabaseQuery.class, Database.class})
public class QueryRunTransactionTest extends GdxIOSAppTest {

    private FIRDatabase firDatabase;
    private FIRDatabaseReference firDatabaseReference;

    @Override
    public void setup() {
        super.setup();
        PowerMockito.mockStatic(FIRDatabase.class);
        firDatabase = Mockito.mock(FIRDatabase.class);
        firDatabaseReference = Mockito.mock(FIRDatabaseReference.class);
        Mockito.when(firDatabase.referenceWithPath(Mockito.anyString())).thenReturn(firDatabaseReference);
        Mockito.when(FIRDatabase.database()).thenReturn(firDatabase);
    }

    @Test
    public void createArgumentsValidator() {
        // Given
        QueryRunTransaction query = new QueryRunTransaction(Mockito.mock(Database.class), "/test");

        // When
        ArgumentsValidator argumentsValidator = query.createArgumentsValidator();

        // Then
        Assert.assertTrue(argumentsValidator instanceof RunTransactionValidator);
    }

    @Test
    public void run() throws Exception {
        // Given
        PowerMockito.mockStatic(Database.class);
        Database database = PowerMockito.mock(Database.class);
        PowerMockito.when(database, "dbReference").thenReturn(firDatabaseReference);
        Whitebox.setInternalState(database, "databasePath", "/test");
        QueryRunTransaction query = new QueryRunTransaction(database, "/test");
        Class dataType = String.class;

        // When
        query.withArgs(dataType, Mockito.mock(Function.class)).execute();

        // Then
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).runTransactionBlockAndCompletionBlock(
                Mockito.any(FIRDatabaseReference.Block_runTransactionBlockAndCompletionBlock_0.class),
                Mockito.any(FIRDatabaseReference.Block_runTransactionBlockAndCompletionBlock_1.class)
        );
    }

    @Test
    public void run_withCallback() throws Exception {
        // Given
        PowerMockito.mockStatic(Database.class);
        Database database = PowerMockito.mock(Database.class);
        PowerMockito.when(database, "dbReference").thenReturn(firDatabaseReference);
        Whitebox.setInternalState(database, "databasePath", "/test");
        QueryRunTransaction query = new QueryRunTransaction(database, "/test");
        Class dataType = String.class;

        // When
        query.withArgs(dataType, Mockito.mock(Function.class)).execute();

        // Then
        Mockito.verify(firDatabaseReference, VerificationModeFactory.times(1)).runTransactionBlockAndCompletionBlock(
                Mockito.any(FIRDatabaseReference.Block_runTransactionBlockAndCompletionBlock_0.class),
                Mockito.any(FIRDatabaseReference.Block_runTransactionBlockAndCompletionBlock_1.class)
        );
    }
}