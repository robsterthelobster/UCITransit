package com.robsterthelobster.ucitransit;

import android.support.test.InstrumentationRegistry;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by robin on 9/20/2016.
 * https://github.com/Egorand/android-test-rules
 */

class RealmRule implements TestRule{

    Realm realm;

    @Override
    public Statement apply(Statement base, Description description) {

        return new RealmStatement(base);
    }

    private class RealmStatement extends Statement{

        private final Statement base;

        RealmStatement(Statement base){
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                RealmConfiguration config =
                        new RealmConfiguration.Builder(InstrumentationRegistry.getContext())
                                .build();
                Realm.setDefaultConfiguration(config);
                realm = Realm.getDefaultInstance();
                base.evaluate();
            } finally {
                if(realm != null)
                    realm.executeTransaction(realm -> realm.deleteAll());
            }
        }
    }
}
