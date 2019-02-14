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

import apple.foundation.NSError;
import bindings.google.firebasedatabase.FIRDataSnapshot;
import bindings.google.firebasedatabase.FIRDatabaseQuery;
import bindings.google.firebasedatabase.enums.FIRDataEventType;
import pl.mk5.gdx.fireapp.database.OrderByClause;
import pl.mk5.gdx.fireapp.database.validators.ArgumentsValidator;
import pl.mk5.gdx.fireapp.database.validators.OnDataValidator;
import pl.mk5.gdx.fireapp.promises.ConverterPromise;
import pl.mk5.gdx.fireapp.promises.FutureListenerPromise;

/**
 * Provides call to {@link FIRDatabaseQuery#observeEventTypeWithBlockWithCancelBlock(long, FIRDatabaseQuery.Block_observeEventTypeWithBlockWithCancelBlock_1, FIRDatabaseQuery.Block_observeEventTypeWithBlockWithCancelBlock_2)}.
 */
class QueryOnDataChange<R> extends IosDatabaseQuery<R> {

    QueryOnDataChange(Database databaseDistribution, String databasePath) {
        super(databaseDistribution, databasePath);
    }

    @Override
    protected ArgumentsValidator createArgumentsValidator() {
        return new OnDataValidator();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected R run() {
        long handle = filtersProvider.applyFiltering().observeEventTypeWithBlockWithCancelBlock(FIRDataEventType.Value,
                new DataChangeBlock((Class) arguments.get(0), orderByClause, (ConverterPromise) promise),
                new DataChangeCancelBlock((ConverterPromise) promise));
        ((FutureListenerPromise) promise).onCancel(new CancelAction(handle, query));
        return null;
    }

    /**
     * Observer for data change. Wraps {@code DataChangeListener}
     */
    private class DataChangeBlock implements FIRDatabaseQuery.Block_observeEventTypeWithBlockWithCancelBlock_1 {

        private Class type;
        private ConverterPromise promise;
        private OrderByClause orderByClause;

        private DataChangeBlock(Class type, OrderByClause orderByClause, ConverterPromise promise) {
            this.type = type;
            this.orderByClause = orderByClause;
            this.promise = promise;
        }


        @Override
        public void call_observeEventTypeWithBlockWithCancelBlock_1(FIRDataSnapshot arg0) {
            if (arg0.value() == null) {
                promise.doComplete(new Exception(GIVEN_DATABASE_PATH_RETURNED_NULL_VALUE));
            } else {
                Object data = null;
                try {
                    if (!ResolverFIRDataSnapshotOrderBy.shouldResolveOrderBy(orderByClause, type, arg0)) {
                        data = DataProcessor.iosDataToJava(arg0.value(), type);
                    } else {
                        data = ResolverFIRDataSnapshotOrderBy.resolve(arg0);
                    }
                } catch (Exception e) {
                    promise.doFail(e);
                    return;
                }
                promise.doComplete(data);
            }
        }
    }

    /**
     * Observer for data change cancel. Wraps {@code DataChangeListener}
     */
    private class DataChangeCancelBlock implements FIRDatabaseQuery.Block_observeEventTypeWithBlockWithCancelBlock_2 {

        private ConverterPromise promise;

        private DataChangeCancelBlock(ConverterPromise promise) {
            this.promise = promise;
        }

        @Override
        public void call_observeEventTypeWithBlockWithCancelBlock_2(NSError arg0) {
            promise.doFail(new Exception(arg0.localizedDescription()));
        }
    }

    private static class CancelAction implements Runnable {

        private final long handle;
        private final FIRDatabaseQuery query;

        private CancelAction(long handle, FIRDatabaseQuery query) {
            this.handle = handle;
            this.query = query;
        }

        @Override
        public void run() {
            query.removeObserverWithHandle(handle);
        }
    }
}