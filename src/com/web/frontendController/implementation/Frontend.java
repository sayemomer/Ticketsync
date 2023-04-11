package com.web.frontendController.implementation;

import FrontEnd.MyRequest;
import FrontEnd.RmResponse;
import com.web.frontendController.FEInterface;
import com.web.webcontroller.ControllerInterface;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static FrontEnd.FE.ANSI_RESET;
import static FrontEnd.FE.sendUnicastToSequencer;


@WebService(endpointInterface = "com.web.frontendController.FEInterface")
@SOAPBinding(style = SOAPBinding.Style.RPC)

public class Frontend implements FEInterface {
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";

    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static long DYNAMIC_TIMEOUT = 10000;
    private static int Rm1BugCount = 0;
    private static int Rm2BugCount = 0;
    private static int Rm3BugCount = 0;

    private static int Rm4BugCount = 0;
    private static int Rm1NoResponseCount = 0;
    private static int Rm2NoResponseCount = 0;
    private static int Rm3NoResponseCount = 0;

    private static int Rm4NoResponseCount = 0;
    private final String serverID;
    private final String serverName;
    private long responseTime = DYNAMIC_TIMEOUT;
    private long startTime;
    private CountDownLatch latch;
//    private final FEInterface inter;
    private final List<RmResponse> responses = new ArrayList<>();
//    private ORB orb;

    public Frontend(String serverID,String serverName) {
        super();
        this.serverID = serverID;
        this.serverName = serverName;
    }


