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

package pl.mk5.gdx.fireapp.e2e.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.UUID;

import pl.mk5.gdx.fireapp.GdxFIRAuth;
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser;
import pl.mk5.gdx.fireapp.e2e.runner.E2ETest;
import pl.mk5.gdx.fireapp.functional.BiConsumer;
import pl.mk5.gdx.fireapp.functional.Consumer;

public class GdxFirebaseUserTest extends E2ETest {

    @Override
    public void action() {
        final String email = UUID.randomUUID().toString() + "@gmail.com";
        final String email2 = UUID.randomUUID().toString() + "@gmail.com";
        GdxFIRAuth.instance()
                .createUserWithEmailAndPassword(email, "abcd1234".toCharArray())
                .then(GdxFIRAuth.instance().signInWithEmailAndPassword(email, "abcd1234".toCharArray()))
                .then(new Consumer<GdxFirebaseUser>() {
                    @Override
                    public void accept(GdxFirebaseUser user) {
                        user.updateEmail(email2)
                                .then(new Consumer<GdxFirebaseUser>() {
                                    @Override
                                    public void accept(GdxFirebaseUser user) {
                                        user.reload()
                                                .then(user.sendEmailVerification())
                                                .then(new Consumer<GdxFirebaseUser>() {
                                                    @Override
                                                    public void accept(GdxFirebaseUser gdxFirebaseUser) {
                                                        gdxFirebaseUser.delete().subscribe();
                                                        success();
                                                    }
                                                })
                                                .fail(new DeleteUserBiConsumer());
                                    }
                                })
                                .fail(new DeleteUserBiConsumer());
                    }
                })
                .fail(new DeleteUserBiConsumer());
    }

    @Override
    public void draw(Batch batch) {
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void dispose() {
    }

    private class DeleteUserBiConsumer implements BiConsumer<String, Throwable> {

        @Override
        public void accept(String s, Throwable throwable) {
            Gdx.app.log(GdxFirebaseUserTest.class.getSimpleName(), s, throwable);
            if (GdxFIRAuth.instance().getCurrentUser() != null) {
                GdxFIRAuth.instance().getCurrentUser().delete().subscribe();
            }
        }
    }
}
