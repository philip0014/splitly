package com.example.splitly.repository;

import com.example.splitly.model.BillStatus;
import com.example.splitly.model.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends MongoRepository<Bill, String> {

    Optional<Bill> findByIdAndStatus(String id, BillStatus status);

    List<Bill> findByIdInAndStatus(Collection<String> id, BillStatus status);

    Page<Bill> findByIdInAndStatus(Collection<String> id, BillStatus status, Pageable pageable);

}
