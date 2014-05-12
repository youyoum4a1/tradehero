package com.tradehero.common.milestone;


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
