package de.sophienallee.backup;

import org.json.JSONException;

/**
 * CreateExport operation
 */
public class PBWCreateExport extends PBWJSONOperation {
    private Boolean files;
    private Boolean revs;
    private Boolean notify;

    public PBWCreateExport() {
    }

    @Override
    String getOperationName() {
        return "CreateExport";
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

    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(Boolean notify) {
        this.notify = notify;
    }

    @Override
    void appendAuthKey(PBWorks pbworks) {
        addParam("admin_key", pbworks.getAdminKey());
    }

    @Override
    void addOperationParams() {
        addParam("files", files);
        addParam("revs", revs);
        addParam("notify", notify);
    }

    public boolean getSuccess() throws JSONException {
        return response.getBoolean("success");
    }
}
