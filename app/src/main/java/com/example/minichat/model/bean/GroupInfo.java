package com.example.minichat.model.bean;

import androidx.annotation.NonNull;

//群信息的bean类
public class GroupInfo {
    private String groupName;//群名称
    private String groupId;//群ID
    private String invatePerson;//邀请人

    public GroupInfo(){

    }
    public GroupInfo(String groupName,String groupId,String invatePerson){
        this.groupName = groupName;
        this.groupId = groupId;
        this.invatePerson = invatePerson;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getInvatePerson() {
        return invatePerson;
    }

    public void setInvatePerson(String invatePerson) {
        this.invatePerson = invatePerson;
    }

    @NonNull
    @Override
    public String toString() {
        return "GroupInfo{"+
                "groupName='"+groupName+'\''+
                ", groupId='"+groupId+'\''+
                ", invatePerson='"+invatePerson+'\''+
                '}';
    }
}
