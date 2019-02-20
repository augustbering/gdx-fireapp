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
import pl.mk5.gdx.fireapp.database.validators.ReadValueValidator;
import pl.mk5.gdx.fireapp.promises.ConverterPromise;
import pl.mk5.gdx.fireapp.promises.FuturePromise;

/**
 * Provides call to {@link FIRDatabaseQuery#observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock(long, FIRDatabaseQuery.Block_observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock_1, FIRDatabaseQuery.Block_observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock_2)}.
 */
class QueryReadValue<R> extends IosDatabaseQuery<R> {

    QueryReadValue(Database databaseDistribution, String databasePath) {
        super(databaseDistribution, databasePath);
    }

    @Override
    protected ArgumentsValidator createArgumentsValidator() {
        return new ReadValueValidator();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected R run() {
        filtersProvider.applyFiltering().observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock(FIRDataEventType.Value,
                new ReadValueBlock((Class) arguments.get(0), orderByClause, (ConverterPromise) promise),
                new ReadValueCancelBlock((ConverterPromise) promise));
        return null;
    }

    /**
     * Observer read value block. Wraps {@code DataCallback}
     */
    private static class ReadValueBlock implements FIRDatabaseQuery.Block_observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock_1 {

        private Class type;
        private ConverterPromise promise;
        private OrderByClause orderByClause;

        private ReadValueBlock(Class type, OrderByClause orderByClause, ConverterPromise promise) {
            this.type = type;
            this.orderByClause = orderByClause;
            this.promise = promise;
        }

        @Override
        public void call_observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock_1(FIRDataSnapshot arg0, String arg1) {
            if (arg0.value() == null) {
                // TODO - consider about this fail
                promise.doFail(new Exception(GIVEN_DATABASE_PATH_RETURNED_NULL_VALUE));
            } else {
                Object data = arg0.value();
                try {
                    if (!ResolverFIRDataSnapshotOrderBy.shouldResolveOrderBy(orderByClause, type, arg0)) {
                        data = DataProcessor.iosDataToJava(data, type);
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

    private static class ReadValueCancelBlock implements FIRDatabaseQuery.Block_observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock_2 {

        private FuturePromise promise;

        private ReadValueCancelBlock(FuturePromise promise) {
            this.promise = promise;
        }

        @Override
        public void call_observeSingleEventOfTypeAndPreviousSiblingKeyWithBlockWithCancelBlock_2(NSError arg0) {
            promise.doFail(new Exception(arg0.localizedDescription()));
        }
    }
}
