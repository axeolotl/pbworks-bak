package de.sophienallee.backup;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * communication with the pbworks (rest api) server.
 */
public class PBWorks {
    private String readKey;
    private String workspaceName;

    public void setReadKey(String readKey) {
        this.readKey = readKey;
    }

    public String getReadKey() {
        return readKey;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    URI getAPIUrl() {
        // TODO precompute
        try {
            return URIUtils.createURI("https", workspaceName + ".pbworks.com", -1, "/api_v2", null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String SECURE_JSON_PREFIX = "/*-secure-\n";
    public static final String SECURE_JSON_POSTFIX = "\n*/\n";

    private String secureUnfilter(String s) {
        if (s.startsWith(SECURE_JSON_PREFIX)) {
            if (s.endsWith(SECURE_JSON_POSTFIX)) {
                return s.substring(SECURE_JSON_PREFIX.length(), s.length() - SECURE_JSON_POSTFIX.length());
            }
        }
        return s;
    }

    public JSONObject getJson(String s) throws JSONException {
        s = secureUnfilter(s);
        try {
            return new JSONObject(s);
        } catch (JSONException e) {
            System.out.println(s);
            throw e;
        }
    }

    public PBWGetPage getPage(String pageName) throws IOException, JSONException {
        PBWGetPage getPage = new PBWGetPage(pageName);
        getPage.execute(this);
        return getPage;
    }

    HttpClient httpclient = new DefaultHttpClient();

    HttpClient getHttpClient() {
        return httpclient;
    }

    public PBWGetFiles getFiles() throws IOException, JSONException {
        PBWGetFiles getFiles = new PBWGetFiles();
        getFiles.execute(this);
        return getFiles;
    }
}
