package com.robsterthelobster.ucitransit;

import com.robsterthelobster.ucitransit.data.models.Stop;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * Created by robin on 9/21/2016.
 */

public class SampleRealmTest {

    @Rule
    public final RealmRule realmRule = new RealmRule();

    @Before
    public void setUp(){
        realmRule.realm.executeTransaction(realm -> {
            Stop stop = new Stop();
            stop.setName("Test Stop");
            realm.insert(stop);
        });
    }

    @Test
    public void sampleTestDatabaseSize(){
        int size = realmRule.realm
                .where(Stop.class).equalTo("name", "Test Stop").findAll()
                .size();
        assertThat(size, is(1));

    }

}
