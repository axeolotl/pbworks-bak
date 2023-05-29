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
        if (true) {
            // clean up any pre-existing backups
            if (!deleteExprts(pbworks)) {
                System.err.println("failed to delete pre-exissting backuos");
                return;
            }
            // start creating new backup
            if (!createExport(pbworks)) {
                System.err.println("failed to request new backuo");
                return;
            }
            // wait for export to become available
            PBWGetExportStatus.SegmentInfo segmentInfo = getExportSegmentInfo(pbworks);
            try {
                for(int retries=5; segmentInfo == null && retries > 0; --retries) {
                    System.out.println("Still waiting, remaining retries: "+retries);
                    Thread.sleep(10*1000L);
                    segmentInfo = getExportSegmentInfo(pbworks);
                }
            } catch (InterruptedException e) {
                System.err.println("interrupted while waiting for export to be completed");
                throw new IOException(e);
            }
            if (segmentInfo == null) {
                System.err.println("timed out waiting for export to be completed");
                return;
            }
            // download generated export
            File exportDir = new File((args.length == 0) ? "." : args[0]);
            if (!(exportDir.exists() && exportDir.isDirectory()) && !exportDir.mkdirs())
                throw new IOException("Could not create export directory `"+exportDir+"'");
            String segmentName = segmentInfo.getName();
            PBWGetExport getExport = new PBWGetExport(segmentName);
            getExport.execute(pbworks);
            File downloaded = new File(exportDir, segmentName);
            System.out.println("Downloading to "+downloaded);
            getExport.download(downloaded);
            // clean up
            deleteExprts(pbworks);
        }

        // file backup.
        if (false) {
            GetFolderTree getFolderTree = new GetFolderTree();
            getFolderTree.execute(pbworks);
            File exportDir = new File((args.length == 0) ? "." : args[0]);
            if (!(exportDir.exists() && exportDir.isDirectory()) && !exportDir.mkdirs())
                throw new IOException("Could not create export directory `"+exportDir+"'");
            exportFolder(pbworks, getFolderTree.getRoot(), getFolderTree, exportDir);
        }
    }

    private static PBWGetExportStatus.SegmentInfo getExportSegmentInfo(PBWorks pbworks) throws IOException, JSONException {
        PBWGetExportStatus getExportStatus = new PBWGetExportStatus();
        getExportStatus.execute(pbworks);
        System.out.println(getExportStatus.getResponse());
        List<PBWGetExportStatus.ExportInfo> exports = getExportStatus.getExports();
        System.out.println(exports);
        PBWGetExportStatus.SegmentInfo segmentInfo = null;
        if(exports.size()>0 && exports.get(0).getSegments().size() > 0) {
            segmentInfo = exports.get(0).getSegments().get(0);
        }
        return segmentInfo;
    }

    private static boolean createExport(PBWorks pbworks) throws IOException, JSONException {
        PBWCreateExport createExport = new PBWCreateExport();
        createExport.execute(pbworks);
        System.out.println(createExport.getResponse());
        return createExport.getSuccess();
    }

    private static boolean deleteExprts(PBWorks pbworks) throws IOException, JSONException {
        PBWDeleteExport deleteExport = new PBWDeleteExport();
        deleteExport.execute(pbworks);
        System.out.println(deleteExport.getResponse());
        return deleteExport.getSuccess();
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
