package com.my.commandservice.repository;

import com.my.commandservice.entity.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, UUID> {
}
