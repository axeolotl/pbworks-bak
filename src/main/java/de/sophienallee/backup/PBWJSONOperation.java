package de.sophienallee.backup;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * pbworks api operation return JSON formatted result
 */
public abstract class PBWJSONOperation extends PBWOperation {
    JSONObject response;

    public JSONObject getResponse() {
        return response;
    }

    @Override
    public void execute(PBWorks pbworks) throws IOException, JSONException {
        super.execute(pbworks);
        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            System.out.println("non-OK response code: "+httpResponse);
            // TODO: proper error handling
        }
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            String s = EntityUtils.toString(entity);
            this.response = pbworks.getJson(s);
        } // TODO: else ??
    }
}
