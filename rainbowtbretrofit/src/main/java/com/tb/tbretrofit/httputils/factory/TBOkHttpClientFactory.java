package com.tb.tbretrofit.httputils.factory;

import android.app.Application;
import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.tb.tbretrofit.httputils.tools.TbLog;
import com.tb.tbretrofit.httputils.exception.RepeatBuildException;
import com.tb.tbretrofit.httputils.tools.LogInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Create on 2016/8/19.
 *
 * @author Ben
 *         Description-构建okhttpClient
 *         <p>
 *         github  https://github.com/HarkBen
 * @Last_update time - 2016年10月8日14:38:32
 */
public final class TBOkHttpClientFactory {

    private static OkHttpClient okHttpClient;


    public static final class Builder {

        private boolean isDebug = false;

        /**
         * 读取超时
         */
        private int TIMEOUT_READ = 25;
        /**
         * 链接超时
         */
        private int TIMEOUT_CONNECTION = 25;
        /**
         * 写入超时
         * -这玩意儿为啥一般不会出现
         */
        private int TIMEOUT_WRITE = 25;
        private Context context;
        private static List<Interceptor> mInterceptors;

        private Builder() {

        }

        public static final Builder create() {
            mInterceptors = new ArrayList<>();
            return new Builder();
        }

        public Builder addInterceptor(Interceptor interceptor) {
            mInterceptors.add(interceptor);
            return this;
        }

        public Builder setDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        public Builder setTimeout_read(int timeout_read) {
            this.TIMEOUT_READ = timeout_read;
            return this;
        }

        public Builder setTimeout_connection(int timeout_connection) {
            this.TIMEOUT_CONNECTION = timeout_connection;
            return this;
        }

        public Builder setTimeout_write(int TIMEOUT_WRITE) {
            this.TIMEOUT_WRITE = TIMEOUT_WRITE;
            return this;

        }

        public Builder syncCookie(Application context) {
            this.context = context;
            return this;
        }

        /**
         * 这里使用的LogInterceptor 内部采用的也是日志类 TbLog
         * 对于 Tblog的 debug 模式的更改这里统一受约束
         * 保证所有Client统一性，不允许重复调用build()方法
         * @return
         */
        public OkHttpClient  build() {
            if (null == okHttpClient) {
                synchronized (TBOkHttpClientFactory.class) {
                    if (null == okHttpClient) {
                        OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        builder.connectTimeout(this.TIMEOUT_CONNECTION, TimeUnit.SECONDS);
                        builder.readTimeout(this.TIMEOUT_READ, TimeUnit.SECONDS);
                        builder.writeTimeout(this.TIMEOUT_WRITE, TimeUnit.SECONDS);
                        if (null != context) {
                            ClearableCookieJar cookieJar =
                                    new PersistentCookieJar(new SetCookieCache(),
                                            new SharedPrefsCookiePersistor(context));
                            builder.cookieJar(cookieJar);
                        }
                        LogInterceptor logInterceptor = new LogInterceptor();
                        logInterceptor.setLevel(LogInterceptor.Level.BODY);
                        builder.addInterceptor(logInterceptor);

                        for (Interceptor interceptor : mInterceptors) {
                            builder.addInterceptor(interceptor);
                        }

                        TbLog.setDeBug(isDebug);
                        return okHttpClient = builder.build();
                    }
                }
            }
                throw  new RepeatBuildException();
        }
    }


    public static final OkHttpClient getInstance() {
        if (null == okHttpClient) {
            throw new NullPointerException("uh~. When you initializing  TBOkHttpClientFactory you didn't build okHttpClient");
        } else {
            return okHttpClient;
        }
    }
}
