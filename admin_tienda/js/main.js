// ===== PRODUCTOS.JS - GESTIÓN DE PRODUCTOS =====

let productosTable;

$(document).ready(function() {
    // Inicializar DataTable
    productosTable = $('#tablaProductos').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/es-ES.json'
        },
        order: [[0, 'desc']],
        pageLength: 10,
        responsive: true,
        buttons: [
            {
                text: '<i class="fas fa-file-excel me-2"></i>Excel',
                className: 'btn btn-success btn-sm',
                action: function(e, dt, node, config) {
                    exportToExcel('tablaProductos', 'productos');
                    trackEvent('Productos', 'Exportar', 'Excel');
                }
            },
            {
                text: '<i class="fas fa-file-pdf me-2"></i>PDF',
                className: 'btn btn-danger btn-sm',
                action: function(e, dt, node, config) {
                    exportToPDF('tablaProductos', 'productos');
                    trackEvent('Productos', 'Exportar', 'PDF');
                }
            }
        ]
    });

    // Resetear formulario cuando se cierra el modal
    $('#modalProducto').on('hidden.bs.modal', function() {
        $('#formProducto')[0].reset();
        $('#productoId').val('');
        $('#modalTitle').text('Nuevo Producto');
        $('.is-invalid').removeClass('is-invalid');
    });

    // Búsqueda en tiempo real
    $('#busquedaProductos').on('keyup', debounce(function() {
        productosTable.search($(this).val()).draw();
    }, 500));

    // Filtro por categoría
    $('#filtroCategoria').on('change', function() {
        const categoria = $(this).val();
        if (categoria) {
            productosTable.column(3).search(categoria).draw();
        } else {
            productosTable.column(3).search('').draw();
        }
    });

    // Filtro por estado
    $('#filtroEstado').on('change', function() {
        const estado = $(this).val();
        if (estado === 'activo') {
            productosTable.column(6).search('Activo').draw();
        } else if (estado === 'inactivo') {
            productosTable.column(6).search('Inactivo').draw();
        } else {
            productosTable.column(6).search('').draw();
        }
    });
});

// ===== CRUD DE PRODUCTOS =====

/**
 * Guardar producto (Crear/Actualizar)
 */
$('#formProducto').on('submit', async function(e) {
    e.preventDefault();
    
    if (!validateForm('formProducto')) {
        showToast('Por favor complete todos los campos requeridos', 'warning');
        return;
    }
    
    const formData = new FormData(this);
    const idProducto = $('#productoId').val();
    const method = idProducto ? 'PUT' : 'POST';
    const url = idProducto ? `api/productos.php?id=${idProducto}` : 'api/productos.php';
    
    try {
        const response = await fetch(url, {
            method: method,
            body: formData
        });
        
        const result = await response.json();
        
        if (response.ok) {
            $('#modalProducto').modal('hide');
            
            const alert = await Swal.fire({
                icon: 'success',
                title: idProducto ? '¡Actualizado!' : '¡Creado!',
                text: idProducto ? 'Producto actualizado exitosamente' : 'Producto creado exitosamente',
                timer: 2000,
                showConfirmButton: false
            });
            
            location.reload();
            trackEvent('Productos', idProducto ? 'Actualizar' : 'Crear', result.id_producto);
        } else {
            throw new Error(result.message || 'Error al guardar');
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: error.message || 'Ocurrió un error al guardar el producto'
        });
    }
});

/**
 * Editar producto
 */
function editarProducto(producto) {
    $('#modalTitle').text('Editar Producto');
    $('#productoId').val(producto.id_producto);
    $('#nombre').val(producto.nombre);
    $('#descripcion').val(producto.descripcion);
    $('#precio').val(producto.precio);
    $('#stock').val(producto.stock);
    $('#id_categoria').val(producto.id_categoria);
    $('#imagen_url').val(producto.imagen_url);
    
    // Preview de imagen
    if (producto.imagen_url) {
        $('#imagenPreview').attr('src', producto.imagen_url).show();
    } else {
        $('#imagenPreview').hide();
    }
    
    $('#modalProducto').modal('show');
    trackEvent('Productos', 'Editar', producto.id_producto);
}

/**
 * Deshabilitar producto
 */
