package com.robsterthelobster.ucitransit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.realm.Realm;

/**
 * Created by robin on 9/20/2016.
 * https://github.com/Egorand/android-test-rules
 */

public class RealmRule implements TestRule{

    Realm realm;

    @Override
    public Statement apply(Statement base, Description description) {
        return null;
    }

}
