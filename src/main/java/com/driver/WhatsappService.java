package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {


    WhatsappRepository repository = new WhatsappRepository();
    public String  createUser (String name, String mobile) throws Exception{
          return   repository.create_user(name, mobile);
        }
    public Group  createGroup (List<User> users){
       return repository.create_group(users);
    }

    public int createMessage(String content){

        return repository.create_message(content);
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{

        return repository.send_message(message, sender, group);
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{

        return repository.change_admin(approver, user, group);
    }

    public int removeUser(User user) throws Exception{
        return repository.remove_user(user);
    }

    public String findMessage(Date start, Date end, int K) throws Exception{
        return repository.find_message(start, end, K);
 }
}
