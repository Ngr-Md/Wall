package ir.aut.ap.wall.controller;

import ir.aut.ap.wall.dto.Dtos.*;
import ir.aut.ap.wall.model.User;
import ir.aut.ap.wall.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ConversationDto> start(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody StartConversationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.start(user, request));
    }

    @GetMapping
    public List<ConversationDto> myConversations(@AuthenticationPrincipal User user) {
        return chatService.myConversations(user);
    }

    @GetMapping("/{id}/messages")
    public List<MessageDto> messages(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return chatService.messages(user, id);
    }

    @PostMapping("/{id}/messages")
    public MessageDto send(@AuthenticationPrincipal User user,
                           @PathVariable Long id,
                           @Valid @RequestBody MessageRequest request) {
        return chatService.send(user, id, request);
    }
}