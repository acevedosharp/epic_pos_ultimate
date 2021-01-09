package xyz.acevedosharp.persistence_layer.entities

import javax.persistence.*

@Entity
@Table(name = "proveedor", schema = "epic")
class ProveedorDB(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proveedor_id")
    var proveedorId: Int? = null,

    @Column(name = "nombre")
    var nombre: String,

    @Column(name = "telefono")
    var telefono: String,

    @Column(name = "correo")
    var correo: String?,

    @Column(name = "direccion")
    var direccion: String?,
)
