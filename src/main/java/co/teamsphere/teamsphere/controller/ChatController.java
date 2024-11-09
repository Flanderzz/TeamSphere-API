package co.teamsphere.teamsphere.controller;

import co.teamsphere.teamsphere.DTO.ChatDTO;
import co.teamsphere.teamsphere.DTO.ChatSummaryDTO;
import co.teamsphere.teamsphere.DTOmapper.ChatDTOMapper;
import co.teamsphere.teamsphere.exception.ChatException;
import co.teamsphere.teamsphere.exception.UserException;
import co.teamsphere.teamsphere.models.Chat;
import co.teamsphere.teamsphere.models.User;
import co.teamsphere.teamsphere.request.GroupChatRequest;
import co.teamsphere.teamsphere.request.RenameGroupChatRequest;
import co.teamsphere.teamsphere.request.SingleChatRequest;
import co.teamsphere.teamsphere.services.ChatService;
import co.teamsphere.teamsphere.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    private final UserService userService;

    private final ChatDTOMapper chatDTOMapper;

    public ChatController(ChatService chatService, UserService userService, ChatDTOMapper chatDTOMapper) {
        this.chatService = chatService;
        this.userService = userService;
        this.chatDTOMapper = chatDTOMapper;
    }

    @PostMapping("/single")
    public ResponseEntity<ChatDTO> creatChatHandler(@RequestBody SingleChatRequest singleChatRequest, @RequestHeader("Authorization")  String jwt) throws UserException {

        log.info("single chat --------");
        User reqUser = userService.findUserProfile(jwt);

        Chat chat = chatService.createChat(reqUser.getId(),singleChatRequest.getUserId(),false);
        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);
        return new ResponseEntity<>(chatDto, HttpStatus.OK);
    }

    @PostMapping("/group")
    public ResponseEntity<ChatDTO> createGroupHandler(@RequestBody GroupChatRequest groupChatRequest, @RequestHeader("Authorization") String jwt) throws UserException{

        User reqUser = userService.findUserProfile(jwt);

        Chat chat = chatService.createGroup(groupChatRequest, reqUser.getId());
        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);

    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDTO> findChatByIdHandler(@PathVariable UUID chatId) throws ChatException {

        Chat chat = chatService.findChatById(chatId);

        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);

    }

    @PutMapping("/{chatId}/add/{userId}")
    public ResponseEntity<ChatDTO> addUserToGroupHandler(@PathVariable UUID chatId,@PathVariable UUID userId) throws UserException, ChatException{


        Chat chat = chatService.addUserToGroup(userId, chatId);

        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);
    }

    @PutMapping("/{chatId}/rename")
    public ResponseEntity<ChatDTO> renameGroupHandler(@PathVariable UUID chatId, @RequestBody RenameGroupChatRequest renameGroupRequest, @RequestHeader("Authorization") String jwt) throws ChatException, UserException{

        User reqUser = userService.findUserProfile(jwt);

        Chat chat = chatService.renameGroup(chatId, renameGroupRequest.getGroupName(), reqUser.getId());

        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);
    }

    @PutMapping("/{chatId}/remove/{userId}")
    public ResponseEntity<ChatDTO> removeFromGroupHandler(@RequestHeader("Authorization") String jwt, @PathVariable UUID chatId,@PathVariable UUID userId) throws UserException, ChatException{


        User reqUser=userService.findUserProfile(jwt);

        Chat chat = chatService.removeFromGroup(chatId, userId, reqUser.getId());

        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{chatId}/{userId}")
    public ResponseEntity<ChatDTO> deleteChatHandler(@PathVariable UUID chatId, @PathVariable UUID userId) throws ChatException, UserException{

        Chat chat = chatService.deleteChat(chatId, userId);
        ChatDTO chatDto = chatDTOMapper.toChatDto(chat);

        return new ResponseEntity<>(chatDto, HttpStatus.OK);
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<ChatSummaryDTO>> getChatSummariesHandler(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws ChatException {
        try {
            log.info("Fetching chat summaries for user");

            User user = userService.findUserProfile(jwt);

            // Fetch chat summaries with pagination
            List<ChatSummaryDTO> chatSummaries = chatService.getChatSummaries(user.getId(), page, size);

            log.info("Retrieved {} chat summaries for user ID: {}", chatSummaries.size(), user.getId());
            return new ResponseEntity<>(chatSummaries, HttpStatus.OK);
        } catch (ChatException e) {
            log.error("User error fetching chat summaries: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error fetching chat summaries", e);
            throw new ChatException("Error fetching chat summaries: " + e.getMessage());
        }
    }

}
