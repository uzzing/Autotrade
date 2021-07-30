package com.project.autotrade.chat.model_group;

public class ListViewItem {
    private String name;
    private String userCount;

    public ListViewItem() {}

    public ListViewItem(String name, String userCount) {
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
