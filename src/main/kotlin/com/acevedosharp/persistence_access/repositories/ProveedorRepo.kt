package com.acevedosharp.persistence_access.repositories

import com.acevedosharp.entities.ProveedorDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProveedorRepo: JpaRepository<ProveedorDB, Int>