const express = require('express');
const router = express.Router();
const productoController = require('../controllers/productoController');
const authMiddleware = require('../middlewares/authMiddleware');

// Rutas públicas
router.get('/', productoController.getAll);
router.get('/categorias', productoController.getCategorias);
router.get('/:id', productoController.getById);

// Rutas protegidas para admin
router.post('/', authMiddleware.verifyToken, authMiddleware.verifyAdmin, productoController.create);
router.put('/:id', authMiddleware.verifyToken, authMiddleware.verifyAdmin, productoController.update);
router.put('/:id/disable', authMiddleware.verifyToken, authMiddleware.verifyAdmin, productoController.disable);
router.put('/:id/enable', authMiddleware.verifyToken, authMiddleware.verifyAdmin, productoController.enable);

module.exports = router;