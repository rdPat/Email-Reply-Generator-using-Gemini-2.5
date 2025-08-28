package com.smartreply.controller;

import com.smartreply.model.EmailRequest;
import com.smartreply.view.SmartEmailReplyService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/smart/reply")
@AllArgsConstructor
public class SmartEmailReplyController
{

    private  final SmartEmailReplyService smartEmailReplyService;

    //below api is used to get email reply using google gemini 2.5 Pro api
    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest)
    {
        String reply = smartEmailReplyService.generateEmailReply(emailRequest);
        return ResponseEntity.ok(reply);
    }


}
