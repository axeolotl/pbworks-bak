package de.sophienallee.backup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * response of a GetFiles operation
 */
public class PBWGetFiles extends PBWOperation {
    private static final boolean VERBOSE_DEFAULT = true;
    boolean verbose = VERBOSE_DEFAULT;
    private static final boolean DETAIL_DEFAULT = true;
    boolean detailFull = DETAIL_DEFAULT;
    String folder;

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * @param detailFull true for "detail: full" or false for "detail: partial"
     */
    public void setDetailFull(boolean detailFull) {
        this.detailFull = detailFull;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public PBWGetFiles() {
    }

    @Override
    String getOperationName() {
        return "GetFiles";
    }

    @Override
    void addOperationParams() {
        if (verbose != VERBOSE_DEFAULT)
            addParam("verbose", Boolean.toString(verbose));
        if (detailFull != DETAIL_DEFAULT)
            addParam("detail", detailFull ? "full" : "partial");
        if (folder != null)
            addParam("folder", folder);
    }

    public List<FileInfo> getFileInfos() throws JSONException {
        JSONArray jsonArray = response.getJSONArray("files");
        ArrayList<FileInfo> result = new ArrayList<FileInfo>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); ++i) {
            if (verbose) {
                JSONObject obj = jsonArray.getJSONObject(i);
                try {
                    result.add(new FileInfo(
                            obj.getString("folder"),
                            obj.getLong("oid"),
                            obj.getString("name"),
                            obj.getLong("mtime"),
                            obj.getLong("size")
                    ));
                } catch (JSONException e) {
                    System.out.println("ignoring malformed file info: "+obj);
                }
            } else {
                String name = jsonArray.getString(i);
                result.add(new FileInfo(null, -1L, name, -1L, -1L));
            }
        }
        return Collections.unmodifiableList(result);
    }

    public class FileInfo {
        private final String folder;
        private final long oid;
        private final String name;
        private final long mtime;
        private final long size;
        // has_custom_perms
        // author
        // perms
        // hidden
        // locked
        // comment
        // revcount


        private FileInfo(String folder, long oid, String name, long mtime, long size) {
            this.folder = folder;
            this.oid = oid;
            this.name = name;
            this.mtime = mtime;
            this.size = size;
        }

        public String getFolder() {
            return folder;
        }

        public long getOid() {
            return oid;
        }

        public String getName() {
            return name;
        }

        public long getMtime() {
            return mtime;
        }

        public long getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "FileInfo{" +
                    "folder: '" + folder + '\'' +
                    ", oid: " + oid +
                    ", name: '" + name + '\'' +
                    ", mtime: " + mtime +
                    ", size: " + size +
                    '}';
        }
    }
}
