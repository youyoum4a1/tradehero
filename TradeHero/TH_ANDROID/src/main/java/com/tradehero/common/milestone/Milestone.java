package com.tradehero.common.milestone;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 4:44 PM To change this template use File | Settings | File Templates. */
public interface Milestone
{
    void launch();
    boolean isRunning();
    boolean isComplete();
    boolean isFailed();
    void setOnCompleteListener(OnCompleteListener listener);
    OnCompleteListener getOnCompleteListener();
    void onDestroy();

    public static interface OnCompleteListener
    {
        void onComplete(Milestone milestone);
        void onFailed(Milestone milestone, Throwable throwable);
    }
}
