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

package pl.mk5.gdx.fireapp.android.database;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.Map;

import pl.mk5.gdx.fireapp.GdxFIRDatabase;
import pl.mk5.gdx.fireapp.android.AndroidContextTest;
import pl.mk5.gdx.fireapp.database.Filter;
import pl.mk5.gdx.fireapp.database.FilterType;
import pl.mk5.gdx.fireapp.database.MapConverter;
import pl.mk5.gdx.fireapp.database.OrderByClause;
import pl.mk5.gdx.fireapp.database.OrderByMode;
import pl.mk5.gdx.fireapp.exceptions.DatabaseReferenceNotSetException;
import pl.mk5.gdx.fireapp.functional.Function;
import pl.mk5.gdx.fireapp.promises.Promise;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest({
        GdxNativesLoader.class, FirebaseDatabase.class,
        QueryOnDataChange.class, Database.class, QueryConnectionStatus.class,
        QueryUpdateChildren.class, QueryReadValue.class, QueryRemoveValue.class,
        QuerySetValue.class, QueryRunTransaction.class,
        GdxFIRDatabase.class

})
public class DatabaseTest extends AndroidContextTest {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private MapConverter mapConverter;

    @Override
    public void setup() throws Exception {
        super.setup();
        PowerMockito.mockStatic(GdxFIRDatabase.class);
        PowerMockito.mockStatic(FirebaseDatabase.class);
        firebaseDatabase = PowerMockito.mock(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(firebaseDatabase);
        databaseReference = Mockito.mock(DatabaseReference.class);
        when(firebaseDatabase.getReference(Mockito.anyString())).thenReturn(databaseReference);
        mapConverter = mock(MapConverter.class);
        GdxFIRDatabase gdxFIRDatabase = Mockito.mock(GdxFIRDatabase.class);
        when(GdxFIRDatabase.instance()).thenReturn(gdxFIRDatabase);
        when(gdxFIRDatabase.getMapConverter()).thenReturn(mapConverter);
    }

    @Test
    public void onConnect() throws Exception {
        // Given
        PowerMockito.mockStatic(QueryConnectionStatus.class);
        Database database = new Database();
        QueryConnectionStatus query = Mockito.spy(new QueryConnectionStatus(database, "/test"));
        PowerMockito.whenNew(QueryConnectionStatus.class).withAnyArguments().thenReturn(query);
        when(query.withArgs(Mockito.any())).thenReturn(query);

        // When
        database.onConnect().subscribe();

        // Then
        PowerMockito.verifyNew(QueryConnectionStatus.class);
    }

    @Test
    public void inReference() {
        // Given
        Database database = Mockito.spy(new Database());

        // When
        database.inReference("test");
        DatabaseReference reference = Whitebox.getInternalState(database, "databaseReference");
        String path = Whitebox.getInternalState(database, "databasePath");

        // Then
        Assert.assertEquals("test", path);
        Assert.assertNotNull(reference);
    }

    @Test
    public void setValue() throws Exception {
        // Given
        PowerMockito.mockStatic(QuerySetValue.class);
        Database database = new Database();
        QuerySetValue query = PowerMockito.spy(new QuerySetValue(database, "/test"));
        PowerMockito.whenNew(QuerySetValue.class).withAnyArguments().thenReturn(query);
        when(query.withArgs(Mockito.any())).thenReturn(query);

        // When
        Promise promise = Mockito.spy(database.inReference("/test").setValue("").subscribe());

        // Then
        PowerMockito.verifyNew(QuerySetValue.class);
    }

    @Test
    public void readValue() throws Exception {
        // Given
        PowerMockito.mockStatic(QueryReadValue.class);
        Database database = new Database();
        QueryReadValue query = Mockito.spy(new QueryReadValue(database, "/test"));
        PowerMockito.whenNew(QueryReadValue.class).withAnyArguments().thenReturn(query);

        // When
        database.inReference("/test").readValue(String.class).subscribe();

        // Then
//        verify(mapConverter, VerificationModeFactory.times(1)).convert(any(Map.class), any(Class.class));
        PowerMockito.verifyNew(QueryReadValue.class);
        // TODO - verify converter
    }

    @Test
    public void onDataChange() throws Exception {
        // Given
        PowerMockito.mockStatic(QueryOnDataChange.class);
        QueryOnDataChange query = PowerMockito.mock(QueryOnDataChange.class);
        PowerMockito.whenNew(QueryOnDataChange.class).withAnyArguments().thenReturn(query);
        when(query.with(Mockito.nullable(Array.class))).thenReturn(query);
        when(query.with(Mockito.nullable(OrderByClause.class))).thenReturn(query);
        when(query.withArgs(Mockito.any(), Mockito.any())).thenReturn(query);
        Database database = new Database();

        // When
        database.inReference("/test").onDataChange(Map.class).subscribe();

        // Then
//        verify(mapConverter, VerificationModeFactory.times(1)).convert(any(Map.class), any(Class.class));
        PowerMockito.verifyNew(QueryOnDataChange.class);
        // TODO - verify converter
    }

    @Test
    public void filter() {
        // Given
        Database database = new Database();

        // When
        database.filter(FilterType.LIMIT_FIRST, 2)
                .filter(FilterType.EQUAL_TO, 3);

        // Then
        Assert.assertEquals(FilterType.LIMIT_FIRST, ((Array<Filter>) Whitebox.getInternalState(database, "filters")).get(0).getFilterType());
        Assert.assertEquals(FilterType.EQUAL_TO, ((Array<Filter>) Whitebox.getInternalState(database, "filters")).get(1).getFilterType());
    }

    @Test
    public void orderBy() {
        // Given
        Database database = new Database();

        // When
        database.orderBy(OrderByMode.ORDER_BY_KEY, "test");

        // Then
        Assert.assertEquals(OrderByMode.ORDER_BY_KEY, ((OrderByClause) Whitebox.getInternalState(database, "orderByClause")).getOrderByMode());
        Assert.assertEquals("test", ((OrderByClause) Whitebox.getInternalState(database, "orderByClause")).getArgument());
    }

    @Test
    public void push() {
        // Given
        Database database = new Database();
        when(databaseReference.push()).thenReturn(databaseReference);

        // When
        database.inReference("/test").push();

        // Then
        Mockito.verify(databaseReference, VerificationModeFactory.times(1)).push();
    }

    @Test
    public void removeValue() throws Exception {
        // Given
        PowerMockito.mockStatic(QueryRemoveValue.class);
        Database database = new Database();
        QueryRemoveValue query = PowerMockito.mock(QueryRemoveValue.class);
        PowerMockito.whenNew(QueryRemoveValue.class).withAnyArguments().thenReturn(query);
        when(query.withArgs(Mockito.any())).thenReturn(query);

        // When
        Promise promise = Mockito.spy(database.inReference("/test").removeValue());

        // Then
        PowerMockito.verifyNew(QueryRemoveValue.class);
    }

    @Test
    public void updateChildren() throws Exception {
        // Given
        PowerMockito.mockStatic(QueryUpdateChildren.class);
        Database database = new Database();
        QueryUpdateChildren query = PowerMockito.spy(new QueryUpdateChildren(database, "/test"));
        PowerMockito.whenNew(QueryUpdateChildren.class).withAnyArguments().thenReturn(query);
        when(query.withArgs(Mockito.any())).thenReturn(query);
        Map data = Mockito.mock(Map.class);

        // When
        database.inReference("/test").updateChildren(data);

        // Then
        PowerMockito.verifyNew(QueryUpdateChildren.class);
    }

    @Test
    public void transaction() throws Exception {
        // Given
        PowerMockito.mockStatic(QueryRunTransaction.class);
        Database database = new Database();
        QueryRunTransaction query = PowerMockito.mock(QueryRunTransaction.class);
        PowerMockito.whenNew(QueryRunTransaction.class).withAnyArguments().thenReturn(query);
        when(query.withArgs(Mockito.any())).thenReturn(query);
        Function transactionFunction = Mockito.mock(Function.class);
        Class dataType = String.class;

        // When
        database.inReference("/test").transaction(dataType, transactionFunction);

        // Then
        PowerMockito.verifyNew(QueryRunTransaction.class);
    }

    @Test
    public void setPersistenceEnabled() {
        // Given
        Database database = new Database();

        // When
        database.setPersistenceEnabled(true);

        // Then
        Mockito.verify(firebaseDatabase, VerificationModeFactory.times(1)).setPersistenceEnabled(Mockito.eq(true));
    }

    @Test
    public void keepSynced() {
        // Given
        Database database = new Database();

        // When
        database.inReference("/test").keepSynced(true);

        // Then
        Mockito.verify(databaseReference, VerificationModeFactory.times(1)).keepSynced(Mockito.eq(true));
    }

    @Test(expected = DatabaseReferenceNotSetException.class)
    public void databaseReference() {
        // Given
        Database database = new Database();

        // When
        database.keepSynced(true);

        // Then
        Assert.fail();
    }

    @Test(expected = DatabaseReferenceNotSetException.class)
    public void databaseReference2() {
        // Given
        Database database = new Database();

        // When
        database.setValue("test");
        // Then
        Assert.fail();
    }

    @Test
    public void terminateOperation() {
        // Given
        Database database = new Database();
        database.inReference("test").filter(FilterType.LIMIT_FIRST, 2).orderBy(OrderByMode.ORDER_BY_KEY, "test");

        // When
        database.terminateOperation();

        // Then
        Assert.assertNull(Whitebox.getInternalState(database, "databaseReference"));
        Assert.assertNull(Whitebox.getInternalState(database, "databasePath"));
        Assert.assertNull(Whitebox.getInternalState(database, "orderByClause"));
        Assert.assertEquals(0, ((Array) Whitebox.getInternalState(database, "filters")).size);
    }

}