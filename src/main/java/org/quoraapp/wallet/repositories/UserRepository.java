package org.quoraapp.wallet.repositories;

import java.util.List;

import org.quoraapp.wallet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> getUserByName(String name);
}