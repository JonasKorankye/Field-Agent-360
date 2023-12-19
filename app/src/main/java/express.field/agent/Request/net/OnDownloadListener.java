package express.field.agent.Request.net;

public interface OnDownloadListener {

    void onDownloadedSuccess();
    void onDownloadedError();
    void onDownloadedFinished();
    void ontDownloadUpdate(int percent);

}
