package naverworks.bot.vo;

public class BirthdayUserInfoVO {

    private String birthday;        /** 생일 */
    private String image;           /** 프로필 사진 */
    private String deptName;        /** 부서명 */
    private String userName;        /** 이름 */

    public BirthdayUserInfoVO() {}

    public BirthdayUserInfoVO(String birthday, String image, String deptName, String userName) {
        this.birthday = birthday;
        this.image = image;
        this.deptName = deptName;
        this.userName = userName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
