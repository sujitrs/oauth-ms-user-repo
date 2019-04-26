package org.sj.msuserrepo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStoreRepo extends JpaRepository<UserRepo, UUID> {

}
