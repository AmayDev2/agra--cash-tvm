package com.amay.tom.service.userauth;

import com.amay.tom.config.PasswordService;
import com.amay.tom.config.SystemConfig;
import com.amay.tom.exceptions.UsernameNotFoundException;
import com.amay.tom.model.ccuRest.Role;
import com.amay.tom.model.user.entity.User;
import com.amay.tom.model.user.entity.UserPrivilege;
import com.amay.tom.service.userauth.UserDetailsService;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class UserAuth {

    private final UserDetailsService userDetailsService;
    @Getter
    private User currentUser;

    public UserAuth(UserDetailsService userDetailsService) {
        this.userDetailsService=userDetailsService;
    }

    public UserPrivilege login(String username, String password) throws Exception   {

        String userName= "TVM"+ SystemConfig.getInstance().getCurrentEquipment().getEquipmentId();
        String userPass="tvm_user";
        if(username.equals(userName) && password.equals(userPass)){
            UserPrivilege userPrivilege= new UserPrivilege();
//            userPrivilege.setTvm(true);
//            Set<String> roles= new HashSet<>();
//            roles.add(Role.OPERATOR.name());
//            currentUser= new User().setRoles(roles);
//            currentUser.setUsername(username);
            return userPrivilege;
        }

        currentUser = new User();
        User user = userDetailsService.loadUserByUsername(username);
        PasswordService passwordService = new PasswordService();

        boolean isMatch = passwordService.verifyPassword(user.getPassword(), password);
        //System.out.println("Password Match: " + isMatch);
        if (isMatch) {
            currentUser = user;

           //System.out.println("User Privilege: "+user.toString());
            UserPrivilege userPrivilege= userDetailsService.loadUserPrivilege(username);
//            if(userPrivilege.isQrFreeTicket()  || userPrivilege.isQrPaidTicket() || userPrivilege.isQrTicketAdjustment()
//            || userPrivilege.isQrTicketAnalysis() || userPrivilege.isQrTicketIssue() || userPrivilege.isQrTicketCancellation()
//            || userPrivilege.isQrTicketRefund() || userPrivilege.isQrTicketReplacement()
//            || userPrivilege.isQrTicketReprint() || userPrivilege.isTvm()){
                return  userPrivilege;
//            }

        }
        throw new UsernameNotFoundException("Incorrect User ID or Password");
    }

    public boolean resumeShift(String password){
        return null != currentUser && currentUser.getPassword().equals(password);
    }


    public void logout() {

        currentUser = null;

    }

    public void pauseShift(){

    }

    public boolean hasRole(String roleName) {
        if (currentUser == null) return false;
        return currentUser.getRoles().stream()
                .anyMatch(role -> role.contains(roleName));
    }
}
