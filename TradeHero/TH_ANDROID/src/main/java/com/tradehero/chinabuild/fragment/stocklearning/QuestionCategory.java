package com.tradehero.chinabuild.fragment.stocklearning;

/**
 * Created by palmer on 15/5/4.
 */
public class QuestionCategory {

    public int id;
    public String name;
    public int count;
    public String updatedAtUtc;
    public long updatedAtTicks;

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("id: ");
        stringBuffer.append(id).append(" name: ").append(name).append(" count: ").append(count)
                .append(" updatedAtUtc: ").append(updatedAtUtc).append(" updatedAtTicks: ").append(updatedAtTicks);
        return stringBuffer.toString();
    }

}
