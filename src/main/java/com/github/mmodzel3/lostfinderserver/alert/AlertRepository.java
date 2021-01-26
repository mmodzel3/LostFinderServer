package com.github.mmodzel3.lostfinderserver.alert;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface AlertRepository extends MongoRepository<Alert, String> {
    List<Alert> findAllByEndDateNull();
}
