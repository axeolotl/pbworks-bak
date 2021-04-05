package de.sophienallee.backup;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * GetFile operation
 */
public class PBWGetFile extends PBWJSONOperation {
    String fileName;
    Long oid;
    private static final boolean REDIRECT_DEFAULT = true;
    boolean redirect = REDIRECT_DEFAULT;
    String format;
    Long revision;

    public PBWGetFile() {
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    @Override
    String getOperationName() {
        return "GetFile";
    }

    @Override
    void addOperationParams() {
        if (fileName != null)
            addParam("file", fileName);
        if (oid != null)
            addParam("oid", Long.toString(oid));
        if (redirect != REDIRECT_DEFAULT)
            addParam("redirect", Boolean.toString(redirect));
        if (format != null)
            addParam("format", format);
        if (revision != null)
            addParam("revision", Long.toString(revision));
    }

    /**
     * Returns the "One-time download URL for the file" provided by the PBWorks server.
     */
    public String getUrl() throws JSONException {
        if (response.isNull("url"))
            return null;
        String url = response.getString("url");
        // it seems pbworks does not properly encode the file name part in the URL, causing java.net.URI to barf later.
        // see what we can do.
        int lastSlash = url.lastIndexOf('/');
        try {
            url = url.substring(0, lastSlash) + "/" + URLEncoder.encode(url.substring(lastSlash + 1), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    /**
     * advanced helper method: after the operation has been executed, actually download the file contents.
     */
    public File download() throws IOException, JSONException {
        File tempFile = File.createTempFile("pbw", ".tmp");
        download(tempFile);
        return tempFile;
    }

    public void download(File output) throws JSONException, IOException {
        String url = getUrl();
        if (url == null) {
            System.out.println("Failed to obtain URL for " + fileName + " / " + oid);
            return;
        }
        HttpGet httpGet = new HttpGet(url);
        System.out.println("invoking download " + url);
        HttpResponse httpResponse = pbworks.getHttpClient().execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        FileOutputStream out = new FileOutputStream(output);
        try {
            entity.writeTo(out);
        } finally {
            out.close();
        }
    }


}
