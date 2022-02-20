package com.demo.userMSDemo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.userMSDemo.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

}
