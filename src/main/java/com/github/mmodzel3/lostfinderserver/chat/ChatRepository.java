package com.github.mmodzel3.lostfinderserver.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ChatRepository extends MongoRepository<ChatMessage, String> {
    Page<ChatMessage> findAll(Pageable pageable);
}
