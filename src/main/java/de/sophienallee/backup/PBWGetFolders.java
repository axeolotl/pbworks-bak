package de.sophienallee.backup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * response of a GetFolders operation
 */
public class PBWGetFolders extends PBWOperation {
    private static final boolean VERBOSE_DEFAULT = false;
    boolean verbose = VERBOSE_DEFAULT;
    public static final Long ROOT_FOLDER_OID = 0L;

    public PBWGetFolders() {
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    String getOperationName() {
        return "GetFolders";
    }

    @Override
    void addOperationParams() {
        if (verbose != VERBOSE_DEFAULT)
            addParam("verbose", Boolean.toString(verbose));
    }

    public class FolderInfo {
        final String name;
        final int pagecount, filecount, foldercount;
        final long oid;
        final long parent_id;

        public String getName() {
            return name;
        }

        public int getPagecount() {
            return pagecount;
        }

        public int getFilecount() {
            return filecount;
        }

        public int getFoldercount() {
            return foldercount;
        }

        public long getOid() {
            return oid;
        }

        public long getParent_id() {
            return parent_id;
        }

        public FolderInfo(String name, int pagecount, int filecount, int foldercount, long oid, long parent_id) {
            this.name = name;
            this.pagecount = pagecount;
            this.filecount = filecount;
            this.foldercount = foldercount;
            this.oid = oid;
            this.parent_id = parent_id;
        }

        @Override
        public String toString() {
            return "FolderInfo{" +
                    "name: '" + name + '\'' +
                    ", pagecount: " + pagecount +
                    ", filecount: " + filecount +
                    ", foldercount: " + foldercount +
                    ", oid: " + oid +
                    ", parent_id: " + parent_id +
                    '}';
        }
    }

    public List<FolderInfo> getFolders() throws JSONException {
        JSONArray jsonArray = response.getJSONArray("folders");
        ArrayList<FolderInfo> result = new ArrayList<FolderInfo>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject obj = jsonArray.getJSONObject(i);
            result.add(new FolderInfo(obj.getString("name"),
                    obj.getInt("pagecount"),
                    obj.getInt("filecount"),
                    obj.getInt("foldercount"),
                    obj.getLong("oid"),
                    obj.getLong("parent_id")));
        }
        return Collections.unmodifiableList(result);
    }

    public FolderInfo getRootFolderInfo() throws JSONException {
        FolderInfo root = new FolderInfo("<root>",
                getUnfiledPageCount(),
                getUnfiledFileCount(),
                getUnfiledFolderCount(),
                ROOT_FOLDER_OID,
                -1);
        return root;
    }

    public int getUnfiledFolderCount() throws JSONException {
        return response.getInt("unfiled_foldercount");
    }

    public int getUnfiledFileCount() throws JSONException {
        return response.getInt("unfiled_filecount");
    }

    public int getUnfiledPageCount() throws JSONException {
        return response.getInt("unfiled_pagecount");
    }
}
