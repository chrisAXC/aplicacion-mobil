<?php
require_once 'includes/functions.php';
redirectIfNotLoggedIn();

// Obtener productos
$productos = apiRequest('/productos', 'GET', null, $_SESSION['admin_token']);
$categorias = apiRequest('/productos/categorias', 'GET', null, $_SESSION['admin_token']);
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Productos - <?php echo SITE_NAME; ?></title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- DataTables -->
    <link href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <style>
        .sidebar {
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        .sidebar .nav-link {
            color: rgba(255,255,255,0.8);
            padding: 15px 20px;
            margin: 5px 0;
            border-radius: 10px;
            transition: all 0.3s;
        }
        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            color: white;
            background: rgba(255,255,255,0.1);
            transform: translateX(5px);
        }
        .sidebar .nav-link i {
            width: 25px;
            margin-right: 10px;
        }
        .main-content {
            padding: 30px;
            background: #f8f9fa;
        }
        .btn-gradient-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
        }
        .btn-gradient-primary:hover {
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102,126,234,0.4);
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 px-0 sidebar">
                <div class="p-4">
                    <h4 class="text-white mb-4">
                        <i class="fas fa-laptop me-2"></i>Tienda Admin
                    </h4>
                    <hr class="bg-white">
                    <div class="mt-4">
                        <img src="https://ui-avatars.com/api/?name=<?php echo urlencode($_SESSION['admin_name']); ?>&background=random&size=64" 
                             class="rounded-circle mb-3" alt="Admin">
                        <h6 class="text-white mb-1"><?php echo $_SESSION['admin_name']; ?></h6>
                        <small class="text-white-50">Administrador</small>
                    </div>
                </div>
                
                <nav class="nav flex-column mt-4">
                    <a class="nav-link" href="dashboard.php">
                        <i class="fas fa-tachometer-alt"></i>Dashboard
                    </a>
                    <a class="nav-link" href="ventas.php">
                        <i class="fas fa-shopping-cart"></i>Ventas
                    </a>
                    <a class="nav-link active" href="productos.php">
                        <i class="fas fa-boxes"></i>Productos
                    </a>
                    <a class="nav-link" href="#" onclick="document.getElementById('logout-form').submit()">
                        <i class="fas fa-sign-out-alt"></i>Cerrar Sesión
                    </a>
                </nav>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>Gestión de Productos</h2>
                    <button class="btn btn-gradient-primary" data-bs-toggle="modal" data-bs-target="#modalProducto">
                        <i class="fas fa-plus me-2"></i>Nuevo Producto
                    </button>
                </div>
                
                <!-- Tabla de Productos -->
                <div class="card">
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="tablaProductos" class="table table-hover">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Imagen</th>
                                        <th>Nombre</th>
                                        <th>Categoría</th>
                                        <th>Precio</th>
                                        <th>Stock</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php foreach ($productos['data'] ?? [] as $producto): ?>
                                    <tr>
                                        <td>#<?php echo $producto['id_producto']; ?></td>
                                        <td>
                                            <img src="<?php echo $producto['imagen_url'] ?? 'https://via.placeholder.com/50'; ?>" 
                                                 class="img-thumbnail" style="width: 50px; height: 50px; object-fit: cover;">
                                        </td>
                                        <td><?php echo $producto['nombre']; ?></td>
                                        <td><?php echo $producto['categoria_nombre']; ?></td>
                                        <td><?php echo formatCurrency($producto['precio']); ?></td>
                                        <td>
                                            <span class="badge bg-<?php echo $producto['stock'] > 0 ? 'success' : 'danger'; ?>">
                                                <?php echo $producto['stock']; ?>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge bg-<?php echo $producto['estado'] ? 'success' : 'secondary'; ?>">
                                                <?php echo $producto['estado'] ? 'Activo' : 'Inactivo'; ?>
                                            </span>
                                        </td>
                                        <td>
                                            <button class="btn btn-sm btn-info" onclick="editarProducto(<?php echo htmlspecialchars(json_encode($producto)); ?>)">
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <?php if ($producto['estado']): ?>
                                                <button class="btn btn-sm btn-warning" onclick="deshabilitarProducto(<?php echo $producto['id_producto']; ?>)">
                                                    <i class="fas fa-ban"></i>
                                                </button>
                                            <?php else: ?>
                                                <button class="btn btn-sm btn-success" onclick="habilitarProducto(<?php echo $producto['id_producto']; ?>)">
                                                    <i class="fas fa-check"></i>
                                                </button>
                                            <?php endif; ?>
                                        </td>
                                    </tr>
                                    <?php endforeach; ?>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Modal Producto -->
    <div class="modal fade" id="modalProducto" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitle">Nuevo Producto</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form id="formProducto" method="POST">
                    <input type="hidden" id="productoId" name="id_producto">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="nombre" class="form-label">Nombre *</label>
                                <input type="text" class="form-control" id="nombre" name="nombre" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="id_categoria" class="form-label">Categoría *</label>
                                <select class="form-select" id="id_categoria" name="id_categoria" required>
                                    <option value="">Seleccione...</option>
                                    <?php foreach ($categorias['data'] ?? [] as $categoria): ?>
                                        <option value="<?php echo $categoria['id_categoria']; ?>">
                                            <?php echo $categoria['nombre']; ?>
                                        </option>
                                    <?php endforeach; ?>
                                </select>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="descripcion" class="form-label">Descripción</label>
                            <textarea class="form-control" id="descripcion" name="descripcion" rows="3"></textarea>
                        </div>
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label for="precio" class="form-label">Precio *</label>
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <input type="number" class="form-control" id="precio" name="precio" step="0.01" required>
                                </div>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label for="stock" class="form-label">Stock *</label>
                                <input type="number" class="form-control" id="stock" name="stock" required>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label for="imagen_url" class="form-label">URL de Imagen</label>
                                <input type="url" class="form-control" id="imagen_url" name="imagen_url">
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-gradient-primary">Guardar</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <form id="logout-form" method="POST" action="includes/auth.php" style="display: none;">
        <input type="hidden" name="action" value="logout">
    </form>
    
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>
    <script src="js/productos.js"></script>
</body>
</html>