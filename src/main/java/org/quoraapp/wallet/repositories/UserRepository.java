package org.quoraapp.wallet.repositories;

import org.quoraapp.wallet.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}