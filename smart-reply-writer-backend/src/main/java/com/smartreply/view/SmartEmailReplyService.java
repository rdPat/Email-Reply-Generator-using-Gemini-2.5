package com.smartreply.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartreply.model.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SmartEmailReplyService {
    //using web client for making http request to other services or apis in SPRING WEBFLUX
    private final WebClient webClient;

    public SmartEmailReplyService(
            WebClient.Builder webclientbuilder,
            @Value("${gemini.api.url}") String baseUrl,
            @Value("${gemini.api.key}") String geminiApiKey
            ) {
        this.webClient = webclientbuilder.baseUrl(baseUrl).build();
        this.apiKey = geminiApiKey;
    }

    private final String apiKey;

    //below method responsible for generating email reply
    public String generateEmailReply(EmailRequest emailRequest)
    {
         //build prompt : building request to send to chatgpt
         String prompt=buildPrompt(emailRequest);

        //prepare raw json body
        String requestBody=String.format("""
                {    
                	"contents": [      
                		{       
                			"parts":
                			[         
                				{            
                					"text": "%s"
                				}        
                			]      
                		}    
                	]
                }
                """,prompt);

        //send request to Gemini API using web client which gives you response in json format

        String response=webClient.post().uri(uriBuilder ->uriBuilder
                .path("/v1beta/models/gemini-2.5-flash:generateContent").build()
                )
                .header("X-goog-api-key",apiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //extract response
        //after getting json response extract text or reply from it
        return extractResponseContent(response);

    }

    private String extractResponseContent(String response)
    {
        //below code is used for navigating thorugh response json given by gemini model
        try
        {
            //below we used object mapper to get only "text" entity from json
            ObjectMapper mapper=new ObjectMapper();
            JsonNode root=mapper.readTree(response);
            return root.path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private String buildPrompt(EmailRequest emailRequest)
    {
        //below creating request /prompt which will be sent to gemini api
        StringBuilder prompt=new StringBuilder();
        prompt.append("Generate a professional reply for following email:");
        //validating that email tone should be given
        if(emailRequest.getTone()!=null && !emailRequest.getTone().isEmpty())
        {
            prompt.append("Use a").append(emailRequest.getTone()).append("tone for generating reply");

        }
        prompt.append("Original Email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }


}
