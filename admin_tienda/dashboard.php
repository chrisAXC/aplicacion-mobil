<?php
require_once 'includes/functions.php';
redirectIfNotLoggedIn();

// Obtener estadísticas de la API
$stats = [
    'productos' => apiRequest('/productos', 'GET', null, $_SESSION['admin_token']),
    'ventas' => apiRequest('/ventas', 'GET', null, $_SESSION['admin_token']),
    'historial' => [] // Aquí irían los datos del historial
];
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - <?php echo SITE_NAME; ?></title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
        .sidebar .nav-link:hover {
            color: white;
            background: rgba(255,255,255,0.1);
            transform: translateX(5px);
        }
        .sidebar .nav-link.active {
            background: rgba(255,255,255,0.2);
            color: white;
        }
        .sidebar .nav-link i {
            width: 25px;
            margin-right: 10px;
        }
        .main-content {
            padding: 30px;
            background: #f8f9fa;
        }
        .stat-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.05);
            transition: transform 0.3s;
        }
        .stat-card:hover {
            transform: translateY(-5px);
        }
        .stat-icon {
            width: 60px;
            height: 60px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin-bottom: 15px;
        }
        .bg-gradient-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        .bg-gradient-success {
            background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
            color: white;
        }
        .bg-gradient-warning {
            background: linear-gradient(135deg, #fad0c4 0%, #ffd1ff 100%);
            color: white;
        }
        .activity-timeline {
            position: relative;
            padding-left: 40px;
        }
        .timeline-item {
            position: relative;
            padding-bottom: 30px;
        }
        .timeline-icon {
            position: absolute;
            left: -40px;
            top: 0;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background: white;
            border: 3px solid #667eea;
            display: flex;
            align-items: center;
            justify-content: center;
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
                    <a class="nav-link active" href="dashboard.php">
                        <i class="fas fa-tachometer-alt"></i>Dashboard
                    </a>
                    <a class="nav-link" href="ventas.php">
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
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>Dashboard</h2>
                    <div class="text-muted">
                        <i class="fas fa-calendar me-2"></i><?php echo date('d/m/Y'); ?>
                    </div>
                </div>
                
                <!-- Stats Cards -->
                <div class="row mb-4">
                    <div class="col-md-4 mb-3">
                        <div class="stat-card">
                            <div class="stat-icon bg-gradient-primary">
                                <i class="fas fa-boxes"></i>
                            </div>
                            <h3 class="mb-1"><?php echo count($stats['productos']['data'] ?? []); ?></h3>
                            <p class="text-muted mb-0">Productos</p>
                        </div>
                    </div>
                    <div class="col-md-4 mb-3">
                        <div class="stat-card">
                            <div class="stat-icon bg-gradient-success">
                                <i class="fas fa-shopping-cart"></i>
                            </div>
                            <h3 class="mb-1"><?php echo count($stats['ventas']['data'] ?? []); ?></h3>
                            <p class="text-muted mb-0">Ventas Totales</p>
                        </div>
                    </div>
                    <div class="col-md-4 mb-3">
                        <div class="stat-card">
                            <div class="stat-icon bg-gradient-warning">
                                <i class="fas fa-dollar-sign"></i>
                            </div>
                            <h3 class="mb-1"><?php 
                                $total = array_sum(array_column($stats['ventas']['data'] ?? [], 'total'));
                                echo formatCurrency($total);
                            ?></h3>
                            <p class="text-muted mb-0">Ingresos Totales</p>
                        </div>
                    </div>
                </div>
                
                <div class="row">
                    <!-- Ventas Recientes -->
                    <div class="col-md-8 mb-4">
                        <div class="card">
                            <div class="card-header bg-white d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">
                                    <i class="fas fa-history me-2 text-primary"></i>
                                    Ventas Recientes
                                </h5>
                                <a href="ventas.php" class="btn btn-sm btn-outline-primary">Ver todas</a>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th># Venta</th>
                                                <th>Cliente</th>
                                                <th>Fecha</th>
                                                <th>Total</th>
                                                <th>Estado</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <?php 
                                            $ventasRecientes = array_slice($stats['ventas']['data'] ?? [], 0, 5);
                                            foreach ($ventasRecientes as $venta): 
                                            ?>
                                            <tr>
                                                <td>#<?php echo $venta['id_venta']; ?></td>
                                                <td><?php echo $venta['usuario_nombre']; ?></td>
                                                <td><?php echo formatDate($venta['fecha_venta']); ?></td>
                                                <td><?php echo formatCurrency($venta['total']); ?></td>
                                                <td>
                                                    <span class="badge bg-success">Completada</span>
                                                </td>
                                            </tr>
                                            <?php endforeach; ?>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Actividad Reciente -->
                    <div class="col-md-4 mb-4">
                        <div class="card">
                            <div class="card-header bg-white">
                                <h5 class="mb-0">
                                    <i class="fas fa-clock me-2 text-primary"></i>
                                    Actividad Reciente
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="activity-timeline">
                                    <div class="timeline-item">
                                        <div class="timeline-icon bg-primary">
                                            <i class="fas fa-plus text-white small"></i>
                                        </div>
                                        <small class="text-muted">Hace 5 min</small>
                                        <p class="mb-0">Nuevo producto agregado</p>
                                    </div>
                                    <div class="timeline-item">
                                        <div class="timeline-icon bg-success">
                                            <i class="fas fa-shopping-cart text-white small"></i>
                                        </div>
                                        <small class="text-muted">Hace 15 min</small>
                                        <p class="mb-0">Venta #1023 completada</p>
                                    </div>
                                    <div class="timeline-item">
                                        <div class="timeline-icon bg-warning">
                                            <i class="fas fa-ban text-white small"></i>
                                        </div>
                                        <small class="text-muted">Hace 30 min</small>
                                        <p class="mb-0">Producto deshabilitado</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <form id="logout-form" method="POST" action="includes/auth.php" style="display: none;">
        <input type="hidden" name="action" value="logout">
    </form>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>