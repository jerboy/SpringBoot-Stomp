package com.jerboy.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

  @Autowired
  SimpUserRegistry userRegistry;

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @RequestMapping("/send")
  public void sendAll(){
    messagingTemplate.convertAndSend("/topic/follow", "Test Message");
  }


  @RequestMapping("/sendToUser")
  public void sendToUser(){
    messagingTemplate.convertAndSendToUser("user-demo", "/topic/follow", "Test Message");
  }
}
