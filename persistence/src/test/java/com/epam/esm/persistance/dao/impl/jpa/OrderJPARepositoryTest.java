package com.epam.esm.persistance.dao.impl.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.esm.persistance.config.EmbeddedDatabaseJpaConfig;
import com.epam.esm.persistance.entity.Order;
import com.epam.esm.persistance.entity.User;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmbeddedDatabaseJpaConfig.class})
@ActiveProfiles("integration-test")
@Transactional
class OrderJPARepositoryTest {

  @PersistenceContext
  EntityManager entityManager;

  OrderJpaRepository orderJpaRepository;

  @BeforeEach
  void setUp() {
    orderJpaRepository = new OrderJpaRepository(entityManager);
  }

  private User getUser() {
    return new User(1L, "test@gmail.com", "passwordhash", "Tester", "Test", Instant.now(),
        "test@gmail.com", Instant.now());
  }

  @Test
  void saveShouldInsertEntryWithGeneratedIdWhenEntryHasIdNull() {
    User creator = getUser();
    Order order = new Order(null, creator, BigDecimal.TEN, Set.of(),Instant.now(),creator.getEmail(),Instant.now());

    Order saved = orderJpaRepository.save(order);

    assertTrue(orderJpaRepository.findById(saved.getId()).isPresent());
  }

  @Test
  void saveShouldUpdateEntryWithNewFirstNameWhenEntryExist() {
    BigDecimal targetPrice = BigDecimal.ONE;
    Order order = orderJpaRepository.findById(1L).orElseThrow();

    order.setTotalPrice(targetPrice);

    orderJpaRepository.save(order);

    assertEquals(targetPrice, orderJpaRepository.findById(1L).orElseThrow().getTotalPrice());
  }

  @Test
  void findByIdShouldReturnPresentOptionalWhenByExistId() {
    assertTrue(orderJpaRepository.findById(1L).isPresent());
  }

  @Test
  void findByIdShouldReturnEmptyOptionalWhenByNonExistId() {
    assertTrue(orderJpaRepository.findById(-404L).isEmpty());
  }

  @Test
  void existsByIdShouldReturnTrueWhenByExistId() {
    assertTrue(orderJpaRepository.existsById(1L));
  }

  @Test
  void existsByIdShouldReturnFalseWhenByNonExistId() {
    assertFalse(orderJpaRepository.existsById(-404L));
  }

  @Test
  void findAllAsListShouldReturnNonEmptyListWhenCertificatesEntryExists() {
    assertFalse(orderJpaRepository.findAllAsList().isEmpty());
  }

  @Test
  void deleteShouldDeleteEntryWhenEntryExists() {
    Order order = orderJpaRepository.findById(1L).orElseThrow();

    orderJpaRepository.delete(order);

    assertFalse(orderJpaRepository.existsById(1L));
  }

  @Test
  void deleteShouldNotDeleteAllEntryWhenEntryExists() {
    Order order = orderJpaRepository.findById(1L).orElseThrow();

    orderJpaRepository.delete(order);

    assertFalse(orderJpaRepository.findAllAsList().isEmpty());
  }
}