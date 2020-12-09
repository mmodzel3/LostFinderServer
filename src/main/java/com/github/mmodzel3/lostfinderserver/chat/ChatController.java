package com.github.mmodzel3.lostfinderserver.chat;

import com.github.mmodzel3.lostfinderserver.location.Location;
import com.github.mmodzel3.lostfinderserver.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class ChatController {

    final private ChatService chatService;

    ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/api/chat")
    List<ChatMessage> getMessages(@RequestParam int page, @RequestParam(defaultValue = "10") int pageSize) {
        return chatService.getMessages(page, pageSize);
    }

    @PostMapping("/api/chat")
    ChatMessage sendMessage(@AuthenticationPrincipal Authentication authentication,
                           @RequestBody ChatUserMessage message) {
        User user = (User) authentication.getPrincipal();
        return chatService.sendMessage(user, message);
    }
}
