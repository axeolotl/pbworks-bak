package de.sophienallee.backup;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * base class for the response from a pbworks api operation.
 */
public abstract class PBWOperation {
    List<NameValuePair> qparams = new ArrayList<NameValuePair>();
    HttpResponse httpResponse;
    PBWorks pbworks;

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    abstract String getOperationName();

    public void execute(PBWorks pbworks) throws IOException, JSONException {
        this.pbworks = pbworks;
        URI apiUri = pbworks.getAPIUrl();
        addParam("op", getOperationName());
        addOperationParams();
        appendAuthKey(pbworks);
        URI uri;
        try {
            StringBuilder path = new StringBuilder(apiUri.getPath());
            for (NameValuePair nvp : qparams) {
                if (nvp.getValue().indexOf('/') != -1)
                    System.out.println("Warning: API can't handle parameter with slash: " + nvp);
                path.append('/').append(nvp.getName()).append('/').append(nvp.getValue());
            }
            // createURI resp. new java.net.URI takes care of encoding.
            uri = new URI(apiUri.getScheme(), apiUri.getHost(), path.toString(), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        System.out.println("invoking " + uri);
        HttpGet httpget = new HttpGet(uri);
        httpResponse = pbworks.getHttpClient().execute(httpget);
    }

    void appendAuthKey(PBWorks pbworks) {
        addParam("read_key", pbworks.getReadKey());
    }

    final void addParam(String key, String value) {
        qparams.add(new BasicNameValuePair(key, value));
    }

    void addParam(String name, Boolean bool) {
        if (bool != null) {
            addParam(name, bool.toString());
        }
    }

    void addOperationParams() {

    }
}
