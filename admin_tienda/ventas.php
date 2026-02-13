<?php
require_once 'includes/functions.php';
redirectIfNotLoggedIn();

// Obtener ventas
$ventas = apiRequest('/ventas', 'GET', null, $_SESSION['admin_token']);
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ventas - <?php echo SITE_NAME; ?></title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- DataTables -->
    <link href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar (mismo que en productos.php) -->
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
                    <a class="nav-link active" href="ventas.php">
                        <i class="fas fa-shopping-cart"></i>Ventas
                    </a>
                    <a class="nav-link" href="productos.php">
                        <i class="fas fa-boxes"></i>Productos
                    </a>
                    <a class="nav-link" href="#" onclick="document.getElementById('logout-form').submit()">
                        <i class="fas fa-sign-out-alt"></i>Cerrar Sesión
                    </a>
                </nav>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content">
                <h2 class="mb-4">Ventas Realizadas</h2>
                
                <div class="card">
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="tablaVentas" class="table table-hover">
                                <thead>
                                    <tr>
                                        <th># Venta</th>
                                        <th>Cliente</th>
                                        <th>Fecha</th>
                                        <th>Total</th>
                                        <th>Método de Pago</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php foreach ($ventas['data'] ?? [] as $venta): ?>
                                    <tr>
                                        <td>#<?php echo $venta['id_venta']; ?></td>
                                        <td><?php echo $venta['usuario_nombre']; ?></td>
                                        <td><?php echo formatDate($venta['fecha_venta']); ?></td>
                                        <td><?php echo formatCurrency($venta['total']); ?></td>
                                        <td><?php echo $venta['metodo_pago']; ?></td>
                                        <td>
                                            <button class="btn btn-sm btn-info" onclick="verDetalle(<?php echo $venta['id_venta']; ?>)">
                                                <i class="fas fa-eye"></i>
                                            </button>
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
    
    <form id="logout-form" method="POST" action="includes/auth.php" style="display: none;">
        <input type="hidden" name="action" value="logout">
    </form>
    
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>
    <script>
        $(document).ready(function() {
            $('#tablaVentas').DataTable({
                language: {
                    url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/es-ES.json'
                }
            });
        });
        
        function verDetalle(id) {
            // Implementar modal con detalles de la venta
            window.location.href = 'detalle_venta.php?id=' + id;
        }
    </script>
</body>
</html>