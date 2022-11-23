package com.noetic.zong_billing.services.zongmml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

public class ZongMMLRequest {

    Logger log = LoggerFactory.getLogger(ZongMMLRequest.class.getName());

    public TCPClient client;

    public void serverConnection() {
        //must be uncomment while pushing it on production
//        String ServerIP = "172.25.140.21";

        //for testing
        String ServerIP = "localhost";
        int ServerPort = 8010;
        client = new TCPClient();
        client.Connect(ServerIP, ServerPort);
    }

    public String connect(String message, String flag) throws SocketException {
        String output = "";
        try {
            //  	log.debug("IN CONNECT...");
            OutputStream stream;
            //String message = "`SC`005A1.00JS123456PPSPPS  00000000DLGLGN    00000001TXBEG     LOGIN:USER=Noetic,PSWD=Noetic@123;AEBA9EF6";
            byte[] data = message.getBytes("US-ASCII");
            stream = client.GetStream();
            stream.write(data, 0, data.length);
            output = "Sent: " + message;
            log.info("Sent : " + output);

            data = new byte[10240];
            String responseData = null;

            InputStream stream_in = client.Read();
            int bytes = stream_in.read(data, 0, data.length);
            responseData = new String(data, "US-ASCII");
            output = "Received:  " + responseData;
	    log.info(output);
        } catch (Throwable e) {
            output = "ArgumentNullException" + e;
        }

        return output;
    }

    public String logIn() {

        System.out.println("in login line 56 zongmml request");
        String userid = "Noetic";
        String password = "Noetic@123";
        System.out.println("login success");
        String loginbody = "`SC`005A1.00JS123456PPSPPS  00000000DLGLGN    00000001TXBEG     LOGIN:USER="+userid+",PSWD="+password+";";

        String login = loginbody;


        log.info(login);

        serverConnection();
        String CKsumLogin = chksum(login);

        log.info("CKsumLogin  : " + CKsumLogin);
        String logincommand = null;
        try {
            logincommand = connect(login + CKsumLogin, "N");
            System.out.println(logincommand);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return logincommand;
    }

    public String chksum(String cmd) {
        try {

            byte[] data = cmd.getBytes("US-ASCII");
            byte[] checksum = new byte[8];
            for (int i = 16; i <= data.length - 5; i += 4) {
                checksum[0] = (byte) (checksum[0] ^ data[i]);
                checksum[1] = (byte) (checksum[1] ^ data[i + 1]);
                checksum[2] = (byte) (checksum[2] ^ data[i + 2]);
                checksum[3] = (byte) (checksum[3] ^ data[i + 3]);
            }
            // log.debug("CHECKSUM BYTE CRATED");
            int check = 0;
            for (int i = 0; i <= 3; i++) {
                int r = (int) checksum[i];
                int c = (-(r + (1))) & (0xff);
                c <<= (24 - (i * 8));
                check = (check | c);
            }

            return Integer.toHexString(check).toUpperCase();
        } catch (Exception ex) {
            return "Excepion received: " + ex;

        }
    }

    public String deductBalance(String number, String amt, String serviceId) {
        //System.out.println("deduct balanece line 109");
        String header ="`SC`";
      //  System.out.println("line 111 deduct balance");
       // String requestBody = "00761.00JS123456USSD_Pay00000001DLGCON    00000003TXBEG     DEDUCTBALANCE:DN="+number+",AMT="+amt+",SERVICE="+serviceId+",SUBTYPE=P";
        //String headerAndBody = header + requestBody;
        //String chksum = chksum(headerAndBody);
        //String deductBalCommand = null;
//        try {
//            deductBalCommand = connect(headerAndBody+chksum, "N");
//        } catch (SocketException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//        }
       // System.out.println("line 123 deduct");
        return null;
    }

}
