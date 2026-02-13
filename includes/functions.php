<?php
require_once 'config.php';

function apiRequest($endpoint, $method = 'GET', $data = null, $token = null) {
    $url = API_URL . $endpoint;
    
    $headers = [
        'Content-Type: application/json'
    ];
    
    if ($token) {
        $headers[] = 'Authorization: Bearer ' . $token;
    }
    
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    
    if ($data) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    }
    
    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);
    
    return [
        'code' => $httpCode,
        'data' => json_decode($response, true)
    ];
}

function isLoggedIn() {
    return isset($_SESSION['admin_id']) && isset($_SESSION['admin_token']);
}

function redirectIfNotLoggedIn() {
    if (!isLoggedIn()) {
        header('Location: login.php');
        exit;
    }
}

function formatDate($date) {
    return date('d/m/Y H:i', strtotime($date));
}

function formatCurrency($amount) {
    return '$' . number_format($amount, 2);
}
?>