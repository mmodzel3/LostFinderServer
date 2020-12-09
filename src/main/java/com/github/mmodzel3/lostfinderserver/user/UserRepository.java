package com.github.mmodzel3.lostfinderserver.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface UserRepository extends MongoRepository<User, String> {
    public Optional<User> findByEmail(String email);
}
