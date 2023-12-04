package com.example.resourceserver.controllers.websockets;

import com.example.resourceserver.utils.CustomUserDetails;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {
    @MessageMapping("/ws_test")
    @SendTo("/ws_test")
    public String notification(@AuthenticationPrincipal CustomUserDetails userDetails) throws InterruptedException {
        Thread.sleep(1000);
        return "Hello from websocket " + userDetails.getUsername();
    }
}
