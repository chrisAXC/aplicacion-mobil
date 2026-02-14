const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

router.post('/admin/login', authController.loginAdmin);
router.post('/usuario/register', authController.registerUsuario);
router.post('/usuario/login', authController.loginUsuario);

module.exports = router;