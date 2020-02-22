package com.oounabaramusic.android.okhttputil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class UploadRequestBody extends RequestBody {

    private RequestBody mRequestBody;
    private WriteListener mListener;
    private long fileSize;

    public UploadRequestBody(RequestBody body,WriteListener listener){
        mRequestBody=body;
        mListener=listener;
        init();
    }

    private void init (){
        try {
            fileSize=mRequestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long contentLength() throws IOException {
        return fileSize;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
        MyBufferedSink mBufferedSink = new MyBufferedSink(bufferedSink);
        BufferedSink sink = Okio.buffer(mBufferedSink);
        mRequestBody.writeTo(sink);
        sink.flush();
    }

    class MyBufferedSink extends ForwardingSink {

        long currentCount;

        MyBufferedSink(@NotNull Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NotNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            currentCount+=byteCount;
            mListener.onWrite(currentCount,fileSize);
        }
    }

    public interface WriteListener{
        void onWrite(long byteCount,long fileSize);
    }
}
