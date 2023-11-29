package naverworks.bot.vo;

import java.util.List;

public class BoardRequestDataVO {

    private String status;          /** 게시글의 상태 (ex 게시글 알림, 게시글 승인 등) */
    private List<String> cn;        /** 알림 메시지를 받을 구성원의 cn값 */
    private String docTitle;        /** 게시글 제목 */
    private String deptName;        /** 게시자 부서명 */
    private String writerName;      /** 게시자 */
    private String writeDate;       /** 게시 일자 */
    private String targetUri;       /** 게시글 URI */

    public BoardRequestDataVO() {}

    public BoardRequestDataVO(String status, List<String> cn, String docTitle, String deptName, String writerName, String writeDate, String targetUri) {
        this.status = status;
        this.cn = cn;
        this.docTitle = docTitle;
        this.deptName = deptName;
        this.writerName = writerName;
        this.writeDate = writeDate;
        this.targetUri = targetUri;
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

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }
}
