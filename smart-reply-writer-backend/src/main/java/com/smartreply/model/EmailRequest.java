package com.smartreply.model;

import lombok.Data;

@Data
public class EmailRequest {
    //below is the structure of data sended by user to api
    private String emailContent;
    private String tone;

}
