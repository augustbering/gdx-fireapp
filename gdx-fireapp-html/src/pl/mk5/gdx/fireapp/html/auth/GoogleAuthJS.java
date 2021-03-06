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
package pl.mk5.gdx.fireapp.html.auth;

import pl.mk5.gdx.fireapp.promises.FuturePromise;

/**
 * Provides calls for google authorization.
 */
class GoogleAuthJS {

    private GoogleAuthJS() {
        //
    }

    static native void signIn(final FuturePromise promise) /*-{
        if( $wnd.firebase.auth().currentUser != null ){
            promise.@pl.mk5.gdx.fireapp.promises.FuturePromise::doComplete(Ljava/lang/Object;)(
                @pl.mk5.gdx.fireapp.html.auth.AuthJS::getUserBridge()()
            );
            return;
        }
        var provider = new $wnd.firebase.auth.GoogleAuthProvider();
        if( @pl.mk5.gdx.fireapp.html.GdxFIRAuthHtml::isGoogleAuthAlwaysPromptForAccount()() ){
            provider.setCustomParameters({
              prompt: 'select_account'
            });
        }
        var method = "signInWithPopup";
        if( @pl.mk5.gdx.fireapp.html.GdxFIRAuthHtml::isGoogleAuthViaRedirect()() ){
            method = "signInWithRedirect";
        }
        $wnd.firebase.auth()[method](provider).then(function(response){
             promise.@pl.mk5.gdx.fireapp.promises.FuturePromise::doComplete(Ljava/lang/Object;)(
                @pl.mk5.gdx.fireapp.html.auth.AuthJS::getUserBridge()()
            );
        })['catch'](function(error) {
            promise.@pl.mk5.gdx.fireapp.promises.FuturePromise::doFail(Ljava/lang/Throwable;)(
              @java.lang.Exception::new(Ljava/lang/String;)(error.message)
            );
        });
    }-*/;
}
