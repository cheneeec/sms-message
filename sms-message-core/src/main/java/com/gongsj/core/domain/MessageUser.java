package com.gongsj.core.domain;

import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@NoArgsConstructor
public class MessageUser {

    //系统唯一标识(不可变，手动生成)
    private String id;
    //系统名
    private String systemName;
    //已经购买的短信数
    private int own;
    //已经发送的消息数
    private int spend;
    //可用的消息数(own-spend)
    private int usable;
    //截止目前的所有已发送的短信条数
    private int historySpend;
    //系统归类
    private String category;
    //警告率（/100为）
    private int warningRate = 30;
    //上一次警告时间(保存历史记录)
    private List<Date> lastWarnDate;
    //警告时间间隔（提供默认值，可设置。单位：天）
    private int warningInterval = 10;
    //负责人电话
    private String principalPhone;
    //负责人名字
    private String principalName;
    //创建时间
    private Date created = new Date();
    //绑定的IP
    private List<String> ips;


    private SmsUserStatus userStatus = SmsUserStatus.ACTIVE;

    public enum SmsUserStatus {
        ACTIVE,//正常可用
        ARREARS,//欠钱
        DISABLED//停用
    }

    public MessageUser(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return 可用的数量始终是 <code>own-spend</code>。
     */
    public int getUsable() {
        usable = own - spend;
        return usable;
    }

    /**
     * @return 当可用数为0时，为欠费状态。
     */
    public SmsUserStatus getUserStatus() {
        if (this.userStatus == SmsUserStatus.DISABLED) {
            return SmsUserStatus.DISABLED;

        } else {
            if (getUsable() > 0) {
                return this.userStatus;
            } else {
                return SmsUserStatus.ARREARS;
            }
        }


    }

    public String getId() {
        return id;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public int getOwn() {
        return own;
    }

    public void setOwn(int own) {
        this.own = own;
    }

    public int getSpend() {
        return spend;
    }

    public void setSpend(int spend) {
        this.spend = spend;
    }

    public void setUsable(int usable) {
        this.usable = usable;
    }

    public int getHistorySpend() {
        return historySpend;
    }

    public void setHistorySpend(int historySpend) {
        this.historySpend = historySpend;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getWarningRate() {
        return warningRate;
    }

    public void setWarningRate(int warningRate) {
        this.warningRate = warningRate;
    }

    public List<Date> getLastWarnDate() {
        return lastWarnDate;
    }

    public void setLastWarnDate(List<Date> lastWarnDate) {
        this.lastWarnDate = lastWarnDate;
    }

    public int getWarningInterval() {
        return warningInterval;
    }

    public void setWarningInterval(int warningInterval) {
        this.warningInterval = warningInterval;
    }

    public String getPrincipalPhone() {
        return principalPhone;
    }

    public void setPrincipalPhone(String principalPhone) {
        this.principalPhone = principalPhone;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public void setUserStatus(SmsUserStatus userStatus) {
        this.userStatus = userStatus;
    }


}
