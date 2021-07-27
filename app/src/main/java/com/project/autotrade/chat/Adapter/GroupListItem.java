package com.project.autotrade.chat.Adapter;

public class GroupListItem {
    private String name;
    private String userCount;

    public GroupListItem(String name, String userCount) {
        this.name = name;
        this.userCount = userCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserCount() {
        return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }
}
