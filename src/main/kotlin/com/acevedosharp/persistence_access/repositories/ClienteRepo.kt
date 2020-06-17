package com.acevedosharp.persistence_access.repositories

import com.acevedosharp.entities.ClienteDB
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClienteRepo: JpaRepository<ClienteDB, Int>