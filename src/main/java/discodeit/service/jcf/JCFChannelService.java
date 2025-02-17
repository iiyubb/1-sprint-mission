//package discodeit.service.jcf;
//
//import discodeit.entity.Channel;
//import discodeit.entity.ChannelType;
//import discodeit.entity.Message;
//import discodeit.entity.User;
//import discodeit.service.ChannelService;
//import discodeit.service.MessageService;
//import discodeit.service.UserService;
//import discodeit.utils.FileUtil;
//
//import java.util.*;
//
//public class JCFChannelService implements ChannelService {
//    private static Map<String, Channel> channelData;
//    private UserService userService;
//
//    public JCFChannelService(UserService userService) {
//        channelData = new HashMap<>();
//        this.userService = userService;
//    }
//
//    @Override
//    public Channel create(String name, ChannelType type, String description) {
//        Channel channel = new Channel(name, type, description);
//        channelData.put(channel.getId().toString(), channel);
//        return channel;
//    }
//
//    @Override
//    public Channel find(UUID channelId) {
//        try {
//            return channelData.get(channelId.toString());
//        } catch(Exception e) {
//            throw new NoSuchElementException("[error] 존재하지 않는 채널 ID입니다.");
//        }
//    }
//
//    @Override
//    public List<Channel> findAll() {
//        return channelData.values().stream().toList();
//    }
//
//    @Override
//    public Channel update(UUID channelId, String newName, String newDescription) {
//        Channel channel = channelData.get(channelId.toString());
//
//        if (isChannelNameDuplicate(newName)) {
//            throw new NoSuchElementException("[error] 이미 존재하는 채널 이름입니다.");
//        }
//
//        channel.update(newName, newDescription);
//        channelData.put(channel.getId().toString(), channel);
//        return channel;
//    }
//
//    @Override
//    public void delete(UUID channelId) {
//        channelData.remove(channelData.toString());
//    }
//
//    @Override
//    public void addUser(UUID channelId, UUID userId) {
//        Channel channel = channelData.get(channelId.toString());
//        try {
//            User user = userService.find(userId);
//            channel.addUser(user);
//            System.out.println("[User 추가 성공]");
//        } catch (Exception e) {
//            throw new NoSuchElementException("[error] 존재하지 않는 user입니다.");
//        }
//
//        if (isUserDuplicate(channel, userId)) {
//            throw new NoSuchElementException("[error] 이미 존재하는 user입니다.");
//        }
//
//        channelData.put(channelId.toString(), channel);
//    }
//
//    @Override
//    public List<User> findUsers(UUID channelId) {
//        Channel channel = channelData.get(channelId.toString());
//        return channel.getUsers().values().stream().toList();
//    }
//
//    @Override
//    public void deleteUser(UUID channelId, User user) {
//        Channel channel = channelData.get(channelId.toString());
//
//        if (!isUserDuplicate(channel, user.getId())) {
//            throw new NoSuchElementException("[error] 존재하지 않는 채널 user입니다.");
//        }
//        channel.getUsers().remove(user.getId());
//        System.out.println("[User 삭제 완료]");
//        channelData.put(channelId.toString(), channel);
//    }
//
//
//    private boolean isChannelNameDuplicate(String channelName) {
//        return channelData.values().stream().anyMatch(channel -> channel.getChannelName().equals(channelName));
//    }
//
//    private boolean isUserDuplicate(Channel channel, UUID userId) {
//        return channel.getUser(userId) != null;
//    }
//}
