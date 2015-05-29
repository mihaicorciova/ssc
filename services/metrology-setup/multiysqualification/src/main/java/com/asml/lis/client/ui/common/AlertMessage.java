/**
 * 
 */
package com.asml.lis.client.ui.common;

/**
 * @author bbenga
 *
 */
public enum AlertMessage {
    
    EXPORTING_FILE_IN_USE(0, "Export Status", "PPT In Use", "Please close any application which uses the file and export again."),
    NO_MATCHING_FILES(1, "File Matching", "No Files matched", "No files to match are selected."),
    PPT_EXPORTED(2, "Export Status", "PPT Exported", "Exported succesfully to PPT file."),
    PPT_NOT_EXPORTED(3, "Export Status", "PPT Not Exported", "Data couldn't be exported to PPT File.");
    
    //"PPT Exported", "Export Status", "Exported succesfully to PPT file."
    
    private final int code;
    private final String title;
    private final String header;
    private final String text;
    
    private AlertMessage(int code, String title, String header, String text) {
        this.code = code;
        this.title = title;
        this.header = header;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getHeader() {
        return header;
    }

    public String getText() {
        return text;
    }
    
    

}
