package com.acevedosharp.persistence_layer.repository_services

import com.acevedosharp.entities.ProductoDB
import com.acevedosharp.persistence_layer.repositories.ProductoRepo
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class ProductoService(val repo: ProductoRepo): BaseRepoService<ProductoDB> {

    override fun all(): List<ProductoDB> = repo.findAll()

    override fun add(item: ProductoDB): ProductoDB {
        when {
            repo.existsByCodigo(item.codigo)                     -> throw Exception()
            repo.existsByDescripcionLarga(item.descripcionLarga) -> throw Exception()
            repo.existsByDescripcionCorta(item.descripcionCorta) -> throw Exception()
            else -> return repo.save(item)
        }
    }

    override fun edit(item: ProductoDB): ProductoDB {
        when {
            (repo.existsByCodigo(item.codigo)                    ) && (repo.findByCodigo(item.codigo)                    .productoId != item.productoId) -> throw Exception()
            (repo.existsByDescripcionLarga(item.descripcionLarga)) && (repo.findByDescripcionLarga(item.descripcionLarga).productoId != item.productoId) -> throw Exception()
            (repo.existsByDescripcionCorta(item.descripcionCorta)) && (repo.findByDescripcionCorta(item.descripcionCorta).productoId != item.productoId) -> throw Exception()
            else -> return repo.save(item)
        }
    }
}