package xyz.acevedosharp.persistence.entities

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "item_venta", schema = "epic")
class ItemVentaDB(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_venta_id")
    var itemVentaId: Int? = null,

    @Column(name = "fecha_hora")
    var fechaHora: Timestamp,

    @Column(name = "cantidad")
    var cantidad: Int,

    @Column(name = "precio_venta")
    var precioVenta: Int,

    @ManyToOne @JoinColumn(name = "producto")
    var producto: ProductoDB,

    @ManyToOne @JoinColumn(name = "venta")
    var venta: VentaDB,

    @ManyToOne @JoinColumn(name = "cliente")
    var cliente: ClienteDB,
)