async function deshabilitarProducto(id) {
    try {
        const result = await Swal.fire({
            title: '¿Deshabilitar producto?',
            text: 'El producto no estará visible para los clientes',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#667eea',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, deshabilitar',
            cancelButtonText: 'Cancelar'
        });

        if (result.isConfirmed) {
            const response = await fetch(`api/productos.php?id=${id}&action=disable`, {
                method: 'PUT'
            });
            
            const data = await response.json();
            
            if (response.ok) {
                Swal.fire(
                    '¡Deshabilitado!',
                    'El producto ha sido deshabilitado',
                    'success'
                ).then(() => {
                    location.reload();
                });
                trackEvent('Productos', 'Deshabilitar', id);
            } else {
                throw new Error(data.message);
            }
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.fire(
            'Error',
            'No se pudo deshabilitar el producto',
            'error'
        );
    }
}

/**
 * Habilitar producto
 */
async function habilitarProducto(id) {
    try {
        const result = await Swal.fire({
            title: '¿Habilitar producto?',
            text: 'El producto estará visible para los clientes',
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#667eea',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, habilitar',
            cancelButtonText: 'Cancelar'
        });

        if (result.isConfirmed) {
            const response = await fetch(`api/productos.php?id=${id}&action=enable`, {
                method: 'PUT'
            });
            
            const data = await response.json();
            
            if (response.ok) {
                Swal.fire(
                    '¡Habilitado!',
                    'El producto ha sido habilitado',
                    'success'
                ).then(() => {
                    location.reload();
                });
                trackEvent('Productos', 'Habilitar', id);
            } else {
                throw new Error(data.message);
            }
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.fire(
            'Error',
            'No se pudo habilitar el producto',
            'error'
        );
    }
}

/**
 * Eliminar producto (físicamente - solo admin)
 */
async function eliminarProducto(id) {
    try {
        const result = await Swal.fire({
            title: '¿Eliminar producto?',
            text: 'Esta acción no se puede deshacer',
            icon: 'error',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        });

        if (result.isConfirmed) {
            const response = await fetch(`api/productos.php?id=${id}`, {
                method: 'DELETE'
            });
            
            const data = await response.json();
            
            if (response.ok) {
                Swal.fire(
                    '¡Eliminado!',
                    'El producto ha sido eliminado',
                    'success'
                ).then(() => {
                    location.reload();
                });
                trackEvent('Productos', 'Eliminar', id);
            } else {
                throw new Error(data.message);
            }
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.fire(
            'Error',
            'No se pudo eliminar el producto',
            'error'
        );
    }
}

/**
 * Preview de imagen al cambiar URL
 */
$('#imagen_url').on('change', function() {
    const url = $(this).val();
    if (url) {
        $('#imagenPreview').attr('src', url).show();
    } else {
        $('#imagenPreview').hide();
    }
});

/**
 * Actualizar stock rápidamente
 */
async function actualizarStock(id, nuevoStock) {
    try {
        const response = await fetch(`api/productos.php?id=${id}&action=stock`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ stock: nuevoStock })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showToast('Stock actualizado correctamente', 'success');
            trackEvent('Productos', 'ActualizarStock', id);
        } else {
            throw new Error(data.message);
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error al actualizar stock', 'error');
    }
}

/**
 * Cargar productos con filtros
 */
async function cargarProductos(filtros = {}) {
    try {
        let url = 'api/productos.php';
        const params = new URLSearchParams(filtros);
        
        if (params.toString()) {
            url += '?' + params.toString();
        }
        
        const response = await fetch(url);
        const productos = await response.json();
        
        productosTable.clear();
        
        productos.forEach(producto => {
            productosTable.row.add([
                producto.id_producto,
                `<img src="${producto.imagen_url || 'assets/img/no-image.png'}" class="avatar-sm" onerror="this.src='assets/img/no-image.png'">`,
                producto.nombre,
                producto.categoria_nombre,
                formatCurrency(producto.precio),
                `<span class="badge bg-${producto.stock > 0 ? 'success' : 'danger'}">${producto.stock}</span>`,
                `<span class="badge bg-${producto.estado ? 'success' : 'secondary'}">${producto.estado ? 'Activo' : 'Inactivo'}</span>`,
                `
                <button class="btn btn-sm btn-info" onclick="editarProducto(${JSON.stringify(producto).replace(/"/g, '&quot;')})">
                    <i class="fas fa-edit"></i>
                </button>
                ${producto.estado ? 
                    `<button class="btn btn-sm btn-warning" onclick="deshabilitarProducto(${producto.id_producto})">
                        <i class="fas fa-ban"></i>
                    </button>` : 
                    `<button class="btn btn-sm btn-success" onclick="habilitarProducto(${producto.id_producto})">
                        <i class="fas fa-check"></i>
                    </button>`
                }
                <button class="btn btn-sm btn-danger" onclick="eliminarProducto(${producto.id_producto})">
                    <i class="fas fa-trash"></i>
                </button>
                `
            ]);
        });
        
        productosTable.draw();
        
    } catch (error) {
        console.error('Error cargando productos:', error);
        showToast('Error al cargar productos', 'error');
    }
}

// Cargar productos al iniciar
$(document).ready(function() {
    cargarProductos();
});