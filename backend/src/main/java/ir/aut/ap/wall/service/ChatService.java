package ir.aut.ap.wall.service;

import ir.aut.ap.wall.dto.Dtos.*;
import ir.aut.ap.wall.exception.ApiException;
import ir.aut.ap.wall.model.Advertisement;
import ir.aut.ap.wall.model.Conversation;
import ir.aut.ap.wall.model.Message;
import ir.aut.ap.wall.model.User;
import ir.aut.ap.wall.repository.AdvertisementRepository;
import ir.aut.ap.wall.repository.ConversationRepository;
import ir.aut.ap.wall.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AdvertisementRepository advertisementRepository;

    public ChatService(ConversationRepository conversationRepository,
                       MessageRepository messageRepository,
                       AdvertisementRepository advertisementRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.advertisementRepository = advertisementRepository;
    }

    public ConversationDto start(User user, StartConversationRequest request) {
        Advertisement ad = advertisementRepository.findById(request.adId())
                .orElseThrow(() -> ApiException.notFound("آگهی پیدا نشد"));
        if (ad.getSeller().getId().equals(user.getId())) {
            throw ApiException.badRequest("نمی‌توانید با خودتان گفتگو کنید");
        }
        Conversation conversation = conversationRepository
                .findByAdvertisementIdAndBuyerId(ad.getId(), user.getId())
                .orElseGet(() -> conversationRepository.save(
                        new Conversation(ad, user, ad.getSeller())));
        return ConversationDto.of(conversation, unreadCount(conversation, user));
    }

    public List<ConversationDto> myConversations(User user) {
        List<Conversation> all = new ArrayList<>();
        all.addAll(conversationRepository.findByBuyerIdOrderByCreatedAtDesc(user.getId()));
        all.addAll(conversationRepository.findBySellerIdOrderByCreatedAtDesc(user.getId()));
        all.sort(Comparator.comparing(Conversation::getCreatedAt).reversed());
        return all.stream()
                .map(c -> ConversationDto.of(c, unreadCount(c, user)))
                .toList();
    }

    @Transactional
    public List<MessageDto> messages(User user, Long conversationId) {
        Conversation conversation = findAndCheckAccess(user, conversationId);
        List<Message> messages = messageRepository
                .findByConversationIdOrderBySentAtAsc(conversation.getId());
        for (Message m : messages) {
            if (!m.getSender().getId().equals(user.getId()) && !m.isReadByReceiver()) {
                m.setReadByReceiver(true);
                messageRepository.save(m);
            }
        }
        return messages.stream().map(MessageDto::of).toList();
    }

    public MessageDto send(User user, Long conversationId, MessageRequest request) {
        Conversation conversation = findAndCheckAccess(user, conversationId);
        Message message = new Message(conversation, user, request.content().trim());
        return MessageDto.of(messageRepository.save(message));
    }

    private Conversation findAndCheckAccess(User user, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> ApiException.notFound("گفتگو پیدا نشد"));
        boolean isParticipant = conversation.getBuyer().getId().equals(user.getId())
                || conversation.getSeller().getId().equals(user.getId());
        if (!isParticipant) {
            throw ApiException.forbidden("شما به این گفتگو دسترسی ندارید");
        }
        return conversation;
    }

    private long unreadCount(Conversation conversation, User user) {
        return messageRepository.countByConversationIdAndReadByReceiverFalseAndSenderIdNot(
                conversation.getId(), user.getId());
    }
}