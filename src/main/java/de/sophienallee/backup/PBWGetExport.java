package de.sophienallee.backup;

import org.apache.http.HttpEntity;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * GetExport operation
 */
public class PBWGetExport extends PBWOperation {
    String name;

    public PBWGetExport(String name) {
        this.name = name;
    }

    @Override
    String getOperationName() {
        return "GetExport";
    }

    @Override
    void appendAuthKey(PBWorks pbworks) {
        addParam("admin_key", pbworks.getAdminKey());
    }

    @Override
    void addOperationParams() {
        addParam("name", name);
    }

    /**
     * advanced helper method: after the operation has been executed, actually download the file contents.
     */
    public File download() throws IOException, JSONException {
        File tempFile = File.createTempFile("pbw", ".zip");
        download(tempFile);
        return tempFile;
    }

    public void download(File output) throws JSONException, IOException {
        HttpEntity entity = httpResponse.getEntity();
        try (FileOutputStream out = new FileOutputStream(output)) {
            entity.writeTo(out);
        }
    }
}
