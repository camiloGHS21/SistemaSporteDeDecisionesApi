package com.example.demo.domain.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InformePaisComparacionRepository extends JpaRepository<InformePaisComparacion, Integer> {

}
