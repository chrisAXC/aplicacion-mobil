<?php
require_once 'includes/config.php';

// Redirigir al login si no está autenticado
if (isset($_SESSION['admin_id'])) {
    header('Location: dashboard.php');
    exit;
} else {
    header('Location: login.php');
    exit;
}
?>