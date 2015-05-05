package com.tradehero.chinabuild.fragment.stocklearning;

/**
 * Created by palmer on 15/4/29.
 */
public class QuestionGroup {
    public int id;
    public int question_group_progress;
    public String name = "";
    public int categoryId;
    public int count;
    public String updatedAtUtc;

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("id: ");
        stringBuffer.append(id).append(" name: ").append(name).append(" count: ").append(count)
                .append(" updatedAtUtc: ").append(updatedAtUtc)
                .append(" question_group_progress: ").append(question_group_progress).append(" categoryId: ").append(categoryId);
        return stringBuffer.toString();
    }
}