    @Override
    public synchronized String  addMovie(String managerID, String movieID, String movieType, int bookingCapacity) {

        MyRequest myRequest = new MyRequest("addMovie", managerID);
        myRequest.setMovieID(movieID);
        myRequest.setMovieType(movieType);
        myRequest.setBookingCapacity(bookingCapacity);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));

        //Message format 0;LOCALHOST;00;ADDMOVIE;ATWA2345;ATWM190120;AVATAR;NULL;NULL;1
        System.out.println("inside FE Implementation:addEvent ---> " + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public synchronized String removeMovie(String managerID, String movieID, String movieType) {

        MyRequest myRequest = new MyRequest("removeMovie", managerID);
        myRequest.setMovieID(movieID);
        myRequest.setMovieType(movieType);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:removeEvent ---> " + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public synchronized String listMovieAvailability(String managerID, String eventType) {

//        MyRequest myRequest = new MyRequest("listEventAvailability", managerID);
//        myRequest.setEventType(eventType);
//        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
//        System.out.println("FE Implementation:listEventAvailability>>>" + myRequest.toString());
//        return validateResponses(myRequest);
        return "list of movies";
    }

    @Override
    public synchronized String bookMovie(String customerID, String movieID, String movieType) {

        MyRequest myRequest = new MyRequest("bookMovie", customerID);
        myRequest.setMovieID(movieID);
        myRequest.setMovieType(movieType);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:bookEvent ---> " + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public synchronized String getBookingSchedule(String customerID) {

        MyRequest myRequest = new MyRequest("getBookingSchedule", customerID);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:getBookingSchedule ---> " + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public synchronized String cancelMovie(String customerID, String movieID, String movieType) {

        MyRequest myRequest = new MyRequest("cancelBooking", customerID);
        myRequest.setMovieID(movieID);
        myRequest.setMovieType(movieType);
        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
        System.out.println("FE Implementation:cancelEvent ---> " + myRequest.toString());
        return validateResponses(myRequest);
    }

    @Override
    public synchronized String swapMovie(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) {

        System.out.println("in frontend swapping movie...");
//        MyRequest myRequest = new MyRequest("swapEvent", customerID);
//        myRequest.setEventID(newEventID);
//        myRequest.setEventType(newEventType);
//        myRequest.setOldEventID(oldEventID);
//        myRequest.setOldEventType(oldEventType);
//        myRequest.setSequenceNumber(sendUdpUnicastToSequencer(myRequest));
//        System.out.println("FE Implementation:swapEvent>>>" + myRequest.toString());
//        return validateResponses(myRequest);
        System.out.println("swapping movie.....");
        return "movie swapped";
    }

    public void waitForResponse() {
        try {
            System.out.println(ANSI_BLUE_BACKGROUND +"waiting For Response.... ResponsesRemain" + ANSI_RESET + latch.getCount());
            boolean timeoutReached = latch.await(DYNAMIC_TIMEOUT, TimeUnit.MILLISECONDS);
            if (timeoutReached) {
                setDynamicTimout();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String validateResponses(MyRequest myRequest) {
        String resp;
        switch ((int) latch.getCount()) {
            case 0:
            case 1:
            case 2:
            case 3:
                resp = findMajorityResponse(myRequest);
                break;
            case 4:
                resp = "Fail: No response from any server";
                System.out.println(ANSI_RED_BACKGROUND + resp + ANSI_RESET);
                if (myRequest.haveRetries()) {
                    myRequest.countRetry();
//                    resp = retryRequest(myRequest);
                    resp = "retrying request";
                }
                rmDown(1);
                rmDown(2);
                rmDown(3);
                break;
            default:
                resp = "Fail: " + myRequest.noRequestSendError();
                break;
        }
        System.out.println(ANSI_GREEN_BACKGROUND +"Frontend validating Responses ::: Responses remain:" +ANSI_RESET+ latch.getCount() + " Response to be sent to client ---> " + resp);
        return resp;
    }

    private String findMajorityResponse(MyRequest myRequest) {
        RmResponse res1 = null;
        RmResponse res2 = null;
        RmResponse res3 = null;
        RmResponse res4 = null;

        System.out.println(responses.size());
        for (RmResponse response :
                responses) {
            if (response.getSequenceID() == myRequest.getSequenceNumber()) {
                switch (response.getRmNumber()) {
                    case 1:
                        res1 = response;
                        break;
                    case 2:
                        res2 = response;
                        break;
                    case 3:
                        res3 = response;
                        System.out.println(ANSI_BLUE_BACKGROUND + "RM4 response received" + res3.getResponse() + ANSI_RESET);
                        break;
                    case 4:
                        res4 = response;
                        break;
                }
            }
        }
        System.out.println("FE finding Majority Response <--- RM1" + ((res1 != null) ? res1.getResponse() : "null"));
        System.out.println("FE finding Majority Response <--- RM2" + ((res2 != null) ? res2.getResponse() : "null"));
        System.out.println("FE finding Majority Response <--- RM3" + ((res3 != null) ? res3.getResponse() : "null"));
        System.out.println("FE finding Majority Response <--- RM4" + ((res4 != null) ? res4.getResponse() : "null"));

        if (res1 == null) {
            rmDown(1);
        } else {
            Rm1NoResponseCount = 0;
            if (res1.equals(res2) && res1.equals(res3)) {
                if (res1.equals(res4) || res4 == null) {
                    return res1.getResponse();
                } else {
                    rmBugFound(4);
                }
            } else if (res1.equals(res2) && res1.equals(res4)) {
                if (res1.equals(res3) || res3 == null) {
                    return res1.getResponse();
                } else {
                    rmBugFound(3);
                }
            } else if (res1.equals(res3) && res1.equals(res4)) {
                if (res1.equals(res2) || res2 == null) {
                    return res1.getResponse();
                } else {
                    rmBugFound(2);
                }
            } else if (res2 == null && res3 == null && res4 == null) {
                return res1.getResponse();
            } else {
                rmBugFound(1);
            }
        }

        if (res2 == null) {
            rmDown(2);
        } else {
            Rm2NoResponseCount = 0;
            if (res2.equals(res1) && res2.equals(res3)) {
                if (res2.equals(res4) || res4 == null) {
                    return res2.getResponse();
                } else {
                    rmBugFound(4);
                }
            } else if (res2.equals(res1) && res2.equals(res4)) {
                if (res2.equals(res3) || res3 == null) {
                    return res2.getResponse();
                } else {
                    rmBugFound(3);
                }
            } else if (res2.equals(res3) && res2.equals(res4)) {
                if (res2.equals(res1) || res1 == null) {
                    return res2.getResponse();
                } else {
                    rmBugFound(1);
                }
            } else if (res1 == null && res3 == null && res4 == null) {
                return res2.getResponse();
            } else {
                rmBugFound(2);
            }
        }

        if (res3 == null) {
            rmDown(3);
        } else {
            Rm3NoResponseCount = 0;
            if (res3.equals(res1) && res3.equals(res2)) {
                if (res3.equals(res4) || res4 == null) {
                    return res3.getResponse();
                } else {
                    rmBugFound(4);
                }
            } else if (res3.equals(res1) && res3.equals(res4)) {
                if (res3.equals(res2) || res2 == null) {
                    return res3.getResponse();
                } else {
                    rmBugFound(2);
                }
            } else if (res3.equals(res2) && res3.equals(res4)) {
                if (res3.equals(res1) || res1 == null) {
                    return res3.getResponse();
                } else {
                    rmBugFound(1);
                }
            } else if (res1 == null && res2 == null && res4 == null) {
                return res3.getResponse();
            } else {
                rmBugFound(3);
            }
        }
        if (res4 == null) {
            rmDown(4);
        } else {
            Rm4NoResponseCount = 0;
            if (res4.equals(res1) && res4.equals(res2)) {
                if (res4.equals(res3) || res3 == null) {
                    return res4.getResponse();
                } else {
                    rmBugFound(3);
                }
            } else if (res4.equals(res1) && res4.equals(res3)) {
                if (res4.equals(res2) || res2 == null) {
                    return res4.getResponse();
                } else {
                    rmBugFound(2);
                }
            } else if (res4.equals(res2) && res4.equals(res3)) {
                if (res4.equals(res1) || res1 == null) {
                    return res4.getResponse();
                } else {
                    rmBugFound(1);
                }
            } else if (res1 == null && res2 == null && res3 == null) {
                return res4.getResponse();
            } else {
                rmBugFound(4);
            }
        }



        return "!! Fail: majority response not found";
    }

    private void rmBugFound(int rmNumber) {
        switch (rmNumber) {
            case 1:
                Rm1BugCount++;
                if (Rm1BugCount == 3) {
                    Rm1BugCount = 0;
                    informRmHasBug(rmNumber);
                }
                break;
            case 2:
                Rm2BugCount++;
                if (Rm2BugCount == 3) {
                    Rm2BugCount = 0;
                    informRmHasBug(rmNumber);
                }
                break;

            case 3:
                Rm3BugCount++;
                if (Rm3BugCount == 3) {
                    Rm3BugCount = 0;
                    informRmHasBug(rmNumber);
                }
                break;
            case 4:
                Rm4BugCount++;
                if (Rm4BugCount == 3) {
                    Rm4BugCount = 0;
                    informRmHasBug(rmNumber);
                }
                break;
        }
        System.out.println("FE rmBugFound on ---> RM1 - bugs:" + Rm1BugCount);
        System.out.println("FE rmBugFound on ---> RM2 - bugs:" + Rm2BugCount);
        System.out.println("FE rmBugFound on ---> RM3 - bugs:" + Rm3BugCount);
        System.out.println("FE rmBugFound on ---> RM4 - bugs:" + Rm4BugCount);
    }

    private void rmDown(int rmNumber) {
        DYNAMIC_TIMEOUT = 10000;
        switch (rmNumber) {
            case 1:
                Rm1NoResponseCount++;
                if (Rm1NoResponseCount == 3) {
                    Rm1NoResponseCount = 0;
                    informRmIsDown(rmNumber);
                }
                break;
            case 2:
                Rm2NoResponseCount++;
                if (Rm2NoResponseCount == 3) {
                    Rm2NoResponseCount = 0;
                    informRmIsDown(rmNumber);
                }
                break;

            case 3:
                Rm3NoResponseCount++;
                if (Rm3NoResponseCount == 3) {
                    Rm3NoResponseCount = 0;
                    informRmIsDown(rmNumber);
                }
                break;
            case 4:
                Rm4NoResponseCount++;
                if (Rm4NoResponseCount == 3) {
                    Rm4NoResponseCount = 0;
                    informRmIsDown(rmNumber);
                }
        }
        System.out.println("FE rmDown ---> RM1 - noResponse:" + Rm1NoResponseCount);
        System.out.println("FE rmDown ---> RM2 - noResponse:" + Rm2NoResponseCount);
        System.out.println("FE rmDown ---> RM3 - noResponse:" + Rm3NoResponseCount);
        System.out.println("FE rmDown ---> RM4 - noResponse:" + Rm4NoResponseCount);

        return;
    }

    private void setDynamicTimout() {
        if (responseTime < 4000) {
            DYNAMIC_TIMEOUT = (DYNAMIC_TIMEOUT + (responseTime * 3)) / 2;
//            System.out.println("FE Implementation:setDynamicTimout>>>" + responseTime * 2);
        } else {
            DYNAMIC_TIMEOUT = 10000;
        }
        System.out.println("Current dynamic Timeout>>>" + DYNAMIC_TIMEOUT);
    }

    private void notifyOKCommandReceived() {
        latch.countDown();
        System.out.println("FE Received Response : Remaining responses" + latch.getCount());
    }

    public void addReceivedResponse(RmResponse res) {
        long endTime = System.nanoTime();
        responseTime = (endTime - startTime) / 1000000;
        System.out.println("Current Response time is: " + responseTime);
        responses.add(res);
        notifyOKCommandReceived();
    }

    private int sendUdpUnicastToSequencer(MyRequest myRequest) {
        startTime = System.nanoTime();
        int sequenceNumber = sendRequestToSequencer(myRequest);
        myRequest.setSequenceNumber(sequenceNumber);
        latch = new CountDownLatch(4);
        waitForResponse();
        return sequenceNumber;
    }

    @Override
    public void informRmHasBug(int RmNumber) {

        System.out.println( ANSI_RED_BACKGROUND + "FE is informing RmHasBug>>>RM" + RmNumber + " has a bug" + ANSI_RESET);

    }

    @Override
    public void informRmIsDown(int RmNumber) {

        System.out.println( ANSI_RED_BACKGROUND + "FE is informing RmIsDown>>>RM" + RmNumber + " is down" + ANSI_RESET);

    }

//    @Override
//    public void retryRequest(MyRequest myRequest) {
//        System.out.println("No response from all Rms, Retrying request...");
////        sendUnicastToSequencer(myRequest);
//    }

    @Override
    public int sendRequestToSequencer(MyRequest myRequest) {
        return sendUnicastToSequencer(myRequest);
    }


    //Change it accordign
    public String retryRequest(MyRequest myRequest) {
        System.out.println("FE Implementation:retryRequest>>>" + myRequest.toString());
        startTime = System.nanoTime();
        retryRequest(myRequest);
        latch = new CountDownLatch(4);
        waitForResponse();
        return validateResponses(myRequest);
    }
}