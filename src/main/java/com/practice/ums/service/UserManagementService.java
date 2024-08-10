package com.practice.ums.service;
import com.practice.ums.dto.RequestResponse;
import com.practice.ums.entity.OurUsers;
import com.practice.ums.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserManagementService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    public RequestResponse register(RequestResponse registerRequest) {
        RequestResponse response = new RequestResponse();
        try {
            OurUsers ourUser = new OurUsers();
            ourUser.setEmail(registerRequest.getEmail());
            ourUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            ourUser.setCity(registerRequest.getCity());
            ourUser.setName(registerRequest.getName());
            ourUser.setRole(registerRequest.getRole());
           OurUsers userResult= userRepository.save(ourUser);
            if(userResult.getId()>0) {
                response.setOurUsers(userResult);
                response.setMessage("User Saved Successfully");
                response.setStatusCode(200);
            }
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public RequestResponse login(RequestResponse loginRequest) {
        RequestResponse response = new RequestResponse();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            var user=userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt=jwtUtils.generateToken(user);
            var refreshToken =jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hours");
            response.setMessage("Successfully logged In");
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());

        }
        return response;
    }

    public RequestResponse refreshToken(RequestResponse refreshRequest) {
        RequestResponse response = new RequestResponse();
        try{
            String ourEmail= jwtUtils.extractUsername(refreshRequest.getRefreshToken());
            OurUsers ourUser = userRepository.findByEmail(ourEmail).orElseThrow();
            if(jwtUtils.isTokenValid(refreshRequest.getToken(),ourUser)){
                var jwt= jwtUtils.generateToken(ourUser);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshRequest.getToken());
                response.setExpirationTime("24Hours");
                response.setMessage("Successfully Refreshed");
            }
            response.setStatusCode(200);
            return response;
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }

    }

    public RequestResponse getAllUsers(){
        RequestResponse response = new RequestResponse();
        try {
            List<OurUsers> result = userRepository.findAll();
            if(!result.isEmpty()){
                response.setOurUsersList(result);
                response.setStatusCode(200);
                response.setMessage("Successfully Fetched All Users");
            }else{
                response.setStatusCode(404);
                response.setMessage("User Not Found");
            }
            return response;
        }catch(Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred:"+ e.getMessage());
            return response;
        }

    }

    public RequestResponse getUserById(Integer id){
        RequestResponse response = new RequestResponse();
        try{
            OurUsers userById = userRepository.findById(id).orElseThrow(
                    () -> new RuntimeException("User Not Found")
            );
            response.setOurUsers(userById);
            response.setStatusCode(200);
            response.setMessage("User with id "+id+" found successfully");

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred:"+ e.getMessage());
        }
        return response;
    }

    public RequestResponse deleteUserById(Integer id){
        RequestResponse response = new RequestResponse();
        try{
            Optional<OurUsers> userById = userRepository.findById(id);
            if(userById.isPresent()){
                userRepository.deleteById(id);
                response.setStatusCode(200);
                response.setMessage("User Deleted Successfully");
            }else{
                response.setStatusCode(404);
                response.setMessage("User Not Found for Deletion");
            }
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting user:"+ e.getMessage());
        }
        return response;
    }

    public RequestResponse updateUserById(Integer id, OurUsers updateRequest) {
        RequestResponse response = new RequestResponse();
        try{
            Optional<OurUsers> userById = userRepository.findById(id);
            if(userById.isPresent()){
                OurUsers existUser = userById.get();
                existUser.setEmail(updateRequest.getEmail());
                existUser.setName(updateRequest.getName());
                existUser.setRole(updateRequest.getRole());
                existUser.setCity(updateRequest.getCity());

                if(updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()){
                    existUser.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
                }
                OurUsers userResult = userRepository.save(existUser);
                response.setOurUsers(userResult);
                response.setStatusCode(200);
                response.setMessage("User Updated Successfully");
            }else{
                response.setStatusCode(404);
                response.setMessage("User Not Found for Update");
            }
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating user:"+ e.getMessage());
        }
        return response;
    }

    public RequestResponse getMyInfo(String email){
        RequestResponse response = new RequestResponse();
        try{
            Optional<OurUsers> usersOptional =userRepository.findByEmail(email);
            if(usersOptional.isPresent()){
                response.setOurUsers(usersOptional.get());
                response.setStatusCode(200);
                response.setMessage("Successfully Fetched My Info");
            }else{
                response.setStatusCode(404);
                response.setMessage("User Not Found");
            }

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }


}
