package de.sophienallee.backup;

import org.json.JSONException;

/**
 * CreateExport operation
 */
public class PBWDeleteExport extends PBWJSONOperation {
    private Boolean files;
    private Boolean revs;

    public PBWDeleteExport() {
    }

    @Override
    String getOperationName() {
        return "DeleteExport";
    }

    public Boolean getFiles() {
        return files;
    }

    public void setFiles(Boolean files) {
        this.files = files;
    }

    public Boolean getRevs() {
        return revs;
    }

    public void setRevs(Boolean revs) {
        this.revs = revs;
    }

    @Override
    void appendAuthKey(PBWorks pbworks) {
        addParam("admin_key", pbworks.getAdminKey());
    }

    @Override
    void addOperationParams() {
        addParam("files", files);
        addParam("revs", revs);
    }

    public boolean getSuccess() throws JSONException {
        return response.getBoolean("success");
    }
}
