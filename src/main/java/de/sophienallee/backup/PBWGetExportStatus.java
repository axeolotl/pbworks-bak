package de.sophienallee.backup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GetExportStatus operation
 */
public class PBWGetExportStatus extends PBWJSONOperation {
    public PBWGetExportStatus() {
    }

    @Override
    String getOperationName() {
        return "GetExportStatus";
    }

    @Override
    void appendAuthKey(PBWorks pbworks) {
        addParam("admin_key", pbworks.getAdminKey());
    }

    public int getSegmentBytes() throws JSONException {
        return response.getInt("segment_bytes");
    }

    public List<ExportInfo> getExports() throws JSONException {
        JSONArray jsonArray = response.getJSONArray("exports");
        ArrayList<ExportInfo> result = new ArrayList<ExportInfo>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject obj = jsonArray.getJSONObject(i);
            result.add(new ExportInfo(
                obj.getBoolean("running"),
                obj.getLong("size"),
                obj.getBoolean("revs"),
                obj.getBoolean("queued"),
                obj.getBoolean("files"),
                obj.getBoolean("complete"),
                obj.getString("desc"),
                convertSegments(obj.getJSONArray("segments"))
            ));
        }
        return Collections.unmodifiableList(result);
    }

    public List<SegmentInfo> convertSegments(JSONArray jsonArray) throws JSONException {
        ArrayList<SegmentInfo> result = new ArrayList<SegmentInfo>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject obj = jsonArray.getJSONObject(i);
            result.add(new SegmentInfo(
                obj.getLong("bytes"),
                obj.getString("name")
            ));
        }
        return Collections.unmodifiableList(result);
    }

    public static class ExportInfo {
        private final boolean running;
        private final long size;
        private final boolean revs;
        private final boolean queued;
        private final boolean files;
        private final boolean complete;
        private final String desc;
        private final List<SegmentInfo> segments;  /* TODO */

        public ExportInfo(boolean running, long size, boolean revs, boolean queued, boolean files, boolean complete, String desc, List<SegmentInfo> segments) {
            this.running = running;
            this.size = size;
            this.revs = revs;
            this.queued = queued;
            this.files = files;
            this.complete = complete;
            this.desc = desc;
            this.segments = segments;
        }

        public boolean isRunning() {
            return running;
        }

        public long getSize() {
            return size;
        }

        public boolean isRevs() {
            return revs;
        }

        public boolean isQueued() {
            return queued;
        }

        public boolean isFiles() {
            return files;
        }

        public boolean isComplete() {
            return complete;
        }

        public String getDesc() {
            return desc;
        }

        public List<SegmentInfo> getSegments() {
            return segments;
        }

        @Override
        public String toString() {
            return "ExportInfo{" +
                "running=" + running +
                ", size=" + size +
                ", revs=" + revs +
                ", queued=" + queued +
                ", files=" + files +
                ", complete=" + complete +
                ", desc='" + desc + '\'' +
                ", segments=" + segments +
                '}';
        }
    }

    static public class SegmentInfo {
        private final long bytes;
        private final String name;

        public SegmentInfo(long bytes, String name) {
            this.bytes = bytes;
            this.name = name;
        }

        public long getBytes() {
            return bytes;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "SegmentInfo{" +
                "bytes=" + bytes +
                ", name='" + name + '\'' +
                '}';
        }
    }
}
