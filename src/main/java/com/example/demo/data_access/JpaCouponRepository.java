package com.example.demo.data_access;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Coupon;
import com.example.demo.domain.Coupon.ValueType;

import java.util.List;
import java.util.UUID;

public interface JpaCouponRepository extends JpaRepository<Coupon, UUID> {
    List<Coupon> findByName(String name);
    List<Coupon> findByIsSoldFalse();
    List<Coupon> findByValueType(ValueType valueType);
    boolean existsByName(String name);
}