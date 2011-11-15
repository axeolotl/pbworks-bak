package de.sophienallee.backup;

import de.sophienallee.backup.PBWGetFolders.FolderInfo;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * complex method: reconstruct the folder tree structure of the workspace.
 */
public class GetFolderTree {
    private Map<Long, List<FolderInfo>> childrenByOid;
    private FolderInfo root;

    public void execute(PBWorks pbworks) throws IOException, JSONException {
        PBWGetFolders getFolders = new PBWGetFolders();
        getFolders.setVerbose(true);
        getFolders.execute(pbworks);
        List<FolderInfo> folders = getFolders.getFolders();
        root = getFolders.getRootFolderInfo();
        childrenByOid = new HashMap<Long, List<FolderInfo>>();
        for (FolderInfo folder : folders) {
            childrenByOid.put(folder.getOid(), new ArrayList<FolderInfo>());
        }
        childrenByOid.put(PBWGetFolders.ROOT_FOLDER_OID, new ArrayList<FolderInfo>());
        for (FolderInfo f : folders) {
            childrenByOid.get(f.getParent_id()).add(f);
        }
    }

    FolderInfo getRoot() {
        return root;
    }

    public List<FolderInfo> getChildren(long oid) {
        return childrenByOid.get(oid);
    }

    public List<FolderInfo> getChildren(FolderInfo folder) {
        return getChildren(folder.getOid());
    }
}
