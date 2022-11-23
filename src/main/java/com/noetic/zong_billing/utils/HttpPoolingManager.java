package com.noetic.zong_billing.utils;

import com.noetic.zong_billing.services.UCIPChargingService;
import com.noetic.zong_billing.services.PostPaidOrPrePaidCheckService;
import org.apache.http.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class HttpPoolingManager {

    static HttpClientConnection conn;

    /***
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public static PoolingHttpClientConnectionManager getPoolingManagerForPostPaidService() throws InterruptedException, ExecutionException, IOException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        HttpClientContext context = HttpClientContext.create();
        cm.setMaxTotal(20);
        cm.setDefaultMaxPerRoute(19);

        //must be uncomment while pushing it on production
//        HttpRoute route = new HttpRoute(new HttpHost("10.13.32.179", 10010));


        //for testing
        HttpRoute route = new HttpRoute(new HttpHost("localhost", 10010));
        ConnectionRequest connRequest = cm.requestConnection(route, null);
        conn = connRequest.get(10, TimeUnit.SECONDS);
        cm.connect(conn, route, 1000, context);
        ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator
                        (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase
                            ("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return 20 * 1000;
            }
        };
        PostPaidOrPrePaidCheckService postPaidOrPrePaidCheckService = new PostPaidOrPrePaidCheckService(cm,conn,myStrategy);
        return cm;
    }

    /***
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public static PoolingHttpClientConnectionManager getPoolingManagerForChargingService() throws InterruptedException, ExecutionException, IOException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        HttpClientContext context = HttpClientContext.create();
        cm.setMaxTotal(20);
        cm.setDefaultMaxPerRoute(19);
        //msut be uncomment while pushing it on production
//        HttpRoute route = new HttpRoute(new HttpHost("10.13.32.179", 10010));

        //for testing
        HttpRoute route = new HttpRoute(new HttpHost("localhost", 10010));
        ConnectionRequest connRequest = cm.requestConnection(route, null);
        conn = connRequest.get(10, TimeUnit.SECONDS);
        cm.connect(conn, route, 1000, context);
        ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator
                        (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase
                            ("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return 20 * 1000;
            }
        };
        UCIPChargingService UCIPChargingService = new UCIPChargingService(cm,conn,myStrategy);
        return cm;
    }
}
