package xyz.acevedosharp.persistence_layer.repository_services

import xyz.acevedosharp.entities.PedidoDB
import xyz.acevedosharp.persistence_layer.repositories.PedidoRepo
import xyz.acevedosharp.persistence_layer.repository_services.interfaces.NonEditableRepoService
import org.springframework.stereotype.Service

@Service
class PedidoService(val repo: PedidoRepo):
    NonEditableRepoService<PedidoDB> {
    override fun all(): List<PedidoDB> = repo.findAll()

    override fun add(item: PedidoDB): PedidoDB = repo.save(item)
}