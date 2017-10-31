package com.robsterthelobster.ucitransit.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.robsterthelobster.ucitransit.data.models.Segment;
import com.robsterthelobster.ucitransit.data.models.SegmentData;

import java.lang.reflect.Type;
import java.util.Map;

import io.realm.RealmList;

/**
 * Created by robin on 10/27/2017.
 */

public class SegmentDeserializer implements JsonDeserializer<SegmentData>{

    private static final String KEY_RATE = "rate_limit";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_LATEST_API_VERSION = "api_latest_version";
    private static final String KEY_GENERATED_ON = "generated_on";
    private static final String KEY_DATA = "data";
    private static final String KEY_API_VERSION = "api_version";

    @Override
    public SegmentData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        final int rateLimit = jsonObject.get(KEY_RATE).getAsInt();
        final int expiresIn = jsonObject.get(KEY_EXPIRES_IN).getAsInt();
        final String apiLatestVersion = jsonObject.get(KEY_LATEST_API_VERSION).getAsString();
        final String generatedOn = jsonObject.get(KEY_GENERATED_ON).getAsString();
        final String apiVersion = jsonObject.get(KEY_API_VERSION).getAsString();
        final RealmList<Segment> data = parseJsonDataToSegment(jsonObject);

        SegmentData segmentData = new SegmentData();
        segmentData.setRateLimit(rateLimit);
        segmentData.setExpiresIn(expiresIn);
        segmentData.setApiLatestVersion(apiLatestVersion);
        segmentData.setGeneratedOn(generatedOn);
        segmentData.setApiVersion(apiVersion);
        segmentData.setData(data);

        return segmentData;
    }

    private RealmList<Segment> parseJsonDataToSegment(final JsonObject jsonObject){
        final JsonElement paramsElement = jsonObject.get(KEY_DATA);

        if (paramsElement == null) {
            // value not present at all, just return null
            return null;
        }

        final JsonObject parametersObject = paramsElement.getAsJsonObject();
        final RealmList<Segment> segmentRealmList = new RealmList<>();

        for (Map.Entry<String, JsonElement> entry : parametersObject.entrySet()) {
            final Segment segment = new Segment();
            segment.setSegmentId(entry.getKey());
            segment.setSegmentCode(entry.getValue().getAsString());
            segmentRealmList.add(segment);
        }

        return segmentRealmList;
    }
}
