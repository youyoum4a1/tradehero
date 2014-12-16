package com.tradehero.chinabuild.data;

public class DiscussReportDTO {

    public int discussionType;
    public int reportType;
    public long discussionId;

    @Override
    public String toString() {
        return "discussionType: " + discussionType + "  reportType: " + reportType + "  discussionId: " + discussionId;
    }
}
