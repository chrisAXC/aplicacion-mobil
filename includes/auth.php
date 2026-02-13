<?php
require_once 'functions.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';
    
    if ($action === 'login') {
        $email = $_POST['email'] ?? '';
        $password = $_POST['password'] ?? '';
        
        $response = apiRequest('/auth/admin/login', 'POST', [
            'email' => $email,
            'password' => $password
        ]);
        
        if ($response['code'] === 200 && isset($response['data']['token'])) {
            $_SESSION['admin_id'] = $response['data']['usuario']['id'];
            $_SESSION['admin_name'] = $response['data']['usuario']['nombre'];
            $_SESSION['admin_email'] = $response['data']['usuario']['email'];
            $_SESSION['admin_token'] = $response['data']['token'];
            
            header('Location: dashboard.php');
            exit;
        } else {
            $error = 'Credenciales inválidas';
        }
    }
    
    if ($action === 'logout') {
        session_destroy();
        header('Location: login.php');
        exit;
    }
}
?>