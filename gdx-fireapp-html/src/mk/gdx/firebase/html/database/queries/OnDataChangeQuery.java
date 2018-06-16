/*
 * Copyright 2017 mk
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

package mk.gdx.firebase.html.database.queries;

import mk.gdx.firebase.database.validators.ArgumentsValidator;
import mk.gdx.firebase.database.validators.OnDataValidator;
import mk.gdx.firebase.html.database.Database;
import mk.gdx.firebase.html.database.GwtDatabaseQuery;
import mk.gdx.firebase.html.database.json.JsonDataListener;
import mk.gdx.firebase.listeners.DataChangeListener;

/**
 * Provides setValue execution.
 */
public class OnDataChangeQuery extends GwtDatabaseQuery {
    public OnDataChangeQuery(Database databaseDistribution) {
        super(databaseDistribution);
    }

    @Override
    protected void runJS() {
        // TODO DataChangeListener::onCancelled
        if (arguments.get(1) != null && !GwtDataListenersManager.hasListener(databaseReference)) {
            GwtDataListenersManager.addDataListener(databaseReference, new JsonDataListener((Class) arguments.get(0), (DataChangeListener) arguments.get(1)));
            onValue(databaseReference);
        } else if (arguments.get(1) == null) {
            offValue(databaseReference);
        }
    }

    @Override
    protected ArgumentsValidator createArgumentsValidator() {
        return new OnDataValidator();
    }

    /**
     * Attaches listener from {@link GwtDataListenersManager} to given reference.
     *
     * @param reference Reference path, not null
     */
    public static native void onValue(String reference) /*-{
           var ref = reference;
           $wnd.valueListeners = $wnd.valueListeners || {};
           $wnd.valueListeners[reference] = $wnd.firebase.database().ref(ref).on("value", function(snap){
            var val = JSON.stringify(snap.val());
            @mk.gdx.firebase.html.database.queries.GwtDataListenersManager::callListener(Ljava/lang/String;Ljava/lang/String;)(ref,val);
          });
    }-*/;

    /**
     * Remove value listeners for given path.
     * <p>
     * If listener was not declared before - all value listeners for given path will be cleared.
     *
     * @param reference Reference path, not null
     */
    public static native void offValue(String reference) /*-{
        $wnd.valueListeners = $wnd.valueListeners || {};
        var listener = $wnd.valueListeners[reference] || null;
        $wnd.firebase.database().ref(reference).off('value', listener);
        @mk.gdx.firebase.html.database.queries.GwtDataListenersManager::removeDataListener(Ljava/lang/String;)(reference);
    }-*/;

}
