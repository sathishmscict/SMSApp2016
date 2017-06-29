package com.tech9teen.pojo;

/**
 * Created by Satish Gadde on 14-10-2016.
 */

public class UploadDocuments {


    String status;
    String senderid;



    String routetype;
    String documentid;
    String approvaldate;
    String requestdate;


    public UploadDocuments(String status, String senderid, String type, String documentid, String approvaldate, String requestdate) {
        this.status = status;
        this.senderid = senderid;
        this.routetype = type;
        this.documentid = documentid;
        this.approvaldate = approvaldate;
        this.requestdate = requestdate;
    }


    public String getRoutetype() {
        return routetype;
    }

    public void setRoutetype(String routetype) {
        this.routetype = routetype;
    }


    public String getRequestdate() {
        return requestdate;
    }

    public void setRequestdate(String requestdate) {
        this.requestdate = requestdate;
    }

    public String getApprovaldate() {
        return approvaldate;
    }

    public void setApprovaldate(String approvaldate) {
        this.approvaldate = approvaldate;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }


    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }







}
