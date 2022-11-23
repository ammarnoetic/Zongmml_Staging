package com.noetic.zong_billing.services;

import com.noetic.zong_billing.utils.HttpPoolingManager;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UCIPChargingService {

    private String methodName ="UpdateBalanceAndDate";

    private String transactionCurrency="PKR";
    private String originNodeType="EXT";
    private String originHostName="Gameinapp";
    private	String transactionType="Gameinapp";
    private String transactionCode="Gameinapp";
    private String externalData1="Gameinapp_VAS";
    private String externalData2="Gameinapp_VAS";

    private String subscriberNumber;
    private double adjustmentAmountRelative;
    private String originTransactionID;
    private String originTimeStamp;
    private static HttpClientConnection conn;
    private static PoolingHttpClientConnectionManager connectionManager;
    private static ConnectionKeepAliveStrategy keepAliveStrategy;
    CloseableHttpClient httpclient;

    Logger log = LoggerFactory.getLogger(UCIPChargingService.class.getName());

    public UCIPChargingService(String subscriberNumber, double adjustmentAmountRelative2, String originTransactionID){

        this.subscriberNumber = subscriberNumber;
        this.adjustmentAmountRelative = adjustmentAmountRelative2;
        this.originTransactionID = originTransactionID;
    }
    public UCIPChargingService(PoolingHttpClientConnectionManager httpClientConnectionManager, HttpClientConnection conn, ConnectionKeepAliveStrategy keepAliveStrategy){
        this.connectionManager = httpClientConnectionManager;
        this.conn = conn;
        this.keepAliveStrategy = keepAliveStrategy;
    }

    public HttpResponse sendRequest(boolean requiresAuthorization) throws ClientProtocolException, IOException, ExecutionException, InterruptedException {
        log.info("Number : "+this.subscriberNumber+" Amount : "+this.adjustmentAmountRelative);
        HttpResponse httpResponse=null;
        httpclient = HttpClients.custom()
                .setConnectionManager(this.connectionManager)
                .setKeepAliveStrategy(keepAliveStrategy)
                .setConnectionTimeToLive(5, TimeUnit.MINUTES)
                .build();
        HttpPost httpRequest = getHttpRequest(requiresAuthorization);
        try {
            return httpclient.execute(httpRequest);
        }catch (IllegalStateException e){
            log.error("EXCEPTION "+e.getMessage());
        }
        HttpPoolingManager.getPoolingManagerForChargingService();
        sendRequest(false);
        return httpResponse;
    }

    private HttpPost getHttpRequest(boolean requiresAuthorization) throws UnsupportedEncodingException {

        Date date = new Date(System.currentTimeMillis());

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
        TimeZone PKT = TimeZone.getTimeZone("Asia/Karachi");
        dateFormat.setTimeZone(PKT);
        this.originTimeStamp = dateFormat.format(date);

        //String originDateTime = dateFormat.format(date)+"+0500";

        //log.info(originDateTime);

        HttpPost httpRequest=null;

        // must be uncomment while pushing it on production
//        httpRequest = new HttpPost("http://10.13.32.179:10010/Air");

        //for testing
        httpRequest = new HttpPost("localhost:10010/Air");

        //Authorization

        String Authorization = "Basic SU5UTk9FVElDOk1vYmkjOTEx";

        httpRequest.addHeader("Content-Type", "text/xml");
        httpRequest.addHeader("User-Agent", "UGw Server/4.3/1.0");
        httpRequest.addHeader("Authorization",Authorization);
        httpRequest.addHeader("Cache-Control", "no-cache");
        httpRequest.addHeader("Pragma", "no-cache");

        //must be uncomment while pushing it on production
//        httpRequest.addHeader("Host", "10.13.32.179:10010");

        //for testing
        httpRequest.addHeader("Host", "localhost:10010");
        httpRequest.addHeader("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
        httpRequest.addHeader("Connection", "keep-alive");
        httpRequest.addHeader("Keep-Alive", "20");
        //httpRequest.addHeader("Content-Length", "1300");

        int adjustmentamount = (int)this.adjustmentAmountRelative;

        String inputXML = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
                "<methodCall>\n" +
                "<methodName>" + this.methodName + "</methodName>\n" +
                "<params>\n" +
                "<param>\n" +
                "<value>\n" +
                "<struct>\n" +
                "<member>\n" +
                "<name>originNodeType</name>\n" +
                "<value><string>" + this.originNodeType + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>originHostName</name>\n" +
                "<value><string>" + this.originHostName + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>originTransactionID</name>\n" +
                "<value><string>" + this.originTransactionID + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>transactionType</name>\n" +
                "<value><string>" + this.transactionType + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>transactionCode</name>\n" +
                "<value><string>" + this.transactionCode + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>externalData1</name>\n" +
                "<value><string>" + this.externalData1 + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>externalData2</name>\n" +
                "<value><string>" + this.externalData2 + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>originTimeStamp</name>\n" +
                "<value><dateTime.iso8601>" + this.originTimeStamp + "+0500</dateTime.iso8601></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>transactionCurrency</name>\n" +
                "<value><string>" + this.transactionCurrency + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>subscriberNumber</name>\n" +
                //"<value><string>3015166666</string></value>\n" +
                "<value><string>" + this.subscriberNumber + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>adjustmentAmountRelative</name>\n" +
                "<value><string>-" + adjustmentamount + "</string></value>\n" +
                "</member>\n" +
                "</struct>\n" +
                "</value>\n" +
                "</param>\n" +
                "</params>\n" +
                "</methodCall>";
        httpRequest.setEntity(new StringEntity(inputXML));
        return httpRequest;

    }
}
