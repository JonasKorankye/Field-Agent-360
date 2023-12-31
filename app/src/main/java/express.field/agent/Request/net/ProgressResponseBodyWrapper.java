package express.field.agent.Request.net;


import java.io.IOException;

import express.field.agent.Helpers.ActivityProvider;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBodyWrapper extends ResponseBody {

    private final ResponseBody responseBody;
    private final OnDownloadListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBodyWrapper(ResponseBody responseBody, OnDownloadListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }

        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);

                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                float percent = bytesRead == -1 ? 100f : (((float)totalBytesRead / (float) responseBody.contentLength()) * 100);

                if (progressListener != null) {
                    ActivityProvider.getInstance().getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressListener.ontDownloadUpdate((int) percent);
                        }
                    });
                }

                return bytesRead;
            }
        };
    }

}
