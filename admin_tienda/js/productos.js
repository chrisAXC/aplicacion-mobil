$(document).ready(function() {
    // Inicializar DataTable
    $('#tablaProductos').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/es-ES.json'
        }
    });
});

// Función para editar producto
function editarProducto(producto) {
    $('#modalTitle').text('Editar Producto');
    $('#productoId').val(producto.id_producto);
    $('#nombre').val(producto.nombre);
    $('#descripcion').val(producto.descripcion);
    $('#precio').val(producto.precio);
    $('#stock').val(producto.stock);
    $('#id_categoria').val(producto.id_categoria);
    $('#imagen_url').val(producto.imagen_url);
    
    $('#modalProducto').modal('show');
}

// Función para deshabilitar producto
function deshabilitarProducto(id) {
    Swal.fire({
        title: '¿Deshabilitar producto?',
        text: 'El producto no estará visible para los clientes',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, deshabilitar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: 'api/productos.php',
                method: 'PUT',
                data: {
                    action: 'disable',
                    id: id
                },
                success: function(response) {
                    Swal.fire(
                        'Deshabilitado!',
                        'El producto ha sido deshabilitado.',
                        'success'
                    ).then(() => {
                        location.reload();
                    });
                }
            });
        }
    });
}

// Función para habilitar producto
function habilitarProducto(id) {
    Swal.fire({
        title: '¿Habilitar producto?',
        text: 'El producto estará visible para los clientes',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, habilitar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: 'api/productos.php',
                method: 'PUT',
                data: {
                    action: 'enable',
                    id: id
                },
                success: function(response) {
                    Swal.fire(
                        'Habilitado!',
                        'El producto ha sido habilitado.',
                        'success'
                    ).then(() => {
                        location.reload();
                    });
                }
            });
        }
    });
}

// Manejar envío del formulario
$('#formProducto').on('submit', function(e) {
    e.preventDefault();
    
    const formData = $(this).serialize();
    const idProducto = $('#productoId').val();
    const method = idProducto ? 'PUT' : 'POST';
    
    $.ajax({
        url: 'api/productos.php',
        method: method,
        data: formData,
        success: function(response) {
            $('#modalProducto').modal('hide');
            Swal.fire(
                idProducto ? 'Actualizado!' : 'Creado!',
                idProducto ? 'Producto actualizado exitosamente' : 'Producto creado exitosamente',
                'success'
            ).then(() => {
                location.reload();
            });
        },
        error: function(xhr) {
            Swal.fire(
                'Error!',
                'Ocurrió un error al guardar el producto',
                'error'
            );
        }
    });
});