package com.noetic.zong_billing.services.zongmml;

import com.noetic.zong_billing.services.PostPaidOrPrePaidCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;

public class ZongHeartBeat implements Runnable {

    Logger log = LoggerFactory.getLogger(ZongHeartBeat.class.getName());

    private String msg;

    private ZongMMLRequest zongMMLRequest = new ZongMMLRequest();
    @Override
    public void run() {
        try {
            String hearbeat = "`SC`0004HBHBB7BDB7BD";
            msg = zongMMLRequest.connect(hearbeat, "Y");
            log.info(msg);
        } catch (SocketException e) {
            log.info("ZONG LOGIN | EXCEPTION CAUGHT HERE" + e.getStackTrace());
            //  obj_ZongClient.Login();
        }catch (Exception e){
            log.info("Excption "+e.getMessage());
        }
    }
}
