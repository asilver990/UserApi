package com.usersApi.Users.Controller;

import com.usersApi.Users.BigQueryService;
import com.usersApi.Users.Configuration.BigQueryAppProperties;
import com.usersApi.Users.Model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private static final String ADD_USER   = "/users/add";
    private static final String GET_USER   = "/users";
    private static final String GET_USER_BY_DOCUMENT_TYPE  = "/users/{documentType}";
    private BigQueryService service = new BigQueryService(new BigQueryAppProperties());

    @RequestMapping(value = GET_USER, method = RequestMethod.GET)
    public Object getUsers() {
        try{
            return service.getUsers();
        }catch(Exception ex){
            return "There was a problem retrieving users.";
        }
    }

    @RequestMapping(value = ADD_USER, method = RequestMethod.POST)
    public Object addUser(@RequestBody User user) {
        try{
            service.addUser(user.getDocument(), user.getDocumentType(), user.getFirst_name(), user.getLast_name(), user.getEmail(),user.getGender());
            return "User inserted.";
        }catch(Exception ex){
            return "There was a problem inserting the user.";
        }
    }

    @RequestMapping(value = GET_USER_BY_DOCUMENT_TYPE, method = RequestMethod.GET)
    public Object getUserByDocumentType(@PathVariable String documentType) {
        try{
            return service.getUsers("0", documentType);
        }catch(Exception ex){
            return "There was a problem retrieving users by document Type.";
        }
    }
}
