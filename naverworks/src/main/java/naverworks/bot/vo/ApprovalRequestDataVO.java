package naverworks.bot.vo;

import java.util.List;

public class ApprovalRequestDataVO {

    private String status;          /** 결재 문서의 상태 (ex 결재요청, 결재반려 등) */
    private List<String> cn;        /** 알림 메시지를 받은 결재자의 cn값 */
    private String docTitle;        /** 문서 제목 */
    private String writerName;      /** 기안자 */
    private String startDate;       /** 기안 일자 */
    private String targetUri;       /** 결재 문서함 URI */
    private String easyApproval;    /** 간편결재 URI */

    public ApprovalRequestDataVO() {}

    public ApprovalRequestDataVO(String status, List<String> cn, String docTitle, String writerName, String startDate, String targetUri, String easyApproval) {
        this.status = status;
        this.cn = cn;
        this.docTitle = docTitle;
        this.writerName = writerName;
        this.startDate = startDate;
        this.targetUri = targetUri;
        this.easyApproval = easyApproval;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getCn() {
        return cn;
    }

    public void setCn(List<String> cn) {
        this.cn = cn;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    public String getEasyApproval() {
        return easyApproval;
    }

    public void setEasyApproval(String easyApproval) {
        this.easyApproval = easyApproval;
    }

}
