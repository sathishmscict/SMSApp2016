package com.tech9teen.pojo;

/**
 * Created by Satish Gadde on 13-09-2016.
 */
public class MessageTemplate {


    String templateTitle, templateDescr;


    int templateId;


    public MessageTemplate(int templateId, String templateTitle, String templateDescr) {
        this.templateTitle = templateTitle;
        this.templateDescr = templateDescr;
        this.templateId = templateId;

    }


    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }


    public String getTemplateTitle() {
        return templateTitle;
    }

    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    public String getTemplateDescr() {
        return templateDescr;
    }

    public void setTemplateDescr(String templateDescr) {
        this.templateDescr = templateDescr;
    }


}


