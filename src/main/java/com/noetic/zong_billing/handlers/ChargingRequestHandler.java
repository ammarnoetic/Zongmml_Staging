package com.noetic.zong_billing.handlers;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.noetic.zong_billing.entities.UcipChargingEntity;
//import com.noetic.zong_billing.repositories.UcipChargingRepository;
import com.noetic.zong_billing.services.PostPaidOrPrePaidCheckService;
import com.noetic.zong_billing.services.UCIPChargingService;
import com.noetic.zong_billing.services.zongmml.ZongMMLRequest;
import com.noetic.zong_billing.utils.AppResponse;
import com.noetic.zong_billing.utils.Constants;
import com.noetic.zong_billing.utils.ChargeRequestDTO;
//import com.noetic.zong_billing.utils.RequestSender;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
//import javax.persistence.criteria.CriteriaBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
//import java.io.PushbackInputStream;
import java.io.StringReader;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

@Service
public class ChargingRequestHandler {
//    @Autowired
//    RequestSender requestSender;
    private UCIPChargingService ucipChargingService = null;
//    @Autowired
//    private UcipChargingRepository ucipChargingRepository;

    PostPaidOrPrePaidCheckService postPaidOrPrePaidCheckService = null;

    public AppResponse processReqeust(ChargeRequestDTO chargeRequestDTO) throws InterruptedException, ExecutionException, JSONException, IOException {

        //Mobilink Request
        if(chargeRequestDTO.getOperatorId() == 10 || chargeRequestDTO.getOperatorId() == 40){

            System.out.println("line 53 in charging request handler");
            postPaidOrPrePaidCheckService = new PostPaidOrPrePaidCheckService(chargeRequestDTO.getMsisdn(),chargeRequestDTO.getTransactionId());
            System.out.println("line 55");
            if(postPaidOrPrePaidCheckService.isPostPaid()){
                System.out.println("line 57");
               return postPaidResponse();
            }else {
//                try {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    Object object=null;
//                    try {
//                        ChargeRequestDTO chargeRequestDTOForQueue=new ChargeRequestDTO();
//                        chargeRequestDTO.setMsisdn( chargeRequestDTO.getMsisdn() );
//                        chargeRequestDTO.setAmount(chargeRequestDTO.getAmount());
//                        chargeRequestDTO.setTransactionId( chargeRequestDTO.getTransactionId() );
//
//
//                        object = objectMapper.writeValueAsString(chargeRequestDTOForQueue);
//                    } catch (JsonProcessingException e) {
//                        
//                    };
//                    ucipChargingRepository.save(chargeRequestDTOForQueue);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                ucipChargingService = new UCIPChargingService(chargeRequestDTO.getMsisdn(),
                        Double.parseDouble(chargeRequestDTO.getAmount("200")),
                        chargeRequestDTO.getTransactionId());
                HttpResponse httpResponse = ucipChargingService.sendRequest(false);
                return parseUcipResponse(httpResponse,chargeRequestDTO);
            }
        }else if(chargeRequestDTO.getOperatorId()== 50){
           // System.out.println("in zong request lie 84");
            AppResponse appResponse = sendZongMMRequest(chargeRequestDTO);
            return appResponse;
        }else {
            return null;
        }
    }

    private AppResponse parseUcipResponse(HttpResponse httpResponse,ChargeRequestDTO chargeRequestDTO) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        String xmlResponse = EntityUtils.toString(entity);
        String[] recArray = new String[2];
        recArray = xmlConversion(xmlResponse);

        int responseCode = -1;
        String transID = recArray[0]; // TransactionID
        EntityUtils.consume(entity);
        if (recArray[1] != null) {
            responseCode = Integer.valueOf(recArray[1]); // ResponseCode
        }
        //saveChargingRecord(responseCode,chargeRequestDTO);
        if(responseCode==0){
            return createChargingResponse(Constants.CHARGED_SUCCESSFUL,Constants.CHARGED_SUCCESSFUL_MSG,transID);
        }else {
            return createChargingResponse(Constants.INSUFFICIENT_BALANCE,Constants.INSUFFICIENT_BALANCE_MSG,transID);
        }
    }

    private AppResponse sendZongMMRequest(ChargeRequestDTO chargeRequestDTO){
      //  System.out.println("in request of zong line 113");
        String number = "";
        String serviceId = "";

        ZongMMLRequest zongMMLRequest = new ZongMMLRequest();
        if (chargeRequestDTO.getMsisdn().startsWith("92")) {
            number = chargeRequestDTO.getMsisdn();
        } else if (chargeRequestDTO.getMsisdn().startsWith("03")) {
            number = chargeRequestDTO.getMsisdn().replaceFirst("03", "92");
        } else if (chargeRequestDTO.getMsisdn().startsWith("3")) {
            number = "92" + chargeRequestDTO.getMsisdn();
        }
        System.out.println("Zong Request for number = " + number);
        System.out.println("Amount "+chargeRequestDTO.getAmount("200"));
       // System.out.println("zong requst line 127");
        Integer adjustmentAmountRelative = 0;
       // System.out.println("zong request line 129");

        Integer amount = Integer.valueOf(chargeRequestDTO.getAmount("200"));
        //System.out.println("zong request line 131");
        if (amount == 200) {
           // System.out.println("amount 200 line 133");
            adjustmentAmountRelative = 200;
            serviceId = "Noet01";
        } else if (amount == 500) {
            adjustmentAmountRelative = 500;
            serviceId = "Noet05";
        } else if (amount == 1000) {
            adjustmentAmountRelative = 1000;
            serviceId = "Noet10";
        }
        else if (amount == 2000) {
            adjustmentAmountRelative = 2000;
            serviceId = "Noet20";
        }
        else if (amount == 2500) {
            adjustmentAmountRelative = 2500;
            serviceId = "Noet25";
        }
        else if (amount == 1500) {
            adjustmentAmountRelative = 1500;
            serviceId = "Noet15";
        }
        else if (amount == 5000) {
            adjustmentAmountRelative = 5000;
            serviceId = "Noet50";
        }

        System.out.println("final Amount "+adjustmentAmountRelative);
       // System.out.println("line 162");
       // zongMMLRequest.logIn();
        //System.out.println("request handler line 164");
        String mmlResponse = zongMMLRequest.deductBalance(number, String.valueOf(adjustmentAmountRelative), serviceId);
        //System.out.println("after deduct balance");

        AppResponse     appResponse = parseMMLResponse(mmlResponse);
        //saveChargingRecord(appResponse.getCode(),chargeRequestDTO);
        return appResponse;
    }

    private AppResponse parseMMLResponse(String mmlResponse){

        //System.out.println("in parsemml response line 175");
        AppResponse appResponse = new AppResponse();
//        String[] res = mmlResponse.split("RETN=");
//        String[] codeArr = res[1].split(",");
//        String code = codeArr[0];
//        Scanner scanner= new Scanner(System.in);
//        System.out.println("enter 0000 for successful charging  " +"  enter 9999 for not allowed mcg" +"" +
//                "  enter 1001 for insufficient balance" +"  enter 1002 for  SUBS_DO_NOT_ALLOW");

//        int number = 44;
//        String size;

//        Random rand = new Random();
//        System.out.println("random function");
//         rand.nextInt(5);
//        rand.nextInt((max - min) + 1) + min;




        int random = (int) (Math.random() * 4 + 1);
        //System.out.println(random);

         if (random==1) {

            // System.out.println("if");
             String code = "0000";
             //System.out.println("printed");
             if (code.equalsIgnoreCase("0000")) {
                 appResponse.setCode(Constants.CHARGED_SUCCESSFUL);
                 appResponse.setMsg(Constants.CHARGED_SUCCESSFUL_MSG);
                 appResponse.setTransID("transId");
             }
         }

             else if(random==2){

                 String code1="9999";
              // System.out.println("printed");
                if (code1.equalsIgnoreCase("9999")) {
                    appResponse.setCode(Constants.NOT_ALLOWED);
                    appResponse.setMsg(Constants.NOT_ALLOWED_MSG);
                    appResponse.setTransID("transId");
                }
             }

             else if(random==3){

                 String code2="1001";
               // System.out.println("printed");
                if (code2.equalsIgnoreCase("1001")) {
                    appResponse.setCode(Constants.INSUFFICIENT_BALANCE);
                    appResponse.setMsg(Constants.INSUFFICIENT_BALANCE_MSG);
                    appResponse.setTransID("transId");
                }
             }
             else if (random==4){

                 String code3="103";
                //System.out.println("printed");

                if (code3.equalsIgnoreCase("103")) {
                    appResponse.setCode(Constants.IS_POSTPAID);
                    appResponse.setMsg(Constants.IS_POSTPAID_MSG);
                    appResponse.setTransID("transId");
                }
             }




        // switch statement to check size
//        switch () {
//
//
//            case 1:
//                String code = "0000";
//                System.out.println("printed");
//                if(code.equalsIgnoreCase("0000")) {
//                    appResponse.setCode(Constants.CHARGED_SUCCESSFUL);
//                    appResponse.setMsg(Constants.CHARGED_SUCCESSFUL_MSG);
//                    appResponse.setTransID("transId");
//                }
//                break;
//
//            case 2:
//                String code1="9999";
//                System.out.println("printed");
//                if (code1.equalsIgnoreCase("9999")) {
//                    appResponse.setCode(Constants.NOT_ALLOWED);
//                    appResponse.setMsg(Constants.NOT_ALLOWED_MSG);
//                    appResponse.setTransID("transId");
//                }
//                break;
//
//            // match the value of week
//            case 3:
//                String code2="1001";
//                System.out.println("printed");
//                if (code2.equalsIgnoreCase("1001")) {
//                    appResponse.setCode(Constants.INSUFFICIENT_BALANCE);
//                    appResponse.setMsg(Constants.INSUFFICIENT_BALANCE_MSG);
//                    appResponse.setTransID("transId");
//                }
//                break;
//
//            case 4:
//                String code3="1002";
//                System.out.println("printed");
//
//                if (code3.equalsIgnoreCase("1002")) {
//                    appResponse.setCode(Constants.SUBS_DO_NOT_ALLOW);
//                    appResponse.setMsg(Constants.SUBS_DO_NOT_ALLOW_MSG);
//                    appResponse.setTransID("transId");
//                }
//                break;
//
//            default:
//                String use="wrong entry";
//                break;
//
//        }



//        if (scanner.nextInt()==0000){
//       String code = "0000";
//        System.out.println("line 187 if condition");
//        if(code.equalsIgnoreCase("0000")) {
//            appResponse.setCode(Constants.CHARGED_SUCCESSFUL);
//            appResponse.setMsg(Constants.CHARGED_SUCCESSFUL_MSG);
//            appResponse.setTransID("transId");
//        }
//        }else if(scanner.nextInt()==9999) {
//            String code="9999";
//            System.out.println("printed");
//           if (code.equalsIgnoreCase("9999")) {
//                appResponse.setCode(Constants.NOT_ALLOWED);
//                appResponse.setMsg(Constants.NOT_ALLOWED_MSG);
//                appResponse.setTransID("transId");
//            }
//        }
//
//        else if(scanner.nextInt()==1001) {
//            String code="1001";
//            System.out.println("printed");
//            if (code.equalsIgnoreCase("1001")) {
//                appResponse.setCode(Constants.INSUFFICIENT_BALANCE);
//                appResponse.setMsg(Constants.INSUFFICIENT_BALANCE_MSG);
//                appResponse.setTransID("transId");
//            }
//        }
//
//        else if (scanner.nextInt()==1002) {
//            String code="1002";
//            System.out.println("printed");
//
//            if (code.equalsIgnoreCase("1002")) {
//                appResponse.setCode(Constants.SUBS_DO_NOT_ALLOW);
//                appResponse.setMsg(Constants.SUBS_DO_NOT_ALLOW_MSG);
//                appResponse.setTransID("transId");
//            }
//        }
//
////        else if (code.equalsIgnoreCase("1001")){
////            appResponse.setCode(Constants.INSUFFICIENT_BALANCE);
////            appResponse.setMsg(Constants.INSUFFICIENT_BALANCE_MSG);
////            appResponse.setTransID("transId");
////        }
////        else if(code.equalsIgnoreCase("1002")){
////            appResponse.setCode(Constants.SUBS_DO_NOT_ALLOW);
////            appResponse.setMsg(Constants.SUBS_DO_NOT_ALLOW_MSG);
////            appResponse.setTransID("transId");
////        }
        return appResponse;
    }

    //save record in db
