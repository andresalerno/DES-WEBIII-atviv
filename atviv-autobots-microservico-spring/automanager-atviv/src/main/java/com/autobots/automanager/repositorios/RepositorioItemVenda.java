package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobots.automanager.entidades.ItemVenda;

public interface RepositorioItemVenda extends JpaRepository<ItemVenda, Long> {
}
