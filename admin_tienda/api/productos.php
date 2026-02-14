<?php
require_once '../includes/config.php';
require_once '../includes/functions.php';

header('Content-Type: application/json');

// Verificar autenticación
if (!isset($_SESSION['admin_token'])) {
    http_response_code(401);
    echo json_encode(['error' => 'No autorizado']);
    exit;
}

$method = $_SERVER['REQUEST_METHOD'];
$token = $_SESSION['admin_token'];

switch ($method) {
    case 'GET':
        if (isset($_GET['id'])) {
            $response = apiRequest("/productos/{$_GET['id']}", 'GET', null, $token);
        } else {
            $response = apiRequest('/productos', 'GET', null, $token);
        }
        break;
        
    case 'POST':
        $data = [
            'nombre' => $_POST['nombre'],
            'descripcion' => $_POST['descripcion'] ?? '',
            'precio' => $_POST['precio'],
            'stock' => $_POST['stock'],
            'id_categoria' => $_POST['id_categoria'],
            'imagen_url' => $_POST['imagen_url'] ?? ''
        ];
        $response = apiRequest('/productos', 'POST', $data, $token);
        break;
        
    case 'PUT':
        parse_str(file_get_contents("php://input"), $putData);
        $id = $_GET['id'] ?? null;
        $action = $_GET['action'] ?? null;
        
        if ($action === 'disable') {
            $response = apiRequest("/productos/$id/disable", 'PUT', null, $token);
        } elseif ($action === 'enable') {
            $response = apiRequest("/productos/$id/enable", 'PUT', null, $token);
        } elseif ($action === 'stock') {
            $jsonData = json_decode(file_get_contents("php://input"), true);
            $response = apiRequest("/productos/$id", 'PUT', ['stock' => $jsonData['stock']], $token);
        } else {
            $data = [
                'nombre' => $putData['nombre'],
                'descripcion' => $putData['descripcion'] ?? '',
                'precio' => $putData['precio'],
                'stock' => $putData['stock'],
                'id_categoria' => $putData['id_categoria'],
                'imagen_url' => $putData['imagen_url'] ?? ''
            ];
            $response = apiRequest("/productos/$id", 'PUT', $data, $token);
        }
        break;
        
    case 'DELETE':
        $id = $_GET['id'] ?? null;
        $response = apiRequest("/productos/$id", 'DELETE', null, $token);
        break;
        
    default:
        http_response_code(405);
        $response = ['error' => 'Método no permitido'];
        break;
}

http_response_code($response['code']);
echo json_encode($response['data']);
?>