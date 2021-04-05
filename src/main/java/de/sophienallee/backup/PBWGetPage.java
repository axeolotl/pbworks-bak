package de.sophienallee.backup;

/**
 * response of a GetPage operation
 */
public class PBWGetPage extends PBWJSONOperation {
    private String pageName;

    public PBWGetPage(String pageName) {
        this.pageName = pageName;
    }

    @Override
    String getOperationName() {
        return "GetPage";
    }

    @Override
    void addOperationParams() {
        addParam("page", pageName);
    }
}
