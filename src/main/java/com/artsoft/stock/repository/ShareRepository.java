package com.artsoft.stock.repository;

import com.artsoft.stock.entity.Share;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
}
