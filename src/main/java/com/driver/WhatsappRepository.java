package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;
@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below-mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository() {
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String create_user(String name, String mobile) throws Exception {
            if (userMobile.contains(mobile)) {
                throw new Exception("User already exists");
            }
            userMobile.add(mobile);
            return "SUCCESS";
    }

    public Group create_group(List<User> users) {
        if (users.size() == 2) {
            Group group = new Group(users.get(1).getName(), 1);
            return group;
        }
        customGroupCount++;
        Group group = new Group("Group " + customGroupCount, users.size());
        groupUserMap.put(group, users);
        adminMap.put(group, users.get(0));
        return group;
    }

    public int create_message(String content) {
        messageId++;
        Message message = new Message(messageId,content);
        return messageId;
    }

    public int send_message(Message message, User sender, Group group) throws Exception {
        if (!groupUserMap.containsKey(group)) {
            throw new Exception("Group does not exist");
        }
        if (!userMobile.contains(sender.getMobile())) {
            throw new Exception("You are not allowed to send message");
        }
        List<Message> list = groupMessageMap.get(group);
        list.add(message);
        return list.size();
    }

    public String change_admin(User approver, User user, Group group) throws Exception {
        if (!groupUserMap.containsKey(group)) {
            throw new Exception("Group does not exist");
        }
        if (!adminMap.get(group).equals(approver)) {
            throw new Exception("Approver does not have rights");
        }
        boolean checkParticipant = false;
        List<User> list = groupUserMap.get(group);
        for (User users : list) {
            if (users.getName().equals(users)) {
                checkParticipant = true;
                break;
            }
        }
        if (!checkParticipant) {
            throw new Exception("User is not a participant");
        }
        adminMap.put(group, user);
        return "SUCCESS";
    }

    public int remove_user(User user) throws Exception {
        boolean isUserPresent = false;
       Group userGroup = new Group();
        for(Group group: groupUserMap.keySet()){
            List<User> list = groupUserMap.get(group);
            for(int i =0;i<list.size();i++){
               User user1 = list.get(i);
                if(user1.equals(user)){
                    isUserPresent = true;
                    userGroup = group;
                    // if user is admin
                    if(adminMap.get(group).equals(user)){
                        throw new Exception("Cannot remove admin");
                    }
                    // removing user from the list
                    list.remove(i);

                    // removing message of that user from that group(from groupMessageMap)
                    List<Message> messageList = groupMessageMap.get(group);
                    for(int j =0;j<messageList.size();j++){
                        Message message = messageList.get(j);
                        if(senderMap.get(message).equals(user)){
                            messageList.remove(j);
                        }
                    }

                    // remove message of that user from the senderMap
                    for(Message message: senderMap.keySet()){
                        if(senderMap.get(message).equals(user)){
                            senderMap.remove(message);
                        }
                    }
                    break;
                }
            }
            if(isUserPresent){
                break;
            }
        }
        if(!isUserPresent){
            throw new Exception("User not found");
        }

       // the updated number of users in the group
        int updatedUsers = groupUserMap.get(userGroup).size();

       // the updated number of messages in group
        int updatedMessages = groupMessageMap.get(userGroup).size();

        // the updated number of overall messages
        int overallMessages = senderMap.size();
        return updatedUsers+updatedMessages+overallMessages;
    }

    public String find_message(Date start, Date end, int K) throws Exception {
        PriorityQueue<Message> pq = new PriorityQueue<>();
        for(Message message: senderMap.keySet()){
            if(message.getTimestamp().compareTo(start)>0 && message.getTimestamp().compareTo(end)<0){
                pq.add(message);
            }
        }
        if(pq.size()<K){
            throw new Exception("K is greater than the number of messages");
        }
        while(pq.size()>K){
            pq.remove();
        }
        return pq.peek().getContent() ;
    }
}

