package com.rapl.springajax.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rapl.springajax.domain.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
