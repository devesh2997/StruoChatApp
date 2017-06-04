package com.neutrinostruo.struochatapp;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

public class chatPacket{
    private String messageReceiverID;
    private String messageSenderID;
    private String message;
    /*private String chatId;*/
    private Calendar calendar = Calendar.getInstance();
    private String chatDate;


    public chatPacket(){

    }
    public chatPacket(String messageReceiverID, String messageSenderID, String message, /*String chatId,*/
                      String chatDate){
        this.messageReceiverID = messageReceiverID;
        this.messageSenderID = messageSenderID;
        this.message = message;
        /*this.chatId = chatId;*/
        this.calendar = calendar;
        this.chatDate = chatDate;
    }


    public String getMessageReceiverID(){
        return messageReceiverID;
    }

    public String getMessageSenderID(){
        return messageSenderID;
    }

    public String getMessage(){
        return message;
    }

  /*  public String getChatId(){
        return chatId;
    }*/

    public String getChatDate(){
        return chatDate;
    }
}