//    private void saveChargingRecord(int ucipResponse,ChargeRequestDTO chargeRequestDTO){
//        UcipChargingEntity ucipChargingEntity = new UcipChargingEntity();
//        ucipChargingEntity.setAmount(chargeRequestDTO.getAmount());
//        ucipChargingEntity.setCdate(Timestamp.valueOf(LocalDateTime.now()));
//        ucipChargingEntity.setIsPostpaid(1);
//        ucipChargingEntity.setMsisdn(chargeRequestDTO.getMsisdn());
//        ucipChargingEntity.setUcipResponse(ucipResponse);
//        if(ucipResponse==0){
//            ucipChargingEntity.setIsCharged(1);
//        }else {
//            ucipChargingEntity.setIsCharged(0);
//        }
//
//       ucipChargingRepository.save(ucipChargingEntity);
//    }

    protected
    String[] xmlConversion(String xml) {
        String[] retArray = new String[2];
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));

            Document doc = docBuilder.parse(src);

            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfPersons = doc.getElementsByTagName("member");

            System.out.println(listOfPersons.getLength());

            if (listOfPersons.getLength() == 2) {

                Node firstPersonNode11 = listOfPersons.item(0);

                if (firstPersonNode11.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) firstPersonNode11;

                    retArray[0] = eElement.getElementsByTagName("value").item(0).getTextContent();
                }    //end of if clause

                // Return Response Code
                Node firstPersonNode22 = listOfPersons.item(1);
                if (firstPersonNode22.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstPersonElement22 = (Element) firstPersonNode22;

                    retArray[1] = firstPersonElement22.getElementsByTagName("value").item(0).getTextContent();
                } //end of if clause

            } else {
                //Return Transaction ID
                Node firstPersonNode = listOfPersons.item(16);
                if (firstPersonNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstPersonElement = (Element) firstPersonNode;

                    //-------
                    NodeList lastNameList = firstPersonElement.getElementsByTagName("string");
                    Element lastNameElement = (Element) lastNameList.item(0);

                    NodeList textLNList = lastNameElement.getChildNodes();
                    System.out.println("Para 1 Value : " + ((Node) textLNList.item(0)).getNodeValue().trim());
                    retArray[0] = ((Node) textLNList.item(0)).getNodeValue().trim();

                } //End Transaction IF
                //Return Response Code
                Node firstPersonNode1 = listOfPersons.item(17);
                if (firstPersonNode1.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstPersonElement1 = (Element) firstPersonNode1;

                    NodeList lastNameList = firstPersonElement1.getElementsByTagName("i4");
                    Element lastNameElement = (Element) lastNameList.item(0);

                    NodeList textLNList = lastNameElement.getChildNodes();
                    retArray[1] = ((Node) textLNList.item(0)).getNodeValue().trim();

                } //End Response Code IF
            }
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return retArray;

    }

    private AppResponse createChargingResponse(int code,String msg,String transID){
        AppResponse appResponse = new AppResponse();
        appResponse.setCode(code);
        appResponse.setMsg(msg);
        appResponse.setTransID(transID);
        return appResponse;
    }

    private AppResponse postPaidResponse(){
        AppResponse appResponse = new AppResponse();
        appResponse.setCode(Constants.IS_POSTPAID);
        appResponse.setMsg(Constants.IS_POSTPAID_MSG);
        appResponse.setTransID("trans_id");
        return appResponse;
    }
}
