package com.antonromanov.pf.springjpahibernatear.repository;

import com.antonromanov.pf.springjpahibernatear.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Integer>{

}
