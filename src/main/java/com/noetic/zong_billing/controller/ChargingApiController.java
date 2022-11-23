package com.noetic.zong_billing.controller;

import com.noetic.zong_billing.handlers.ChargingRequestHandler;
import com.noetic.zong_billing.utils.AppResponse;
import com.noetic.zong_billing.utils.ChargeRequestDTO;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

//must be uncomment while pushing it on production
//@RestController
//public class ChargingApiController {
//
//    @Autowired
//    ChargingRequestHandler chargingRequestHandler;
//
//    @RequestMapping(value = "/chargezong",method = RequestMethod.POST)
//    public AppResponse charge(@RequestBody ChargeRequestDTO chargeRequestDTO) throws InterruptedException, ExecutionException, JSONException, IOException {
//        try {
//            return chargingRequestHandler.processReqeust(chargeRequestDTO);
//        }catch (Exception e){
//
//        }
//        return null;
//    }
//}

//for testing
@RestController
public class ChargingApiController {
    //code pushed for confirmation
    @Autowired
    ChargingRequestHandler chargingRequestHandler;
    private  com.noetic.zong_billing.utils.AppResponse AppResponse;

    @PostMapping(value = "/charge")
    public AppResponse charge(@RequestBody ChargeRequestDTO chargeRequestDTO)
            throws InterruptedException, ExecutionException, JSONException, IOException {
        try {
            return chargingRequestHandler.processReqeust(chargeRequestDTO);
        }catch (Exception e){

        }
        return AppResponse;
    }}
