package de.sophienallee.backup;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * create a backup of the files in our pbworks workspace.
 */
public class Backup {
    public static final String SYSTEM_PROPERTY_PBWORKS_API_READ_KEY = "PBWORKS_API_READ_KEY";
    public static final String SYSTEM_PROPERTY_PBWORKS_API_ADMIN_KEY = "PBWORKS_API_ADMIN_KEY";

    public static void main(String[] args) throws IOException, JSONException {
        PBWorks pbworks = new PBWorks();
        pbworks.setWorkspaceName("sophienallee");
        String pbworks_api_read_key = System.getProperty(SYSTEM_PROPERTY_PBWORKS_API_READ_KEY);
        if (pbworks_api_read_key == null) {
            pbworks_api_read_key = System.getenv(SYSTEM_PROPERTY_PBWORKS_API_READ_KEY);
        }
        if (pbworks_api_read_key == null) {
            System.out.println("The system property or env var " + SYSTEM_PROPERTY_PBWORKS_API_READ_KEY + " must be set");
            return;
        }
        pbworks.setReadKey(pbworks_api_read_key);

        String pbworks_api_admin_key = System.getProperty(SYSTEM_PROPERTY_PBWORKS_API_ADMIN_KEY);
        if (pbworks_api_admin_key == null) {
            pbworks_api_admin_key = System.getenv(SYSTEM_PROPERTY_PBWORKS_API_ADMIN_KEY);
        }
        if (pbworks_api_admin_key != null) {
            pbworks.setAdminKey(pbworks_api_admin_key);
        }

        // here's some code examples.
        if (false) {
            PBWGetPage getPage = pbworks.getPage("Sophienallee");
            if (getPage != null) {
                System.out.println(getPage.getResponse());
            } else {
                System.out.println("sorry, no page could be retrieved");
            }
            System.exit(0);
        }
        if (false) {
            PBWGetFiles getFiles = pbworks.getFiles();
            System.out.println(getFiles.getResponse());
        }
        if (false) {
            PBWGetFile getFile = new PBWGetFile();
            getFile.setRedirect(false);
            getFile.setFileName("2OG-S04-A3-50.pdf");
            getFile.execute(pbworks);
            System.out.println(getFile.getResponse());
            File download = getFile.download();
            System.out.println("length: " + download.length());
            System.out.println(download.getAbsolutePath());
        }
        if (false) {
            PBWGetFolders getFolders = new PBWGetFolders();
            getFolders.setVerbose(true);
            getFolders.execute(pbworks);
            System.out.println(getFolders.getResponse());
            System.out.println(getFolders.getFolders());
        }
        if (false) {
            PBWCreateExport createExport = new PBWCreateExport();
            createExport.execute(pbworks);
            System.out.println(createExport.getResponse());
            System.out.println(createExport.getSuccess());
        }
        if (false) {
            PBWGetExportStatus getExportStatus = new PBWGetExportStatus();
            getExportStatus.execute(pbworks);
            System.out.println(getExportStatus.getResponse());
            List<PBWGetExportStatus.ExportInfo> exports = getExportStatus.getExports();
            System.out.println(exports);
            if(exports.size()>0 && exports.get(0).getSegments().size() > 0) {
                String segmentName = exports.get(0).getSegments().get(0).getName();
                PBWGetExport getExport = new PBWGetExport(segmentName);
                getExport.execute(pbworks);
                System.out.println(getExport.download());
            }
        }
        if (true) {
            PBWDeleteExport deleteExport = new PBWDeleteExport();
            deleteExport.execute(pbworks);
            System.out.println(deleteExport.getResponse());
            System.out.println(deleteExport.getSuccess());
        }

        // here's the actual backup.
        if (false) {
            GetFolderTree getFolderTree = new GetFolderTree();
            getFolderTree.execute(pbworks);
            File exportDir = new File((args.length == 0) ? "." : args[0]);
            if (!(exportDir.exists() && exportDir.isDirectory()) && !exportDir.mkdirs())
                throw new IOException("Could not create export directory `"+exportDir+"'");
            exportFolder(pbworks, getFolderTree.getRoot(), getFolderTree, exportDir);
        }
    }

    private static void exportFolder(PBWorks pbworks, PBWGetFolders.FolderInfo folder, GetFolderTree folderTree, File path) throws IOException, JSONException {
        if (folder.getFilecount() > 0) {
            if (folder.getName().equals("<root>")) {
                // TODO
            } else {
                System.out.println("files in " + folder.getName() + ":");
                PBWGetFiles getFiles = new PBWGetFiles();
                getFiles.setFolder(folder.getName());
                getFiles.setVerbose(true);
                getFiles.setDetailFull(false);
                getFiles.execute(pbworks);
                //System.out.println(getFiles.getResponse());
                List<PBWGetFiles.FileInfo> fileInfos = getFiles.getFileInfos();
                System.out.println(fileInfos);
                for (PBWGetFiles.FileInfo fileInfo : fileInfos) {
                    File output = new File(path, fileInfo.getName());
                    if (output.exists() && output.lastModified() >= fileInfo.getMtime()
                            && output.length() == fileInfo.getSize()) {
                        System.out.println("not modified: " + output);
                    } else {
                        PBWGetFile getFile = new PBWGetFile();
                        getFile.setOid(fileInfo.getOid());
                        getFile.setRedirect(false);
                        try {
                            getFile.execute(pbworks);
                            System.out.println(getFile.getResponse());
                            System.out.println("Downloading " + fileInfo.getSize() + " bytes to " + output);
                            getFile.download(output);
                        } catch (Exception e) {
                            System.out.println("FAILED to download modified "+output);
                            e.printStackTrace(System.out);
                        }
                    }
                }
            }
        }
        for (PBWGetFolders.FolderInfo child : folderTree.getChildren(folder)) {
            String childName = child.getName();
            if (childName.indexOf('/') > 0) {
                System.out.println("SKIPPING folder with name containing slash: " + childName);
                continue; // character is legal neither in file name nor URL path and spells trouble
            }
            File subpath = new File(path, childName);
            if (!subpath.isDirectory()) {
                System.out.println("creating directory " + subpath);
                if (!subpath.mkdir())
                    System.out.println(" >>> FAILED ?!");
            }
            exportFolder(pbworks, child, folderTree, subpath);
        }

    }
}
